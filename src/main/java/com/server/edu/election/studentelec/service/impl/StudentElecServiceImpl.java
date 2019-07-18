package com.server.edu.election.studentelec.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
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
import com.server.edu.election.dto.NoSelectCourseStdsDto;
import com.server.edu.election.entity.ElcCourseTake;
import com.server.edu.election.entity.ElcLog;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.entity.Student;
import com.server.edu.election.service.ElectionApplyService;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ClassTimeUnit;
import com.server.edu.election.studentelec.context.CompletedCourse;
import com.server.edu.election.studentelec.context.ElcCourseResult;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.PlanCourse;
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
import com.server.edu.election.vo.ElcCourseTakeVo;
import com.server.edu.election.vo.ElcLogVo;
import com.server.edu.election.vo.ElcResultCourseVo;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
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
            if(round.getTurn()!=Constants.THIRD_TURN&&round.getTurn()!=Constants.FOURTH_TURN) {
                classDao.decrElcNumber(teachClassId);
            }
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
		if (StringUtils.isNotBlank(allCourseVo.getNatrue())) {
			natrueList.add(allCourseVo.getNatrue());
		}else {
			natrueList = stuDao.getNature(allCourseVo);
		}
		
		for (String natrue : natrueList) {
			allCourseVo.setNatrue(natrue);
			list = stuDao.getAllCourse(allCourseVo);
			List<ElcCourseResult> timeList = getTimeList(list);
			map.put(natrue, timeList);
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
   	public ElcResultCourseVo getOptionalCourses(Long roundId,
   			String studentId) {
   		
   		RoundDataProvider dataProvider =
   				SpringUtils.getBean(RoundDataProvider.class);

		ElectionRounds round = dataProvider.getRound(roundId);
		
		ElecContextUtil elecContextUtil = ElecContextUtil.create(studentId,round.getCalendarId());
		
		//每门课上课信息集合
		List<List<ClassTimeUnit>> classTimeLists = new ArrayList<>();
		
		//获取学生培养计划中的课程
		Set<PlanCourse> planCourses = elecContextUtil.getSet("PlanCourses", PlanCourse.class);
		
		//获取学生本学期已经选取的课程
		Set<SelectedCourse> selectedCourseSet = elecContextUtil.getSet("SelectedCourses", SelectedCourse.class);
		
		List<ElcCourseResult> selectedCourses = new ArrayList<>();
   		for (SelectedCourse completedCourse : selectedCourseSet) {
   			
   			ElcCourseResult elcCourseResult = new ElcCourseResult();
   			elcCourseResult.setNatrue(completedCourse.getNature());
   			elcCourseResult.setCourseCode(completedCourse.getCourseCode());
   			elcCourseResult.setCourseName(completedCourse.getCourseName());
   			elcCourseResult.setCredits(completedCourse.getCredits());
   			elcCourseResult.setFaculty(completedCourse.getFaculty());
   			List<TeachingClassCache> teachClasss =
   		            dataProvider.getTeachClasss(roundId, completedCourse.getCourseCode());
	        if (CollectionUtil.isNotEmpty(teachClasss))
	        {
	            for (TeachingClassCache teachClass : teachClasss)
	            {
	                Long teachClassId = teachClass.getTeachClassId();
	                if(teachClassId.longValue() == completedCourse.getTeachClassMsg().longValue()){
	                	
	                	Integer elecNumber = dataProvider.getElecNumber(teachClassId);
	                	teachClass.setCurrentNumber(elecNumber);
	                	elcCourseResult.setFaculty(teachClass.getFaculty());
	                	elcCourseResult.setTeachClassId(teachClass.getTeachClassId());
	                	elcCourseResult.setTeachingClassCode(teachClass.getTeachClassCode());
	                	elcCourseResult.setTeacherCode(teachClass.getTeacherCode());
	                	elcCourseResult.setTeacherName(teachClass.getTeacherName());
	                	elcCourseResult.setElcNumber(teachClass.getCurrentNumber());
	                	elcCourseResult.setNumber(teachClass.getMaxNumber());
	                	elcCourseResult.setTimes(teachClass.getTimes());
	                	elcCourseResult.setTimeTableList(teachClass.getTimeTableList());
	                	classTimeLists.add( teachClass.getTimes());
	                	selectedCourses.add(elcCourseResult);
	                }
	            }
	        }
		}
		//获取学生已完成的课程
		Set<CompletedCourse> completedCourses1 = elecContextUtil.getSet("CompletedCourses", CompletedCourse.class);
		
		//从缓存中拿到本轮次排课信息
		HashOperations<String, String, String> ops = strTemplate.opsForHash();
		String key = Keys.getRoundCourseKey(roundId);
		Map<String, String> roundsCoursesMap =  ops.entries(key);
		
		List<String> roundsCoursesIdsList = new ArrayList<>();
		for (Entry<String, String> entry : roundsCoursesMap.entrySet()) {
			 String courseCode = entry.getKey();
			 roundsCoursesIdsList.add(courseCode);
		}
		
		//培养计划与排课信息的交集（中间变量）
		List<PlanCourse> centerCourse = new ArrayList<>();
   		//两个结果取交集 拿到学生培养计划与排课信息的交集
   		for (String courseOpenCode : roundsCoursesIdsList) {
   			for (PlanCourse planCourse :planCourses) {
   				if(courseOpenCode.equals(planCourse.getCourseCode())){
					centerCourse.add(planCourse);
   				}
   			}
   		}
   		
   		//从中间变量中剔除本学期已经选过的课
		List<PlanCourse> optionalGraduateCoursesList = new ArrayList<>();
   		for (PlanCourse centerCourseModel : centerCourse) {
   			Boolean flag = true;
   			for (SelectedCourse selectedCourseModel :selectedCourseSet) {
   				if(selectedCourseModel.getCourseCode().equals(centerCourseModel.getCourseCode())){
   						flag = false;
   						break;
   				}
   			}
   			if (flag) {
   				optionalGraduateCoursesList.add(centerCourseModel);
			}
   		}
   		//从中间变量中剔除已完成的课，得到可选课程
   		List<PlanCourse> optionalGraduateCourses = new ArrayList<>();
   		for (PlanCourse centerCourses : optionalGraduateCoursesList) {
   			Boolean flag = true;
   			for (CompletedCourse selectedCourseModel :completedCourses1) {
   				if(selectedCourseModel.getCourseCode().equals(centerCourses.getCourseCode())){
   					flag = false;
   					break;
   				}
   			}
   			if (flag) {
   				optionalGraduateCourses.add(centerCourses);
   			}
   		}
   		
   		List<ElcCourseResult> completedCourses = new ArrayList<>();
   		for (PlanCourse completedCourse : optionalGraduateCourses) {
   			ElcCourseResult elcCourseResult = new ElcCourseResult();
   			elcCourseResult.setNatrue(completedCourse.getNature());
   			elcCourseResult.setCourseCode(completedCourse.getCourseCode());
   			elcCourseResult.setCourseName(completedCourse.getCourseName());
   			elcCourseResult.setCredits(completedCourse.getCredits());
   			List<TeachingClassCache> teachClasss =
   		            dataProvider.getTeachClasss(roundId, completedCourse.getCourseCode());
	        if (CollectionUtil.isNotEmpty(teachClasss))
	        {
	            for (TeachingClassCache teachClass : teachClasss)
	            {
	                Long teachClassId = teachClass.getTeachClassId();
	                Integer elecNumber = dataProvider.getElecNumber(teachClassId);
	                teachClass.setCurrentNumber(elecNumber);
	                elcCourseResult.setFaculty(teachClass.getFaculty());
	                elcCourseResult.setTeachClassId(teachClass.getTeachClassId());
	                elcCourseResult.setTeachingClassCode(teachClass.getTeachClassCode());
	       			elcCourseResult.setTeacherCode(teachClass.getTeacherCode());
	       			elcCourseResult.setTeacherName(teachClass.getTeacherName());
	                elcCourseResult.setElcNumber(teachClass.getCurrentNumber());
	                elcCourseResult.setNumber(teachClass.getMaxNumber());
	                elcCourseResult.setTimeTableList(teachClass.getTimeTableList());
	                elcCourseResult.setTimes(teachClass.getTimes());
	                Boolean flag = true;
	                //上课时间是否冲突
					for (List<ClassTimeUnit> classTimeList: classTimeLists) 
					{
						//已选课程上课时间
						for (ClassTimeUnit classTimeUnit : classTimeList) 
						{
							//本次选课课程时间
							for (ClassTimeUnit thisClassTimeUnit : teachClass.getTimes()) 
							{
								//先判断上课周是有重复的，没有则不冲突
								List<Integer> thisWeeks = thisClassTimeUnit.getWeeks();
								List<Integer> weeks = thisClassTimeUnit.getWeeks();
								thisWeeks.retainAll(weeks);
								if (CollectionUtil.isNotEmpty(weeks)) 
								{
									flag = true;
								}else{
									//判断上课周内时间
									if (thisClassTimeUnit.getDayOfWeek() == classTimeUnit.getDayOfWeek()) 
									{
										//判断上课时间
										if((thisClassTimeUnit.getTimeStart() <= classTimeUnit.getTimeStart()
												&& thisClassTimeUnit.getTimeEnd() >= thisClassTimeUnit.getTimeEnd())
												|| (classTimeUnit.getTimeStart() <= thisClassTimeUnit.getTimeStart()
												&& classTimeUnit.getTimeEnd() >= classTimeUnit.getTimeEnd())
												|| (classTimeUnit.getTimeStart() <= thisClassTimeUnit.getTimeStart()
												&& classTimeUnit.getTimeStart() >= thisClassTimeUnit.getTimeEnd())
												|| (classTimeUnit.getTimeEnd() <= thisClassTimeUnit.getTimeStart()
												&& classTimeUnit.getTimeEnd() >= thisClassTimeUnit.getTimeEnd())
												|| (thisClassTimeUnit.getTimeStart() <= classTimeUnit.getTimeStart()
												&& thisClassTimeUnit.getTimeStart() >= classTimeUnit.getTimeEnd())
												|| (thisClassTimeUnit.getTimeEnd() <= classTimeUnit.getTimeStart()
												&& thisClassTimeUnit.getTimeEnd() >= classTimeUnit.getTimeEnd())){
											flag = false;
											break;
										}else{
											flag = true;
										}
									}else{
										flag = true;
									}
								}
							}
							if (!flag) {
								break;
							}
						}
						if (!flag) {
							break;
						}
					}
					if (flag) {
						elcCourseResult.setIsConflict(Constants.ZERO);
					}else{
						elcCourseResult.setIsConflict(-Constants.ONE);
					}
					completedCourses.add(elcCourseResult);
				}
	        }
		}
   		ElcResultCourseVo elcResultCourseVo = new ElcResultCourseVo(completedCourses,selectedCourses);
   		
   		return elcResultCourseVo;
   	}

	@Override
	public PageResult<NoSelectCourseStdsDto> findAgentElcStudentList(PageCondition<NoSelectCourseStdsDto> condition) {
		Session session = SessionUtils.getCurrentSession();
		NoSelectCourseStdsDto noSelectCourseStds = condition.getCondition();

		if (session.isAcdemicDean()) {// 教务员
			noSelectCourseStds.setRole(Constants.DEPART_ADMIN);
		    noSelectCourseStds.setuId(session.getUid());
		} 
        PageHelper.startPage(condition.getPageNum_(),condition.getPageSize_());
        
        Page<NoSelectCourseStdsDto> agentElcStudentList = courseTakeDao.findAgentElcStudentList(noSelectCourseStds);

        return new PageResult<>(agentElcStudentList);
	}

	@Override
	public Map<String, Object> getElectResultCount(String studentId, Long roundId,Map<String,Object> result) {

		RoundDataProvider dataProvider =
   				SpringUtils.getBean(RoundDataProvider.class);
		//获取当前选课轮次
		ElectionRounds round = dataProvider.getRound(roundId);
		//获取学生已选课程
//		List<ElcCourseTakeVo> eleCourse = courseTakeDao.findAllByStudentId4Course(studentId);
		
		ElecContextUtil elecContextUtil = ElecContextUtil.create(studentId,round.getCalendarId());
		//获取当前已经完成的课程
		Set<CompletedCourse> completedCourses = elecContextUtil.getSet("CompletedCourses", CompletedCourse.class);
		
		//获取本学期已选课程
		Set<SelectedCourse> selectedCourses = elecContextUtil.getSet("SelectedCourses", SelectedCourse.class);
		Set<SelectedCourse> thisSelectedCourses = new TreeSet<>();
		for (SelectedCourse selectedCourse : selectedCourses) {
			//获取本次选课信息
			if (selectedCourse.getTurn().intValue() == round.getTurn()) {
				//已完成课程数
				thisSelectedCourses.add(selectedCourse);
				
			}
		}
		for(Entry<String, Object> entry : result.entrySet()){
			Map<String,Object> map = new HashMap<String,Object>();
			//已完成课程数
			Integer courseNum = 0;
			//已完成学分
			Double sumMcredits = 0.0;
			for (CompletedCourse completedCourse : completedCourses) {
				if (completedCourse.getCourseLabelId() == Long.parseLong(entry.getKey())) {
					courseNum ++;
					sumMcredits += completedCourse.getCredits();
				}
			}
			//统计本次选课门数
			Integer thisTimecourseNum = 0;
			//统计本次选课学分
			Double thisTimeSumMcredits = 0.0;
			for (SelectedCourse thisSelected : thisSelectedCourses) {
				if (thisSelected.getLabel().equals(entry.getKey())) {
					thisTimecourseNum ++;
					thisTimeSumMcredits += thisSelected.getCredits();
				}
			}
			map.put("courseNum", courseNum+thisTimecourseNum);
			map.put("sumMcredits", sumMcredits+thisTimeSumMcredits);
			map.put("culturePlan", entry.getValue());
			map.put("thisTimeSumMcredits", thisTimeSumMcredits.doubleValue());
			result.put(entry.getKey(), map);
		}
		
		return result;
	}

}
