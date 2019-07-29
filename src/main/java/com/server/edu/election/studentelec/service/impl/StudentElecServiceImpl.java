package com.server.edu.election.studentelec.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.servicecomb.provider.springmvc.reference.RestTemplateBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.ServicePathEnum;
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
import com.server.edu.election.studentelec.service.cache.AbstractCacheService;
import com.server.edu.election.studentelec.utils.ElecContextUtil;
import com.server.edu.election.studentelec.utils.ElecStatus;
import com.server.edu.election.studentelec.utils.Keys;
import com.server.edu.election.studentelec.utils.QueueGroups;
import com.server.edu.election.util.WeekUtil;
import com.server.edu.election.vo.AllCourseVo;
import com.server.edu.election.vo.ElcLogVo;
import com.server.edu.util.CalUtil;
import com.server.edu.util.CollectionUtil;

@Service
public class StudentElecServiceImpl extends AbstractCacheService implements StudentElecService
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
    private ElectionApplyService electionApplyService;
    
    @Autowired
    private StringRedisTemplate strTemplate;
   
    private RestTemplate restTemplate = RestTemplateBuilder.create();
    
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
    public RestResult<ElecRespose> loadingAdmin(Integer chooseObj,Long calendarId, String studentId)
    {
    	ElecStatus currentStatus =
    			ElecContextUtil.getElecAdminStatus(calendarId, studentId);
    	// 如果当前是Init状态，进行初始化
    	if (ElecStatus.Init.equals(currentStatus))
    	{
    		// 加入队列
    		currentStatus = ElecStatus.Loading;
    		ElecContextUtil.setElecAdminStatus(calendarId, studentId, currentStatus);
    		if (!addLoadingAdminToQueue(chooseObj,calendarId, studentId))
    		{
    			currentStatus = ElecStatus.Init;
    			ElecContextUtil
    			.setElecAdminStatus(calendarId, studentId, currentStatus);
    			return RestResult.fail("请稍后再试");
    		}
    	}
    	ElecRespose response = this.getElectResultAdmin(calendarId, studentId);
    	
    	return RestResult.successData(response);
    }
    
    @Override
	public ElecContext setData(String studentId,ElecContext c,Long roundId, Long calendarId) {
		
    	
    	Map<String, Object> elecResult = new HashMap<>();
    	if (roundId != null) { // 教务员
    		elecResult = getElectResultCount(studentId,roundId);
		}else {                // 管理员
			elecResult = getAdminElectResultCount(studentId,c,calendarId);
		}
		//每门课上课信息集合
		List<List<ClassTimeUnit>> classTimeLists = new ArrayList<>();
		
		//获取学生培养计划中的课程
		Set<PlanCourse> planCourses = c.getPlanCourses();
		
		//获取学生本学期已经选取的课程
		Set<SelectedCourse> selectedCourseSet = c.getSelectedCourses();

		List<SelectedCourse> selectedCourses = new ArrayList<>();
   		for (SelectedCourse completedCourse : selectedCourseSet) {

   			SelectedCourse elcCourseResult = new SelectedCourse();
   			elcCourseResult.setNature(completedCourse.getNature());
   			elcCourseResult.setCourseCode(completedCourse.getCourseCode());
   			elcCourseResult.setCourseName(completedCourse.getCourseName());
   			elcCourseResult.setCredits(completedCourse.getCredits());
   			elcCourseResult.setCalendarName(completedCourse.getCalendarName());
            elcCourseResult.setCourseTakeType(completedCourse.getCourseTakeType());
            elcCourseResult.setAssessmentMode(completedCourse.getAssessmentMode());
            elcCourseResult.setPublicElec(completedCourse.isPublicElec());
            elcCourseResult.setCalendarId(completedCourse.getCalendarId());
            elcCourseResult.setTerm(completedCourse.getTerm());
            List<TeachingClassCache> teachClasss = new ArrayList<TeachingClassCache>();
            if (roundId != null) { // 教务员
            	teachClasss = dataProvider.getTeachClasss(roundId, completedCourse.getCourseCode());
			}else {                // 管理员
				teachClasss = dataProvider.getTeachClasssbyCalendarId(calendarId, completedCourse.getCourseCode());
			}
            
	        if (CollectionUtil.isNotEmpty(teachClasss))
	        {
	            for (TeachingClassCache teachClass : teachClasss)
	            {
	                Long teachClassId = teachClass.getTeachClassId();
	                if(teachClassId.longValue() == completedCourse.getTeachClassMsg().longValue()){
	                	
	                	Integer elecNumber = dataProvider.getElecNumber(teachClassId);
	                	teachClass.setCurrentNumber(elecNumber);
                        setClassCache(elcCourseResult, teachClass);
	                	classTimeLists.add( teachClass.getTimes());
	                }
	            }
	        }
            selectedCourses.add(elcCourseResult);
        }
   		selectedCourseSet.clear();
   		selectedCourseSet.addAll(selectedCourses);

        RedisTemplate<String, TeachingClassCache> redisTemplate = redisTemplate(TeachingClassCache.class);
        HashOperations<String, String, TeachingClassCache> hash = redisTemplate.opsForHash();
        //获取学生已完成的课程
        Set<CompletedCourse> completedCourses1 = c.getCompletedCourses();

		//从缓存中拿到本轮次排课信息
		HashOperations<String, String, String> ops = strTemplate.opsForHash();
		String key = "";
		if (calendarId == null) {
			key = Keys.getRoundCourseKey(roundId);  // 学生选课或者教务员代理选课
		}else {
			key = Keys.getCalendarCourseKey(calendarId);  // 管理员代理选课
		}
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
   			elcCourseResult.setNature(completedCourse.getNature());
   			elcCourseResult.setCourseCode(completedCourse.getCourseCode());
   			elcCourseResult.setCourseName(completedCourse.getCourseName());
   			elcCourseResult.setCredits(completedCourse.getCredits());
   			List<TeachingClassCache> teachClasss = new ArrayList<TeachingClassCache>();
   			if (roundId != null) { // 教务员
            	teachClasss = dataProvider.getTeachClasss(roundId, completedCourse.getCourseCode());
			}else {                // 管理员
				teachClasss = dataProvider.getTeachClasssbyCalendarId(calendarId, completedCourse.getCourseCode());
			}
   			
	        if (CollectionUtil.isNotEmpty(teachClasss))
	        {
	            for (TeachingClassCache teachClass : teachClasss)
	            {
	                Long teachClassId = teachClass.getTeachClassId();
	                Integer elecNumber = dataProvider.getElecNumber(teachClassId);
	                teachClass.setCurrentNumber(elecNumber);
	                elcCourseResult.setFaculty(teachClass.getFaculty());
	                elcCourseResult.setTeachClassId(teachClass.getTeachClassId());
	                elcCourseResult.setTeachClassCode(teachClass.getTeachClassCode());
	       			elcCourseResult.setTeacherCode(teachClass.getTeacherCode());
	       			elcCourseResult.setTeacherName(teachClass.getTeacherName());
	                elcCourseResult.setCurrentNumber(teachClass.getCurrentNumber());
	                elcCourseResult.setMaxNumber(teachClass.getMaxNumber());
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
   		c.setSelectedCourses(selectedCourseSet);
   		c.setOptionalCourses(completedCourses);
   		c.setElecResult(elecResult);
   		return c;
	}

	private void setClassCache(TeachingClassCache newClassCache, TeachingClassCache oldClassCache){
        newClassCache.setFaculty(oldClassCache.getFaculty());
        newClassCache.setTeachClassId(oldClassCache.getTeachClassId());
        newClassCache.setTeachClassCode(oldClassCache.getTeachClassCode());
        newClassCache.setTeacherCode(oldClassCache.getTeacherCode());
        newClassCache.setTeacherName(oldClassCache.getTeacherName());
        newClassCache.setCurrentNumber(oldClassCache.getCurrentNumber());
        newClassCache.setMaxNumber(oldClassCache.getMaxNumber());
        newClassCache.setTimes(oldClassCache.getTimes());
        newClassCache.setTimeTableList(oldClassCache.getTimeTableList());
        newClassCache.setRemark(oldClassCache.getRemark());
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
    public RestResult<ElecRespose> adminElect(ElecRequest elecRequest)
    {
    	if (CollectionUtil.isEmpty(elecRequest.getElecClassList())
    			&& CollectionUtil.isEmpty(elecRequest.getWithdrawClassList()))
    	{
    		return RestResult.fail("你没选择任何课程");
    	}
    	Long calendarId = elecRequest.getCalendarId();
    	String studentId = elecRequest.getStudentId();
    	
    	ElecStatus currentStatus =
    			ElecContextUtil.getElecAdminStatus(calendarId, studentId);
    	if (ElecStatus.Ready.equals(currentStatus)
    			&& ElecContextUtil.tryLock(calendarId, studentId))
    	{
    		try
    		{
    			// 加锁后二次检查
    			currentStatus =
    					ElecContextUtil.getElecAdminStatus(calendarId, studentId);
    			if (ElecStatus.Ready.equals(currentStatus))
    			{
    				// 加入选课队列
    				if (addAdminElecToQueue(calendarId, studentId, elecRequest))
    				{
    					ElecContextUtil.setElecAdminStatus(calendarId,
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
    			ElecContextUtil.unlock(calendarId, studentId);
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
    
    @Override
    public ElecRespose getElectResultAdmin(Long calendarId, String studentId)
    {
    	RoundDataProvider dataProvider =
    			SpringUtils.getBean(RoundDataProvider.class);
    	if (calendarId == null)
    	{
    		return new ElecRespose();
    	}
    	
    	ElecRespose response =
    			ElecContextUtil.getElecRespose(studentId, calendarId);
    	ElecStatus status = ElecContextUtil.getElecAdminStatus(calendarId, studentId);
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
    
    private boolean addLoadingAdminToQueue(Integer chooseObj, Long calendarId, String studentId)
    {
    	ElecRequest loadingRequest = new ElecRequest();
    	loadingRequest.setChooseObj(chooseObj);
    	loadingRequest.setCalendarId(calendarId);
    	loadingRequest.setStudentId(studentId);
    	return queueService.add(QueueGroups.ADMIN_LOADING, loadingRequest);
    }
    
    private boolean addElecToQueue(Long roundId, String studentId,
        ElecRequest elecRequest)
    {
        elecRequest.setRoundId(roundId);
        elecRequest.setStudentId(studentId);
        return queueService.add(QueueGroups.STUDENT_ELEC, elecRequest);
    }
    
    private boolean addAdminElecToQueue(Long calendarId, String studentId,
    		ElecRequest elecRequest)
    {
    	elecRequest.setCalendarId(calendarId);;
    	elecRequest.setStudentId(studentId);
    	return queueService.add(QueueGroups.ADMIN_ELEC, elecRequest);
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
                int count = classDao.decrElcNumber(teachClassId);
                if (count > 0)
                {
                    dataProvider.decrElcNumber(teachClassId);
                }
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
	public List<TeachingClassCache> arrangementCourses(AllCourseVo allCourseVo) {
	    List<ElcCourseResult> list = stuDao.getAllCourse(allCourseVo);
	    List<TeachingClassCache> lessons = new ArrayList<TeachingClassCache>(list.size());
	     
	    //从缓存中拿到本轮次排课信息
        //List<Long> teachClassIds = list.stream().map(ElcCourseResult::getTeachClassId).collect(Collectors.toList());
        List<String> teachClassIds = new ArrayList<String>(list.size());
        for (ElcCourseResult elcCourseResult : list) {
        	teachClassIds.add(elcCourseResult.getTeachClassId()+"");
		}
        
        HashOperations<String, String, TeachingClassCache> hash = opsTeachClass();
        lessons = hash.multiGet(Keys.getClassKey(), teachClassIds);
        // 过滤null
        lessons = lessons.stream().filter(Objects::nonNull).collect(Collectors.toList());
        return lessons;
	}
	
    public HashOperations<String, String, TeachingClassCache> opsTeachClass()
    {
        RedisTemplate<String, TeachingClassCache> redisTemplate =
            redisTemplate(TeachingClassCache.class);
        HashOperations<String, String, TeachingClassCache> ops =
            redisTemplate.opsForHash();
        return ops;
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
                String weekstr = WeekUtil.findWeek(dayOfWeek);//星期
                String timeStr=weekstr+" "+timeStart+"-"+timeEnd+" "+weekNumStr+" ";
                time.setTimeId(timeId);
                time.setTimeAndRoom(timeStr);
                time.setRoomId(roomID);
                list.add(time);
            }
        }
        return list;
    }
	

	@Override
	public PageResult<NoSelectCourseStdsDto> findAgentElcStudentList(PageCondition<NoSelectCourseStdsDto> condition) {
        PageHelper.startPage(condition.getPageNum_(),condition.getPageSize_());
        
        NoSelectCourseStdsDto noSelectCourseStds = condition.getCondition();
        Page<NoSelectCourseStdsDto> agentElcStudentList = courseTakeDao.findAgentElcStudentList(noSelectCourseStds);
        return new PageResult<>(agentElcStudentList);
	}

	@Override
	public Map<String, Object> getElectResultCount(String studentId, Long roundId) {

		/** 调用培养：培养方案的课程分类学分 */
    	String culturePath = ServicePathEnum.CULTURESERVICE.getPath("/studentCultureRel/getCultureMsg/{studentId}");
    	RestResult<Map<String, Object>> restResult = restTemplate.getForObject(culturePath,RestResult.class,studentId);
		
		RoundDataProvider dataProvider =
   				SpringUtils.getBean(RoundDataProvider.class);
		//获取当前选课轮次
		ElectionRounds round = dataProvider.getRound(roundId);
		
		ElecContextUtil elecContextUtil = ElecContextUtil.create(studentId,round.getCalendarId());
		
		//获取当前已经完成的课程
		Set<PlanCourse> planCourse = elecContextUtil.getSet("PlanCourses", PlanCourse.class);
		Set<CompletedCourse> completedCourses = elecContextUtil.getSet("CompletedCourses", CompletedCourse.class);
		
		//获取本学期已选课程
		Set<SelectedCourse> selectedCourses = elecContextUtil.getSet("SelectedCourses", SelectedCourse.class);
		List<SelectedCourse> thisSelectedCourses = new ArrayList<>();
		for (SelectedCourse selectedCourse : selectedCourses) {
			//获取本次选课信息
			if (selectedCourse.getTurn().intValue() == round.getTurn()) {
				//已完成课程数
				thisSelectedCourses.add(selectedCourse);
				if (StringUtils.isEmpty(selectedCourse.getLabel())) {
					for (PlanCourse course : planCourse) {
						if (course.getCourseCode().equals(selectedCourse.getCourseCode())) {
							selectedCourse.setLabel(course.getLabel()+"");
						}
					}
				}
				
			}
		}
		LOG.info("+++++++++++++++++++++++++++++DATA     ZUZHUANG");
		Map<String,Object>minNumMap = new HashMap<String,Object>();
		Map<String,Object>courseNumMap = new HashMap<String,Object>();
		Map<String,Object>creditsMap = new HashMap<String,Object>();
		Map<String,Object>thisTimesumCreditsMap = new HashMap<String,Object>();
		Map<String,Object>sumCreditsMap = new HashMap<String,Object>();
		Map<String,Object>resultMap= new HashMap<String,Object>();
		
		minNumMap.put("publicLessons",0);
		courseNumMap.put("publicLessons", 0);
		creditsMap.put("publicLessons", 0);
		thisTimesumCreditsMap.put("publicLessons", 0);
		sumCreditsMap.put("publicLessons", 0);
		
		minNumMap.put("professionalCourses",0);
		courseNumMap.put("professionalCourses", 0);
		creditsMap.put("professionalCourses", 0);
		thisTimesumCreditsMap.put("professionalCourses", 0);
		sumCreditsMap.put("professionalCourses", 0);
		
		minNumMap.put("nonDegreeCourses",0);
		courseNumMap.put("nonDegreeCourses", 0);
		creditsMap.put("nonDegreeCourses", 0);
		thisTimesumCreditsMap.put("nonDegreeCourses", 0);
		sumCreditsMap.put("nonDegreeCourses", 0);
		
		minNumMap.put("requiredCourses",0);
		courseNumMap.put("requiredCourses", 0);
		creditsMap.put("requiredCourses", 0);
		thisTimesumCreditsMap.put("requiredCourses", 0);
		sumCreditsMap.put("requiredCourses", 0);
		
		minNumMap.put("interFaculty",0);
		courseNumMap.put("interFaculty", 0);
		creditsMap.put("interFaculty", 0);
		thisTimesumCreditsMap.put("interFaculty", 0);
		sumCreditsMap.put("interFaculty", 0);
		for(Entry<String, Object> entry : restResult.getData().entrySet()){
			Map<String,Object> map = (Map<String,Object>)entry.getValue();
			String key = entry.getKey();
			LOG.info("+++++++++++++++++++++++++++++DATA     ZUZHUANG"+key);
			//已完成课程数
			Integer courseNum = 0;
			//已完成学分
			Double sumCredits = 0.0;
			for (CompletedCourse completedCourse : completedCourses) {
				if (completedCourse != null) {
					for (PlanCourse course : planCourse) {
						if (course.getCourseCode().equals(completedCourse.getCourseCode())) {
							completedCourse.setCourseLabelId(course.getLabel());
						}
					}
					if (completedCourse.getCourseLabelId() != null && completedCourse.getCourseLabelId() == Long.parseLong(key.trim())) {
						courseNum ++;
						sumCredits += completedCourse.getCredits();
					}
				}
			}
			//统计本次选课门数
			Integer thisTimecourseNum = 0;
			//统计本次选课学分
			Double thisTimeSumCredits = 0.0;
			for (SelectedCourse thisSelected : thisSelectedCourses) {
				
				if (StringUtils.equalsIgnoreCase(key,thisSelected.getLabel())) {
					thisTimecourseNum ++;
					thisTimeSumCredits += thisSelected.getCredits();
				}
			}
			map.put("courseNum", courseNum+thisTimecourseNum);
			map.put("sumCredits", sumCredits+thisTimeSumCredits);
			map.put("thisTimeSumCredits", thisTimeSumCredits.doubleValue());
			
			if(map.get("labelName").equals("公共学位课")){
				minNumMap.put("publicLessons",map.get("minNum") == null ? 0 : map.get("minNum"));
				courseNumMap.put("publicLessons", map.get("courseNum") == null ? 0 : map.get("courseNum"));
				creditsMap.put("publicLessons", map.get("credits") == null ? 0 : map.get("credits"));
				thisTimesumCreditsMap.put("publicLessons", map.get("thisTimeSumCredits") == null ? 0 : map.get("thisTimeSumCredits"));
				sumCreditsMap.put("publicLessons", map.get("sumCredits") == null ? 0 : map.get("sumCredits"));
				
			}else if(map.get("labelName").equals("专业学位课")){
				minNumMap.put("professionalCourses",map.get("minNum") == null ? 0 : map.get("minNum"));
				courseNumMap.put("professionalCourses", map.get("courseNum") == null ? 0 : map.get("courseNum"));
				creditsMap.put("professionalCourses", map.get("credits") == null ? 0 : map.get("credits"));
				thisTimesumCreditsMap.put("professionalCourses", map.get("thisTimeSumCredits") == null ? 0 : map.get("thisTimeSumCredits"));
				sumCreditsMap.put("professionalCourses", map.get("sumCredits") == null ? 0 : map.get("sumCredits"));
			}else if(map.get("labelName").equals("非学位课")){
				minNumMap.put("nonDegreeCourses",map.get("minNum") == null ? 0 : map.get("minNum"));
				courseNumMap.put("nonDegreeCourses", map.get("courseNum") == null ? 0 : map.get("courseNum"));
				creditsMap.put("nonDegreeCourses", map.get("credits") == null ? 0 : map.get("credits"));
				thisTimesumCreditsMap.put("nonDegreeCourses", map.get("thisTimeSumCredits") == null ? 0 : map.get("thisTimeSumCredits"));
				sumCreditsMap.put("nonDegreeCourses", map.get("sumCredits") == null ? 0 : map.get("sumCredits"));
			}else if(map.get("labelName").equals("必修环节")){
				minNumMap.put("requiredCourses",map.get("minNum") == null ? 0 : map.get("minNum"));
				courseNumMap.put("requiredCourses", map.get("courseNum") == null ? 0 : map.get("courseNum"));
				creditsMap.put("requiredCourses", map.get("credits") == null ? 0 : map.get("credits"));
				thisTimesumCreditsMap.put("requiredCourses", map.get("thisTimeSumCredits") == null ? 0 : map.get("thisTimeSumCredits"));
				sumCreditsMap.put("requiredCourses", map.get("sumCredits") == null ? 0 : map.get("sumCredits"));
			}else if(map.get("labelName").equals("跨院系或跨门类")){
				minNumMap.put("interFaculty",map.get("minNum") == null ? 0 : map.get("minNum"));
				courseNumMap.put("interFaculty", map.get("courseNum") == null ? 0 : map.get("courseNum"));
				creditsMap.put("interFaculty", map.get("credits") == null ? 0 : map.get("credits"));
				thisTimesumCreditsMap.put("interFaculty", map.get("thisTimeSumCredits") == null ? 0 : map.get("thisTimeSumCredits"));
				sumCreditsMap.put("interFaculty", map.get("sumCredits") == null ? 0 : map.get("sumCredits"));
			}
		}
		resultMap.put("minCourse", minNumMap);
		resultMap.put("selectedCourse", courseNumMap);
		resultMap.put("minCredits", creditsMap);
		resultMap.put("currentElecCredits", thisTimesumCreditsMap);
		resultMap.put("selectedCredits", sumCreditsMap);
		return resultMap;
	}
	
	public Map<String, Object> getAdminElectResultCount(String studentId,ElecContext c, Long calendarId) {

		/** 调用培养：培养方案的课程分类学分 */
    	String culturePath = ServicePathEnum.CULTURESERVICE.getPath("/studentCultureRel/getCultureMsg/{studentId}");
    	RestResult<Map<String, Object>> restResult = restTemplate.getForObject(culturePath,RestResult.class, studentId);
		
		//获取当前已经完成的课程
		Set<PlanCourse> planCourse = c.getPlanCourses();
		Set<CompletedCourse> completedCourses = c.getCompletedCourses();
		
		//获取本学期已选课程
		Set<SelectedCourse> thisSelectedCourses = c.getSelectedCourses();
		for (SelectedCourse selectedCourse : thisSelectedCourses) {
			if (StringUtils.isEmpty(selectedCourse.getLabel())) {
				for (PlanCourse course : planCourse) {
					if (course.getCourseCode().equals(selectedCourse.getCourseCode())) {
						selectedCourse.setLabel(course.getLabel()+"");
					}
				}
			}
		}
		LOG.info("+++++++++++++++++++++++++++++DATA     ZUZHUANG");
		Map<String,Object>minNumMap = new HashMap<String,Object>();
		Map<String,Object>courseNumMap = new HashMap<String,Object>();
		Map<String,Object>creditsMap = new HashMap<String,Object>();
		Map<String,Object>thisTimesumCreditsMap = new HashMap<String,Object>();
		Map<String,Object>sumCreditsMap = new HashMap<String,Object>();
		Map<String,Object>resultMap= new HashMap<String,Object>();
		
		minNumMap.put("publicLessons",0);
		courseNumMap.put("publicLessons", 0);
		creditsMap.put("publicLessons", 0);
		thisTimesumCreditsMap.put("publicLessons", 0);
		sumCreditsMap.put("publicLessons", 0);
		
		minNumMap.put("professionalCourses",0);
		courseNumMap.put("professionalCourses", 0);
		creditsMap.put("professionalCourses", 0);
		thisTimesumCreditsMap.put("professionalCourses", 0);
		sumCreditsMap.put("professionalCourses", 0);
		
		minNumMap.put("nonDegreeCourses",0);
		courseNumMap.put("nonDegreeCourses", 0);
		creditsMap.put("nonDegreeCourses", 0);
		thisTimesumCreditsMap.put("nonDegreeCourses", 0);
		sumCreditsMap.put("nonDegreeCourses", 0);
		
		minNumMap.put("requiredCourses",0);
		courseNumMap.put("requiredCourses", 0);
		creditsMap.put("requiredCourses", 0);
		thisTimesumCreditsMap.put("requiredCourses", 0);
		sumCreditsMap.put("requiredCourses", 0);
		
		minNumMap.put("interFaculty",0);
		courseNumMap.put("interFaculty", 0);
		creditsMap.put("interFaculty", 0);
		thisTimesumCreditsMap.put("interFaculty", 0);
		sumCreditsMap.put("interFaculty", 0);
		for(Entry<String, Object> entry : restResult.getData().entrySet()){
			Map<String,Object> map = (Map<String,Object>)entry.getValue();
			String key = entry.getKey();
			LOG.info("+++++++++++++++++++++++++++++DATA     ZUZHUANG"+key);
			//已完成课程数
			Integer courseNum = 0;
			//已完成学分
			Double sumCredits = 0.0;
			for (CompletedCourse completedCourse : completedCourses) {
				if (completedCourse != null) {
					for (PlanCourse course : planCourse) {
						if (course.getCourseCode().equals(completedCourse.getCourseCode())) {
							completedCourse.setCourseLabelId(course.getLabel());
						}
					}
					if (completedCourse.getCourseLabelId() != null && completedCourse.getCourseLabelId() == Long.parseLong(key.trim())) {
						courseNum ++;
						sumCredits += completedCourse.getCredits();
					}
				}
			}
			//统计本次选课门数
			Integer thisTimecourseNum = 0;
			//统计本次选课学分
			Double thisTimeSumCredits = 0.0;
			for (SelectedCourse thisSelected : thisSelectedCourses) {
				
				if (StringUtils.equalsIgnoreCase(key,thisSelected.getLabel())) {
					thisTimecourseNum ++;
					thisTimeSumCredits += thisSelected.getCredits();
				}
			}
			map.put("courseNum", courseNum+thisTimecourseNum);
			map.put("sumCredits", sumCredits+thisTimeSumCredits);
			map.put("thisTimeSumCredits", thisTimeSumCredits.doubleValue());
			
			if(map.get("labelName").equals("公共学位课")){
				minNumMap.put("publicLessons",map.get("minNum") == null ? 0 : map.get("minNum"));
				courseNumMap.put("publicLessons", map.get("courseNum") == null ? 0 : map.get("courseNum"));
				creditsMap.put("publicLessons", map.get("credits") == null ? 0 : map.get("credits"));
				thisTimesumCreditsMap.put("publicLessons", map.get("thisTimeSumCredits") == null ? 0 : map.get("thisTimeSumCredits"));
				sumCreditsMap.put("publicLessons", map.get("sumCredits") == null ? 0 : map.get("sumCredits"));
				
			}else if(map.get("labelName").equals("专业学位课")){
				minNumMap.put("professionalCourses",map.get("minNum") == null ? 0 : map.get("minNum"));
				courseNumMap.put("professionalCourses", map.get("courseNum") == null ? 0 : map.get("courseNum"));
				creditsMap.put("professionalCourses", map.get("credits") == null ? 0 : map.get("credits"));
				thisTimesumCreditsMap.put("professionalCourses", map.get("thisTimeSumCredits") == null ? 0 : map.get("thisTimeSumCredits"));
				sumCreditsMap.put("professionalCourses", map.get("sumCredits") == null ? 0 : map.get("sumCredits"));
			}else if(map.get("labelName").equals("非学位课")){
				minNumMap.put("nonDegreeCourses",map.get("minNum") == null ? 0 : map.get("minNum"));
				courseNumMap.put("nonDegreeCourses", map.get("courseNum") == null ? 0 : map.get("courseNum"));
				creditsMap.put("nonDegreeCourses", map.get("credits") == null ? 0 : map.get("credits"));
				thisTimesumCreditsMap.put("nonDegreeCourses", map.get("thisTimeSumCredits") == null ? 0 : map.get("thisTimeSumCredits"));
				sumCreditsMap.put("nonDegreeCourses", map.get("sumCredits") == null ? 0 : map.get("sumCredits"));
			}else if(map.get("labelName").equals("必修环节")){
				minNumMap.put("requiredCourses",map.get("minNum") == null ? 0 : map.get("minNum"));
				courseNumMap.put("requiredCourses", map.get("courseNum") == null ? 0 : map.get("courseNum"));
				creditsMap.put("requiredCourses", map.get("credits") == null ? 0 : map.get("credits"));
				thisTimesumCreditsMap.put("requiredCourses", map.get("thisTimeSumCredits") == null ? 0 : map.get("thisTimeSumCredits"));
				sumCreditsMap.put("requiredCourses", map.get("sumCredits") == null ? 0 : map.get("sumCredits"));
			}else if(map.get("labelName").equals("跨院系或跨门类")){
				minNumMap.put("interFaculty",map.get("minNum") == null ? 0 : map.get("minNum"));
				courseNumMap.put("interFaculty", map.get("courseNum") == null ? 0 : map.get("courseNum"));
				creditsMap.put("interFaculty", map.get("credits") == null ? 0 : map.get("credits"));
				thisTimesumCreditsMap.put("interFaculty", map.get("thisTimeSumCredits") == null ? 0 : map.get("thisTimeSumCredits"));
				sumCreditsMap.put("interFaculty", map.get("sumCredits") == null ? 0 : map.get("sumCredits"));
			}
		}
		resultMap.put("minCourse", minNumMap);
		resultMap.put("selectedCourse", courseNumMap);
		resultMap.put("minCredits", creditsMap);
		resultMap.put("currentElecCredits", thisTimesumCreditsMap);
		resultMap.put("selectedCredits", sumCreditsMap);
		return resultMap;
	}

}
