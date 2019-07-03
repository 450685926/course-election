package com.server.edu.election.studentelec.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.RestResult;
import com.server.edu.dictionary.utils.SpringUtils;
import com.server.edu.election.constants.ChooseObj;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.constants.CourseTakeType;
import com.server.edu.election.constants.ElectRuleType;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.dao.ElcLogDao;
import com.server.edu.election.dao.ElecRoundCourseDao;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.dao.TeachingClassDao;
import com.server.edu.election.dto.ClassTeacherDto;
import com.server.edu.election.entity.ElcCourseTake;
import com.server.edu.election.entity.ElcLog;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.entity.Student;
import com.server.edu.election.service.ElectionApplyService;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.CompletedCourse;
import com.server.edu.election.studentelec.context.ElcCourseResult;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.SelectedCourse;
import com.server.edu.election.studentelec.context.TimeAndRoom;
import com.server.edu.election.studentelec.rules.bk.LimitCountCheckerRule;
import com.server.edu.election.studentelec.service.ElecQueueService;
import com.server.edu.election.studentelec.service.StudentElecService;
import com.server.edu.election.studentelec.utils.ElecContextUtil;
import com.server.edu.election.studentelec.utils.ElecStatus;
import com.server.edu.election.studentelec.utils.Keys;
import com.server.edu.election.studentelec.utils.QueueGroups;
import com.server.edu.election.vo.AllCourseVo;
import com.server.edu.election.vo.ElcLogVo;
import com.server.edu.util.CalUtil;
import com.server.edu.util.CollectionUtil;

@Service
public class StudentElecServiceImpl implements StudentElecService
{
    Logger LOG = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private ElecQueueService<ElecRequest> queueService;
    
    @Autowired
    private ElcCourseTakeDao courseTakeDao;
    
    @Autowired
    private TeachingClassDao classDao;
    
    @Autowired
    private ElcLogDao elcLogDao;
    
    @Autowired
    private RoundDataProvider dataProvider;
    
    @Autowired
    private StudentDao stuDao;
    
    @Autowired
    private ElecRoundCourseDao roundCourseDao;
    
    @Autowired
    private ElectionApplyService electionApplyService;
    
    @Autowired
    private StringRedisTemplate strTemplate;
    
    @Override
    public RestResult<ElecRespose> loading(Long roundId, String studentId)
    {
        ElecStatus currentStatus =
            ElecContextUtil.getElecStatus(roundId, studentId);
        // 如果当前是Init状态，进行初始化
        if (ElecStatus.Init.equals(currentStatus))
        {
            // 加入队列
            currentStatus = ElecStatus.Loading;
            ElecContextUtil.setElecStatus(roundId, studentId, currentStatus);
            if (!addLoadingToQueue(roundId, studentId))
            {
                currentStatus = ElecStatus.Init;
                ElecContextUtil
                    .setElecStatus(roundId, studentId, currentStatus);
                return RestResult.fail("请稍后再试");
            }
        }
        ElecRespose response = this.getElectResult(roundId, studentId);
        
        return RestResult.successData(response);
    }
    
    @Override
    public RestResult<ElecRespose> elect(ElecRequest elecRequest)
    {
        if (CollectionUtil.isEmpty(elecRequest.getElecClassList())
            && CollectionUtil.isEmpty(elecRequest.getWithdrawClassList()))
        {
            return RestResult.fail("你没选择任何课程");
        }
        Long roundId = elecRequest.getRoundId();
        String studentId = elecRequest.getStudentId();
        
        ElecStatus currentStatus =
            ElecContextUtil.getElecStatus(roundId, studentId);
        if (ElecStatus.Ready.equals(currentStatus)
            && ElecContextUtil.tryLock(roundId, studentId))
        {
            try
            {
                // 加锁后二次检查
                currentStatus =
                    ElecContextUtil.getElecStatus(roundId, studentId);
                if (ElecStatus.Ready.equals(currentStatus))
                {
                    // 加入选课队列
                    if (addElecToQueue(roundId, studentId, elecRequest))
                    {
                        ElecContextUtil.setElecStatus(roundId,
                            studentId,
                            ElecStatus.Processing);
                        currentStatus = ElecStatus.Processing;
                    }
                    else
                    {
                        return RestResult.fail("请稍后再试");
                    }
                }
            }
            finally
            {
                ElecContextUtil.unlock(roundId, studentId);
            }
        }
        return RestResult.successData(new ElecRespose(currentStatus));
    }
    
    @Override
    public ElecRespose getElectResult(Long roundId, String studentId)
    {
        RoundDataProvider dataProvider =
            SpringUtils.getBean(RoundDataProvider.class);
        ElectionRounds round = dataProvider.getRound(roundId);
        if (round == null)
        {
            return new ElecRespose();
        }
        
        ElecRespose response =
            ElecContextUtil.getElecRespose(studentId, round.getCalendarId());
        ElecStatus status = ElecContextUtil.getElecStatus(roundId, studentId);
        if (response == null)
        {
            response = new ElecRespose(status);
        }
        else
        {
            response.setStatus(status);
        }
        return response;
    }
    
    private boolean addLoadingToQueue(Long roundId, String studentId)
    {
        ElecRequest loadingRequest = new ElecRequest();
        loadingRequest.setRoundId(roundId);
        loadingRequest.setStudentId(studentId);
        return queueService.add(QueueGroups.STUDENT_LOADING, loadingRequest);
    }
    
    private boolean addElecToQueue(Long roundId, String studentId,
        ElecRequest elecRequest)
    {
        elecRequest.setRoundId(roundId);
        elecRequest.setStudentId(studentId);
        return queueService.add(QueueGroups.STUDENT_ELEC, elecRequest);
    }
    
    @Transactional
    @Override
    public void saveElc(ElecContext context, TeachingClassCache teachClass,
        ElectRuleType type)
    {
        StudentInfoCache stu = context.getStudentInfo();
        ElecRequest request = context.getRequest();
        ElecRespose respose = context.getRespose();
        Date date = new Date();
        String studentId = stu.getStudentId();
        
        Long roundId = request.getRoundId();
        ElectionRounds round = dataProvider.getRound(roundId);
        Long teachClassId = teachClass.getTeachClassId();
        String TeachClassCode = teachClass.getTeachClassCode();
        String courseCode = teachClass.getCourseCode();
        String courseName = teachClass.getCourseName();
        
        Integer logType = ElcLogVo.TYPE_1;
        
        Integer courseTakeType =
            Constants.REBUILD_CALSS.equals(teachClass.getTeachClassType())
                ? CourseTakeType.RETAKE.type()
                : CourseTakeType.NORMAL.type();
        
        if (ElectRuleType.ELECTION.equals(type))
        {
            if (dataProvider.containsRule(roundId,
                LimitCountCheckerRule.class.getSimpleName()))
            {
                LOG.info("---- LimitCountCheckerRule ----");
                // 增加选课人数
                int count = classDao.increElcNumberAtomic(teachClassId);
                if (count == 0)
                {
                    respose.getFailedReasons()
                        .put(teachClassId.toString(),
                            I18nUtil.getMsg("ruleCheck.limitCount"));
                    return;
                }
            }
            else
            {
                classDao.increElcNumber(teachClassId);
            }
            
            ElcCourseTake take = new ElcCourseTake();
            take.setCalendarId(round.getCalendarId());
            take.setChooseObj(request.getChooseObj());
            take.setCourseCode(courseCode);
            take.setCourseTakeType(courseTakeType);
            take.setCreatedAt(date);
            take.setStudentId(studentId);
            take.setTeachingClassId(teachClassId);
            take.setMode(round.getMode());
            take.setTurn(round.getTurn());
            courseTakeDao.insertSelective(take);
        }
        else
        {
            logType = ElcLogVo.TYPE_2;
            ElcCourseTake take = new ElcCourseTake();
            take.setCalendarId(round.getCalendarId());
            take.setCourseCode(courseCode);
            take.setStudentId(studentId);
            take.setTeachingClassId(teachClassId);
            courseTakeDao.delete(take);
            classDao.decrElcNumber(teachClassId);
        }
        
        // 添加选课日志
        ElcLog log = new ElcLog();
        log.setCalendarId(round.getCalendarId());
        log.setCourseCode(courseCode);
        log.setCourseName(courseName);
        log.setCreateBy(request.getCreateBy());
        log.setCreatedAt(date);
        log.setCreateIp(request.getRequestIp());
        log.setMode(
            ChooseObj.STU.type() == request.getChooseObj() ? ElcLogVo.MODE_1
                : ElcLogVo.MODE_2);
        log.setStudentId(studentId);
        log.setTeachingClassCode(TeachClassCode);
        log.setTurn(round.getTurn());
        log.setType(logType);
        this.elcLogDao.insertSelective(log);
        
        if (ElectRuleType.ELECTION.equals(type))
        {
        	if (stu.getManagerDeptId().equals(Constants.PROJ_GRADUATE)) {
        		//更新选课申请数据
        		electionApplyService
        		.update(studentId, round.getCalendarId(), courseCode);
			}
            // 更新缓存
            dataProvider.incrementElecNumber(teachClassId);
            
            respose.getSuccessCourses().add(teachClassId);
            SelectedCourse course = new SelectedCourse(teachClass);
            course.setTeachClassId(teachClassId);
            course.setTurn(round.getTurn());
            course.setCourseTakeType(courseTakeType);
            course.setChooseObj(request.getChooseObj());
            context.getSelectedCourses().add(course);
        }
    }
    
    /**根据轮次查询学生信息*/
    @Override
    public Student findStuRound(Long roundId, String studentId)
    {
        Student stu = stuDao.findStuRound(roundId, studentId);
        return stu;
    }

	@Override
	public RestResult<Map<String,List<ElcCourseResult>>> getAllCourse(AllCourseVo allCourseVo) {
	    Map<String,List<ElcCourseResult>> map = new HashMap<String, List<ElcCourseResult>>();
	    // 课程list
	    List<ElcCourseResult> list = new ArrayList<ElcCourseResult>();
	    // natrue集合
		List<String> natrueList = new ArrayList<String>();
		
		if (org.apache.commons.lang.StringUtils.isBlank(allCourseVo.getNatrue())) {
			natrueList = stuDao.getNature(allCourseVo);
			for (String natrue : natrueList) {
				allCourseVo.setNatrue(natrue);
				list = stuDao.getAllCourse(allCourseVo);
				List<ElcCourseResult> timeList = getTimeList(list);
				map.put(natrue, timeList);
			}
		}else {
			list = stuDao.getAllCourse(allCourseVo);
			List<ElcCourseResult> timeList = getTimeList(list);
			map.put(allCourseVo.getNatrue(), timeList);
		}
		return RestResult.successData(map);
	}
	
	private List<ElcCourseResult>  getTimeList(List<ElcCourseResult> list){
		if(CollectionUtil.isNotEmpty(list)){
			for (ElcCourseResult courseResult : list) {
				List<TimeAndRoom> tableMessages = getTimeById(courseResult.getTeachClassId());
				courseResult.setTimeTableList(tableMessages);
			}
		}
		return list;
	}
	
	private List<TimeAndRoom>  getTimeById(Long teachingClassId){
        List<TimeAndRoom> list=new ArrayList<>();
        List<ClassTeacherDto> classTimeAndRoom = courseTakeDao.findClassTimeAndRoomStr(teachingClassId);
        if(CollectionUtil.isNotEmpty(classTimeAndRoom)){
            for (ClassTeacherDto classTeacherDto : classTimeAndRoom) {
            	TimeAndRoom time=new TimeAndRoom();
                Integer dayOfWeek = classTeacherDto.getDayOfWeek();
                Integer timeStart = classTeacherDto.getTimeStart();
                Integer timeEnd = classTeacherDto.getTimeEnd();
                String roomID = classTeacherDto.getRoomID();
                String weekNumber = classTeacherDto.getWeekNumberStr();
                Long timeId = classTeacherDto.getTimeId();
                String[] str = weekNumber.split(",");
                
                List<Integer> weeks = Arrays.asList(str).stream().map(Integer::parseInt).collect(Collectors.toList());
                List<String> weekNums = CalUtil.getWeekNums(weeks.toArray(new Integer[] {}));
                String weekNumStr = weekNums.toString();//周次
                String weekstr = findWeek(dayOfWeek);//星期
                String timeStr=weekstr+" "+timeStart+"-"+timeEnd+" "+weekNumStr+" ";
                time.setTimeId(timeId);
                time.setTimeAndRoom(timeStr);
                time.setRoomId(roomID);
                list.add(time);
            }
        }
        return list;
    }
	
   /**
   *@Description: 星期
   *@Param:
   *@return:
   *@Author: bear
   *@date: 2019/2/15 13:59
   */
	private String findWeek(Integer number){
       String week="";
       switch(number){
           case 1:
               week="星期一";
               break;
           case 2:
               week="星期二";
               break;
           case 3:
               week="星期三";
               break;
           case 4:
               week="星期四";
               break;
           case 5:
               week="星期五";
               break;
           case 6:
               week="星期六";
               break;
           case 7:
               week="星期日";
               break;
       }
       return week;
   }

    /** 获取学生可选课程 */
   	@Override
   	public List<ElcCourseResult> getOptionalCourses(Long roundId,
   			String studentId) {
   		
   		RoundDataProvider dataProvider =
   				SpringUtils.getBean(RoundDataProvider.class);

		ElectionRounds round = dataProvider.getRound(roundId);
		
		ElecContextUtil elecContextUtil = ElecContextUtil.create(studentId,round.getCalendarId());
		
		//获取学生本轮次已经选取的课程
		Set<SelectedCourse> selectedCourseSet = elecContextUtil.getSet("SelectedCourses", SelectedCourse.class);
		//获取学生未完成的课程
		Set<CompletedCourse> inCompletedCourseSet = elecContextUtil.getSet("failedCourse", CompletedCourse.class);
		//从缓存中拿到本轮次排课信息
		HashOperations<String, String, String> ops = strTemplate.opsForHash();
		String key = Keys.getRoundCourseKey(roundId);
		Map<String, String> roundsCoursesMap =  ops.entries(key);
		
		List<String> roundsCoursesIdsList = new ArrayList<>();
		for (Entry<String, String> entry : roundsCoursesMap.entrySet()) {
			 String courseCode = entry.getKey();
			 roundsCoursesIdsList.add(courseCode);
		}
		List<CompletedCourse> centerCourse = new ArrayList<>();
   		//两个结果取交集 拿到学生可选课程
   		for (String courseOpenCode : roundsCoursesIdsList) {
   			for (CompletedCourse inCompletedCourse :inCompletedCourseSet) {
   				if(courseOpenCode.equals(inCompletedCourse.getCourseCode())){
   					if (inCompletedCourse.getIsPass() == null) {
   						centerCourse.add(inCompletedCourse);
   					}
   				}
   			}
   		}
		List<CompletedCourse> OptionalGraduateCoursesList = new ArrayList<>();
   		for (CompletedCourse completedCourseModel : OptionalGraduateCoursesList) {
   			Boolean flag = true;
   			for (SelectedCourse selectedCourseModel :selectedCourseSet) {
   				if(selectedCourseModel.getCourseCode().equals(completedCourseModel.getCourseCode())){
   						flag = false;
   						continue;
   				}
   			}
   			if (flag) {
   				OptionalGraduateCoursesList.add(completedCourseModel);
			}
   		}
   		List<ElcCourseResult> result = new ArrayList<>();
   		for (CompletedCourse completedCourse : OptionalGraduateCoursesList) {
   			
   			ElcCourseResult elcCourseResult = new ElcCourseResult();
   			elcCourseResult.setNatrue(completedCourse.getNature());
   			elcCourseResult.setCourseCode(completedCourse.getCourseCode());
   			elcCourseResult.setCourseName(completedCourse.getCourseName());
   			elcCourseResult.setCredits(completedCourse.getCredits());
   			elcCourseResult.setFaculty(null);
   			List<TeachingClassCache> teachClasss =
   		            dataProvider.getTeachClasss(roundId, completedCourse.getCourseCode());
	        if (CollectionUtil.isNotEmpty(teachClasss))
	        {
	            for (TeachingClassCache teachClass : teachClasss)
	            {
	                Long teachClassId = teachClass.getTeachClassId();
	                Integer elecNumber = dataProvider.getElecNumber(teachClassId);
	                teachClass.setCurrentNumber(elecNumber);
	                elcCourseResult.setTeachClassId(teachClass.getTeachClassId());
	                elcCourseResult.setTeachingClassCode(teachClass.getTeachClassCode());
	       			elcCourseResult.setTeacherCode(teachClass.getTeacherCode());
	       			elcCourseResult.setTeacherName(teachClass.getTeacherName());
	                elcCourseResult.setElcNumber(teachClass.getCurrentNumber());
	                elcCourseResult.setNumber(teachClass.getMaxNumber());
	                result.add(elcCourseResult);
	            }
	        }
   			
		}
   		return result;
   	}
}
