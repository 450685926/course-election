package com.server.edu.election.studentelec.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.ServicePathEnum;
import com.server.edu.common.entity.BeanUtil;
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
import com.server.edu.election.dao.ElecRoundsDao;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.dao.TeachingClassDao;
import com.server.edu.election.dto.ClassTeacherDto;
import com.server.edu.election.dto.ElectionRoundsDto;
import com.server.edu.election.dto.NoSelectCourseStdsDto;
import com.server.edu.election.entity.ElcCourseTake;
import com.server.edu.election.entity.ElcLog;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.service.RebuildCourseChargeService;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ClassTimeUnit;
import com.server.edu.election.studentelec.context.CompletedCourse;
import com.server.edu.election.studentelec.context.ElcCourseResult;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.IElecContext;
import com.server.edu.election.studentelec.context.PlanCourse;
import com.server.edu.election.studentelec.context.SelectedCourse;
import com.server.edu.election.studentelec.context.TimeAndRoom;
import com.server.edu.election.studentelec.dto.ElecTeachClassDto;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutor;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;
import com.server.edu.election.studentelec.rules.AbstractWithdrwRuleExceutor;
import com.server.edu.election.studentelec.service.ElecYjsService;
import com.server.edu.election.studentelec.service.cache.AbstractCacheService;
import com.server.edu.election.studentelec.utils.ElecContextUtil;
import com.server.edu.election.studentelec.utils.Keys;
import com.server.edu.election.studentelec.utils.RetakeCourseUtil;
import com.server.edu.election.util.WeekUtil;
import com.server.edu.election.vo.AllCourseVo;
import com.server.edu.election.vo.ElcLogVo;
import com.server.edu.election.vo.ElectionRuleVo;
import com.server.edu.util.CalUtil;
import com.server.edu.util.CollectionUtil;

/**
 * 研究生选课
 * 
 */
@Service
public class ElecYjsServiceImpl extends AbstractCacheService
    implements ElecYjsService
{
    Logger LOG = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private RoundDataProvider dataProvider;
    
    @Autowired
    private RebuildCourseChargeService chargeService;
    
    @Autowired
    private StringRedisTemplate strTemplate;
    
    @Autowired
    private ElcCourseTakeDao courseTakeDao;
    
    @Autowired
    private StudentDao stuDao;
    
    @Autowired
    private TeachingClassDao classDao;
    
    @Autowired
    private ElcLogDao elcLogDao;
    
    @Autowired
    private ElecRoundsDao elecRoundsDao;
    
    private RestTemplate restTemplate = RestTemplateBuilder.create();
    
    @SuppressWarnings("rawtypes")
    @Override
    public IElecContext doELec(ElecRequest request)
    {
        Long roundId = request.getRoundId();
        String studentId = request.getStudentId();
        Long calendarId = request.getCalendarId();
        Integer chooseObj = request.getChooseObj();
        
        Assert.notNull(calendarId, "calendarId must be not null");
        
        List<AbstractElecRuleExceutor> elecExceutors = new ArrayList<>();
        List<AbstractWithdrwRuleExceutor> cancelExceutors = new ArrayList<>();
        
        // 研究生的管理员代选是没有轮次和规则的
        if (!Objects.equals(ChooseObj.ADMIN.type(), chooseObj))
        {
            List<ElectionRuleVo> rules = dataProvider.getRules(roundId);
            
            // 获取执行规则
            Map<String, AbstractRuleExceutor> map =
                applicationContext.getBeansOfType(AbstractRuleExceutor.class);
            
            for (ElectionRuleVo ruleVo : rules)
            {
                AbstractRuleExceutor excetor = map.get(ruleVo.getServiceName());
                if (null != excetor)
                {
                    excetor.setProjectId(ruleVo.getManagerDeptId());
                    ElectRuleType type =
                        ElectRuleType.valueOf(ruleVo.getType());
                    excetor.setType(type);
                    excetor.setDescription(ruleVo.getName());
                    if (ElectRuleType.WITHDRAW.equals(type))
                    {
                        cancelExceutors
                            .add((AbstractWithdrwRuleExceutor)excetor);
                    }
                    else
                    {
                        elecExceutors.add((AbstractElecRuleExceutor)excetor);
                    }
                }
            }
        }
        ElecContext context = new ElecContext(studentId, calendarId, request);
        
        ElecRespose respose = context.getRespose();
        respose.getSuccessCourses().clear();
        respose.getFailedReasons().clear();
        
        // 退课
        doWithdraw(context, cancelExceutors, request.getWithdrawClassList());
        
        // 研究生的管理员代选是没有轮次和规则的
        if (Objects.equals(ChooseObj.ADMIN.type(), chooseObj))
        {
            // 选课
//            List<ElectionRounds> elecRounds =
//                elecRoundsDao.selectWillBeStartByCalendarId(calendarId);
//            for (ElectionRounds round : elecRounds)
//            {
                doElec(context,elecExceutors,request.getElecClassList(),null,calendarId);
//            }
        }
        else
        {
            ElectionRounds round = dataProvider.getRound(roundId);
            // 选课
            doElec(context, elecExceutors, request.getElecClassList(), round,null);
        }
        
        return context;
    }
    
    /**选课*/
    private void doElec(ElecContext context,
        List<AbstractElecRuleExceutor> exceutors,
        List<ElecTeachClassDto> teachClassIds, ElectionRounds round, Long calendarId)
    {
        if (CollectionUtil.isEmpty(teachClassIds))
        {
            return;
        }
        Collections.sort(exceutors);
        LOG.info("---- exceutors :{} ----", exceutors.size());
        
        ElecRespose respose = context.getRespose();
        Map<String, String> failedReasons = respose.getFailedReasons();
        boolean hasRetakeCourse = false;
        for (ElecTeachClassDto data : teachClassIds)
        {
            Long teachClassId = data.getTeachClassId();
            TeachingClassCache teachClass = new TeachingClassCache();
            if (round != null) {  // 教务员代理选课
            	teachClass = dataProvider.getTeachClass(round.getId(),data.getCourseCode(),teachClassId);
			}else {               // 管理员代理选课
				teachClass = dataProvider.getTeachClassByCalendarId(calendarId,data.getCourseCode(),teachClassId);
			}
            if (teachClass == null)
            {
                failedReasons.put(String.format("%s[%s]",
                    data.getCourseCode(),
                    data.getTeachClassCode()), "教学班不存在无法选课");
                continue;
            }
            boolean allSuccess = true;
            for (AbstractElecRuleExceutor exceutor : exceutors)
            {
                if (!exceutor.checkRule(context, teachClass))
                {
                    // 校验不通过时跳过后面的校验进行下一个
                    allSuccess = false;
                    String key = teachClass.getCourseCodeAndClassCode();
                    if (!failedReasons.containsKey(key))
                    {
                        failedReasons.put(key, exceutor.getDescription());
                    }
                    break;
                }
            }
            // 对校验成功的课程进行入库保存
            if (allSuccess)
            {
                this.saveElc(context, teachClass, ElectRuleType.ELECTION);
                // 判断是否有重修课
                if (!hasRetakeCourse && RetakeCourseUtil.isRetakeCourse(context,
                    teachClass.getCourseCode()))
                {
                    hasRetakeCourse = true;
                }
            }
        }
        // 判断学生是否要重修缴费
        String studentId = context.getStudentInfo().getStudentId();
        if (hasRetakeCourse && !chargeService.isNoNeedPayForRetake(studentId))
        {
            context.getRespose().setData(new HashMap<>());
            context.getRespose().getData().put("retakePay", "true");
        }
    }
    
    /**退课*/
    private void doWithdraw(ElecContext context,
        List<AbstractWithdrwRuleExceutor> exceutors,
        List<ElecTeachClassDto> teachClassIds)
    {
        if (CollectionUtil.isEmpty(teachClassIds))
        {
            return;
        }
        
        Collections.sort(exceutors);
        LOG.info("---- widthdrawExceutors :{} ----", exceutors.size());
        
        ElecRespose respose = context.getRespose();
        Map<String, String> failedReasons = respose.getFailedReasons();
        
        for (ElecTeachClassDto data : teachClassIds)
        {
            Long teachClassId = data.getTeachClassId();
            SelectedCourse teachClass = null;
            Set<SelectedCourse> selectedCourses = context.getSelectedCourses();
            for (SelectedCourse selectCourse : selectedCourses)
            {
                if (selectCourse.getTeachClassId().equals(teachClassId))
                {
                    teachClass = selectCourse;
                    break;
                }
            }
            if (teachClass == null)
            {
                failedReasons.put(String.format("%s[%s]",
                    data.getCourseCode(),
                    data.getTeachClassCode()), "教学班不存在无法退课");
                continue;
            }
            boolean allSuccess = true;
            for (AbstractWithdrwRuleExceutor exceutor : exceutors)
            {
                if (!exceutor.checkRule(context, teachClass))
                {
                    // 校验不通过时跳过后面的校验进行下一个
                    allSuccess = false;
                    String key = teachClass.getCourseCodeAndClassCode();
                    if (!failedReasons.containsKey(key))
                    {
                        failedReasons.put(key, exceutor.getDescription());
                    }
                    break;
                }
            }
            // 对校验成功的课程进行入库保存
            if (allSuccess)
            {
                this.saveElc(context, teachClass, ElectRuleType.WITHDRAW);
                // 删除缓存中的数据
                Iterator<SelectedCourse> iterator = selectedCourses.iterator();
                while (iterator.hasNext())
                {
                    SelectedCourse c = iterator.next();
                    if (c.getTeachClassId().equals(teachClassId))
                    {
                        iterator.remove();
                        break;
                    }
                }
                respose.getSuccessCourses().add(teachClassId);
            }
        }
        
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
        
        Long teachClassId = teachClass.getTeachClassId();
        Long roundId = request.getRoundId();
        ElectionRounds round = new ElectionRounds();
        if (roundId != null) {
        	round = dataProvider.getRound(roundId);
		}else {
			// 通过教学班ID查询轮次
			ElectionRoundsDto electionRoundsDto = elecRoundsDao.getRoundByTeachClassId(teachClassId);
			BeanUtil.copyProperties(round, electionRoundsDto);
		}
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
            // 增加选课人数
            int count = classDao.increElcNumberAtomic(teachClassId);
            if (count == 0)
            {
                respose.getFailedReasons()
                    .put(teachClassId.toString(),
                        I18nUtil.getMsg("ruleCheck.limitCount"));
                return;
            }
            
            ElcCourseTake take = new ElcCourseTake();
            take.setChooseObj(request.getChooseObj());
            take.setCourseCode(courseCode);
            take.setCourseTakeType(courseTakeType);
            take.setCreatedAt(date);
            take.setStudentId(studentId);
            take.setTeachingClassId(teachClassId);
        	take.setCalendarId(round.getCalendarId());
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
            
            if (round.getTurn() != Constants.THIRD_TURN
                && round.getTurn() != Constants.FOURTH_TURN)
            {
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
    
    @Override
    public ElecContext setData(String studentId, ElecContext c, Long roundId,
        Long calendarId)
    {
        
        //每门课上课信息集合
        List<TeachingClassCache> classTimeLists = new ArrayList<>();
        
        //获取学生培养计划中的课程
        Set<PlanCourse> planCourses = c.getPlanCourses();
        
        //获取学生本学期已经选取的课程
        Set<SelectedCourse> selectedCourseSet = c.getSelectedCourses();
        
        List<SelectedCourse> selectedCourses = new ArrayList<>();
        for (SelectedCourse selected : selectedCourseSet)
        {
        	SelectedCourse elcCourseResult = new SelectedCourse();
            for (PlanCourse planCourse : planCourses) {
				if (StringUtils.equalsIgnoreCase(planCourse.getCourseCode(), selected.getCourseCode())) {
					 elcCourseResult.setLabel(planCourse.getLabel()+"");
					 elcCourseResult.setLabelName(planCourse.getLabelName());
					 break;
				}
			}
            elcCourseResult.setChooseObj(selected.getChooseObj());
            elcCourseResult.setTurn(selected.getTurn());
            elcCourseResult.setNature(selected.getNature());
            elcCourseResult.setCourseCode(selected.getCourseCode());
            elcCourseResult.setCourseName(selected.getCourseName());
            elcCourseResult.setCredits(selected.getCredits());
            elcCourseResult.setCalendarName(selected.getCalendarName());
            elcCourseResult
                .setCourseTakeType(selected.getCourseTakeType());
            elcCourseResult
                .setAssessmentMode(selected.getAssessmentMode());
            elcCourseResult.setPublicElec(selected.isPublicElec());
            elcCourseResult.setCalendarId(selected.getCalendarId());
            elcCourseResult.setTerm(selected.getTerm());
            List<TeachingClassCache> teachClasss =
                new ArrayList<TeachingClassCache>();
            if (roundId != null)
            { // 教务员
                teachClasss = dataProvider.getTeachClasss(roundId,
                		selected.getCourseCode());
            }
            else
            { // 管理员
                teachClasss =
                    dataProvider.getTeachClasssbyCalendarId(calendarId,
                    		selected.getCourseCode());
            }
            
            if (CollectionUtil.isNotEmpty(teachClasss))
            {
                for (TeachingClassCache teachClass : teachClasss)
                {
                    Long teachClassId = teachClass.getTeachClassId();
                    if (teachClassId.longValue() == selected
                        .getTeachClassMsg()
                        .longValue())
                    {
                        
                        Integer elecNumber =
                            dataProvider.getElecNumber(teachClassId);
                        teachClass.setCurrentNumber(elecNumber);
                        setClassCache(elcCourseResult, teachClass);
                        classTimeLists.add(teachClass);
                    }
                }
            }
            selectedCourses.add(elcCourseResult);
        }
        selectedCourseSet.clear();
        selectedCourseSet.addAll(selectedCourses);
        //获取学生已完成的课程
        Set<CompletedCourse> completedCourses1 = c.getCompletedCourses();
        
        //从缓存中拿到本轮次排课信息
        HashOperations<String, String, String> ops = strTemplate.opsForHash();
        String key = "";
        if (calendarId == null)
        {
            key = Keys.getRoundCourseKey(roundId); // 学生选课或者教务员代理选课
        }
        else
        {
            key = Keys.getCalendarCourseKey(calendarId); // 管理员代理选课
        }
        Map<String, String> roundsCoursesMap = ops.entries(key);
        List<String> roundsCoursesIdsList = new ArrayList<>();
        for (Entry<String, String> entry : roundsCoursesMap.entrySet())
        {
            String courseCode = entry.getKey();
            roundsCoursesIdsList.add(courseCode);
        }
        
        //培养计划与排课信息的交集（中间变量）
        List<PlanCourse> centerCourse = new ArrayList<>();
        //两个结果取交集 拿到学生培养计划与排课信息的交集
        for (String courseOpenCode : roundsCoursesIdsList)
        {
            for (PlanCourse planCourse : planCourses)
            {
                if (courseOpenCode.equals(planCourse.getCourseCode()))
                {
                    centerCourse.add(planCourse);
                }
            }
        }
        
        //从中间变量中剔除本学期已经选过的课
        List<PlanCourse> optionalGraduateCoursesList = new ArrayList<>();
        for (PlanCourse centerCourseModel : centerCourse)
        {
            Boolean flag = true;
            for (SelectedCourse selectedCourseModel : selectedCourseSet)
            {
                if (selectedCourseModel.getCourseCode()
                    .equals(centerCourseModel.getCourseCode()))
                {
                    flag = false;
                    break;
                }
            }
            if (flag)
            {
                optionalGraduateCoursesList.add(centerCourseModel);
            }
        }
        //从中间变量中剔除已完成的课，得到可选课程
        List<PlanCourse> optionalGraduateCourses = new ArrayList<>();
        for (PlanCourse centerCourses : optionalGraduateCoursesList)
        {
            Boolean flag = true;
            for (CompletedCourse selectedCourseModel : completedCourses1)
            {
                if (selectedCourseModel.getCourseCode()
                    .equals(centerCourses.getCourseCode()))
                {
                    flag = false;
                    break;
                }
            }
            if (flag)
            {
                optionalGraduateCourses.add(centerCourses);
            }
        }
        //可选课程中去除免修免考课程
        Set<String> applyCourses = c.getApplyCourse();
        List<PlanCourse> lastCourse = new ArrayList<>();
        for (PlanCourse centerCourses : optionalGraduateCoursesList)
        {
            Boolean flag = true;
            for (String courseCode : applyCourses)
            {
                if (centerCourses.getCourseCode()
                    .equals(courseCode))
                {
                    flag = false;
                    break;
                }
            }
            if (flag)
            {
            	lastCourse.add(centerCourses);
            }
        }
        List<ElcCourseResult> completedCourses = new ArrayList<>();
        for (PlanCourse completedCourse : lastCourse)
        {
            List<TeachingClassCache> teachClasss =
                new ArrayList<TeachingClassCache>();
            if (roundId != null)
            { // 教务员
                teachClasss = dataProvider.getTeachClasss(roundId,
                    completedCourse.getCourseCode());
            }
            else
            { // 管理员
                teachClasss =
                    dataProvider.getTeachClasssbyCalendarId(calendarId,
                        completedCourse.getCourseCode());
            }
            
            if (CollectionUtil.isNotEmpty(teachClasss))
            {
                for (TeachingClassCache teachClass : teachClasss)
                {
                	ElcCourseResult elcCourseResult = new ElcCourseResult();
                	elcCourseResult.setLabel(completedCourse.getLabel());
                	elcCourseResult.setLabelName(completedCourse.getLabelName());
                	elcCourseResult.setNature(completedCourse.getNature());
                	elcCourseResult.setCourseCode(completedCourse.getCourseCode());
                	elcCourseResult.setCourseName(completedCourse.getCourseName());
                	elcCourseResult.setCredits(completedCourse.getCredits());
                    Long teachClassId = teachClass.getTeachClassId();
                    Integer elecNumber =
                        dataProvider.getElecNumber(teachClassId);
                    teachClass.setCurrentNumber(elecNumber);
                    elcCourseResult.setFaculty(teachClass.getFaculty());
                    elcCourseResult
                        .setTeachClassId(teachClass.getTeachClassId());
                    elcCourseResult
                        .setTeachClassCode(teachClass.getTeachClassCode());
                    elcCourseResult.setTeacherCode(teachClass.getTeacherCode());
                    elcCourseResult.setTeacherName(teachClass.getTeacherName());
                    elcCourseResult
                        .setCurrentNumber(teachClass.getCurrentNumber());
                    elcCourseResult.setMaxNumber(teachClass.getMaxNumber());
                    elcCourseResult
                        .setTimeTableList(teachClass.getTimeTableList());
                    elcCourseResult.setTimes(teachClass.getTimes());
                    
                    Boolean flag = true;
                    String conflictCourse = null;
                    //上课时间是否冲突
                    for (TeachingClassCache teachingClass : classTimeLists)
                    {
                        //已选课程上课时间
                        for (ClassTimeUnit classTimeUnit : teachingClass.getTimes())
                        {
                            //本次选课课程时间
                            for (ClassTimeUnit thisClassTimeUnit : teachClass
                                .getTimes())
                            {
                                //先判断上课周是有重复的，没有则不冲突
                                List<Integer> thisWeeks =
                                    thisClassTimeUnit.getWeeks();
                                List<Integer> weeks =
                                    thisClassTimeUnit.getWeeks();
                                thisWeeks.retainAll(weeks);
                                //教学周无重复
                                if (CollectionUtil.isEmpty(thisWeeks))
                                {
                                    flag = true;
                                }
                                else
                                {
                                    //判断上课周内时间
                                    if (thisClassTimeUnit
                                        .getDayOfWeek() == classTimeUnit
                                            .getDayOfWeek())
                                    {
                                        //判断上课时间
                                        if ((thisClassTimeUnit
                                            .getTimeStart() <= classTimeUnit
                                                .getTimeStart()
                                            && thisClassTimeUnit
                                                .getTimeEnd() >= thisClassTimeUnit
                                                    .getTimeEnd())
                                            || (classTimeUnit
                                                .getTimeStart() <= thisClassTimeUnit
                                                    .getTimeStart()
                                                && classTimeUnit
                                                    .getTimeEnd() >= classTimeUnit
                                                        .getTimeEnd())
                                            || (classTimeUnit
                                                .getTimeStart() <= thisClassTimeUnit
                                                    .getTimeStart()
                                                && classTimeUnit
                                                    .getTimeStart() >= thisClassTimeUnit
                                                        .getTimeEnd())
                                            || (classTimeUnit
                                                .getTimeEnd() <= thisClassTimeUnit
                                                    .getTimeStart()
                                                && classTimeUnit
                                                    .getTimeEnd() >= thisClassTimeUnit
                                                        .getTimeEnd())
                                            || (thisClassTimeUnit
                                                .getTimeStart() <= classTimeUnit
                                                    .getTimeStart()
                                                && thisClassTimeUnit
                                                    .getTimeStart() >= classTimeUnit
                                                        .getTimeEnd())
                                            || (thisClassTimeUnit
                                                .getTimeEnd() <= classTimeUnit
                                                    .getTimeStart()
                                                && thisClassTimeUnit
                                                    .getTimeEnd() >= classTimeUnit
                                                        .getTimeEnd()))
                                        {
                                            flag = false;
                                            conflictCourse = teachingClass.getCourseCode();
                                            break;
                                        }
                                        else
                                        {
                                            flag = true;
                                        }
                                    }
                                    else
                                    {
                                        flag = true;
                                    }
                                }
                                if (!flag)
                                {
                                	break;
                                }
                            }
                            if (!flag)
                            {
                                break;
                            }
                        }
                        if (!flag)
                        {
                            break;
                        }
                    }
                    if (flag)
                    {
                        elcCourseResult.setIsConflict(Constants.ZERO);
                        elcCourseResult.setConflictCourse(null);
                    }
                    else
                    {
                        elcCourseResult.setIsConflict(-Constants.ONE);
                        elcCourseResult.setConflictCourse(conflictCourse);
                    }
                    completedCourses.add(elcCourseResult);
                }
            }
        }
        Map<String, Object> elecResult = new HashMap<>();
        if (roundId != null)
        { // 教务员
            elecResult = getElectResultCount(studentId, roundId);
        }
        else
        { // 管理员
            elecResult = getAdminElectResultCount(studentId, c, calendarId);
        }
        c.setSelectedCourses(selectedCourseSet);
        c.setOptionalCourses(completedCourses);
        c.setElecResult(elecResult);
        return c;
    }
    
    private void setClassCache(TeachingClassCache newClassCache,
        TeachingClassCache oldClassCache)
    {
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
    public PageResult<NoSelectCourseStdsDto> findAgentElcStudentList(
        PageCondition<NoSelectCourseStdsDto> condition)
    {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        
        NoSelectCourseStdsDto noSelectCourseStds = condition.getCondition();
        Page<NoSelectCourseStdsDto> agentElcStudentList =
            courseTakeDao.findAgentElcStudentList(noSelectCourseStds);
        return new PageResult<>(agentElcStudentList);
    }
    
    @Override
    public Map<String, Object> getElectResultCount(String studentId,
        Long roundId)
    {
        
        /** 调用培养：培养方案的课程分类学分 */
        String culturePath = ServicePathEnum.CULTURESERVICE
            .getPath("/studentCultureRel/getCultureMsg/{studentId}");
        RestResult<Map<String, Object>> restResult =
            restTemplate.getForObject(culturePath, RestResult.class, studentId);
        
        RoundDataProvider dataProvider =
            SpringUtils.getBean(RoundDataProvider.class);
        //获取当前选课轮次
        ElectionRounds round = dataProvider.getRound(roundId);
        
        ElecContextUtil elecContextUtil =
            ElecContextUtil.create(studentId, round.getCalendarId());
        
        //获取当前已经完成的课程
        Set<PlanCourse> planCourse =
            elecContextUtil.getSet("PlanCourses", PlanCourse.class);
        Set<CompletedCourse> completedCourses =
            elecContextUtil.getSet("CompletedCourses", CompletedCourse.class);
        
        //获取本学期已选课程
        Set<SelectedCourse> selectedCourses =
            elecContextUtil.getSet("SelectedCourses", SelectedCourse.class);
        List<SelectedCourse> thisSelectedCourses = new ArrayList<>();
        for (SelectedCourse selectedCourse : selectedCourses)
        {
        	
        	for (PlanCourse course : planCourse)
            {
                if (course.getCourseCode()
                    .equals(selectedCourse.getCourseCode()))
                {
                    selectedCourse.setLabel(course.getLabel() + "");
                    selectedCourse.setLabelName(course.getLabelName());
                }
            }
            //获取本次选课信息
            if ( round.getTurn() == selectedCourse.getTurn().intValue())
            {
                //已完成课程数
                thisSelectedCourses.add(selectedCourse);
                
            }
        }
        LOG.info("+++++++++++++++++++++++++++++DATA     ZUZHUANG");
        Map<String, Object> minNumMap = new HashMap<String, Object>();
        Map<String, Object> courseNumMap = new HashMap<String, Object>();
        Map<String, Object> creditsMap = new HashMap<String, Object>();
        Map<String, Object> thisTimesumCreditsMap =
            new HashMap<String, Object>();
        Map<String, Object> sumCreditsMap = new HashMap<String, Object>();
        Map<String, Object> resultMap = new HashMap<String, Object>();
        
        minNumMap.put("publicLessons", 0);
        courseNumMap.put("publicLessons", 0);
        creditsMap.put("publicLessons", 0);
        thisTimesumCreditsMap.put("publicLessons", 0);
        sumCreditsMap.put("publicLessons", 0);
        
        minNumMap.put("professionalCourses", 0);
        courseNumMap.put("professionalCourses", 0);
        creditsMap.put("professionalCourses", 0);
        thisTimesumCreditsMap.put("professionalCourses", 0);
        sumCreditsMap.put("professionalCourses", 0);
        
        minNumMap.put("nonDegreeCourses", 0);
        courseNumMap.put("nonDegreeCourses", 0);
        creditsMap.put("nonDegreeCourses", 0);
        thisTimesumCreditsMap.put("nonDegreeCourses", 0);
        sumCreditsMap.put("nonDegreeCourses", 0);
        
        minNumMap.put("requiredCourses", 0);
        courseNumMap.put("requiredCourses", 0);
        creditsMap.put("requiredCourses", 0);
        thisTimesumCreditsMap.put("requiredCourses", 0);
        sumCreditsMap.put("requiredCourses", 0);
        
        minNumMap.put("interFaculty", 0);
        courseNumMap.put("interFaculty", 0);
        creditsMap.put("interFaculty", 0);
        thisTimesumCreditsMap.put("interFaculty", 0);
        sumCreditsMap.put("interFaculty", 0);
        for (Entry<String, Object> entry : restResult.getData().entrySet())
        {
            Map<String, Object> map = (Map<String, Object>)entry.getValue();
            String key = entry.getKey();
            LOG.info("+++++++++++++++++++++++++++++DATA     ZUZHUANG" + key);
            //已完成课程数
            Integer courseNum = 0;
            //已完成学分
            Double sumCredits = 0.0;
            for (CompletedCourse completedCourse : completedCourses)
            {
                if (completedCourse != null)
                {
                    for (PlanCourse course : planCourse)
                    {
                        if (course.getCourseCode()
                            .equals(completedCourse.getCourseCode()))
                        {
                            completedCourse.setCourseLabelId(course.getLabel());
                            completedCourse.setLabelName(course.getLabelName());
                        }
                    }
                    if (completedCourse.getCourseLabelId() != null
                        && completedCourse.getCourseLabelId() == Long
                            .parseLong(key.trim()))
                    {
                        courseNum++;
                        sumCredits += completedCourse.getCredits();
                    }
                }
            }
            //统计本次选课门数
            Integer thisTimecourseNum = 0;
            //统计本次选课学分
            Double thisTimeSumCredits = 0.0;
            for (SelectedCourse thisSelected : thisSelectedCourses)
            {
                
                if (StringUtils.equalsIgnoreCase(key, thisSelected.getLabel()))
                {
                    thisTimecourseNum++;
                    thisTimeSumCredits += thisSelected.getCredits();
                }
            }
            map.put("courseNum", courseNum + thisTimecourseNum);
            map.put("sumCredits", sumCredits + thisTimeSumCredits);
            map.put("thisTimeSumCredits", thisTimeSumCredits.doubleValue());
            
            if (map.get("labelName").equals("公共学位课"))
            {
                minNumMap.put("publicLessons",
                    map.get("minNum") == null ? 0 : map.get("minNum"));
                courseNumMap.put("publicLessons",
                    map.get("courseNum") == null ? 0 : map.get("courseNum"));
                creditsMap.put("publicLessons",
                    map.get("credits") == null ? 0 : map.get("credits"));
                thisTimesumCreditsMap.put("publicLessons",
                    map.get("thisTimeSumCredits") == null ? 0
                        : map.get("thisTimeSumCredits"));
                sumCreditsMap.put("publicLessons",
                    map.get("sumCredits") == null ? 0 : map.get("sumCredits"));
                
            }
            else if (map.get("labelName").equals("专业学位课"))
            {
                minNumMap.put("professionalCourses",
                    map.get("minNum") == null ? 0 : map.get("minNum"));
                courseNumMap.put("professionalCourses",
                    map.get("courseNum") == null ? 0 : map.get("courseNum"));
                creditsMap.put("professionalCourses",
                    map.get("credits") == null ? 0 : map.get("credits"));
                thisTimesumCreditsMap.put("professionalCourses",
                    map.get("thisTimeSumCredits") == null ? 0
                        : map.get("thisTimeSumCredits"));
                sumCreditsMap.put("professionalCourses",
                    map.get("sumCredits") == null ? 0 : map.get("sumCredits"));
            }
            else if (map.get("labelName").equals("非学位课"))
            {
                minNumMap.put("nonDegreeCourses",
                    map.get("minNum") == null ? 0 : map.get("minNum"));
                courseNumMap.put("nonDegreeCourses",
                    map.get("courseNum") == null ? 0 : map.get("courseNum"));
                creditsMap.put("nonDegreeCourses",
                    map.get("credits") == null ? 0 : map.get("credits"));
                thisTimesumCreditsMap.put("nonDegreeCourses",
                    map.get("thisTimeSumCredits") == null ? 0
                        : map.get("thisTimeSumCredits"));
                sumCreditsMap.put("nonDegreeCourses",
                    map.get("sumCredits") == null ? 0 : map.get("sumCredits"));
            }
            else if (map.get("labelName").equals("必修环节"))
            {
                minNumMap.put("requiredCourses",
                    map.get("minNum") == null ? 0 : map.get("minNum"));
                courseNumMap.put("requiredCourses",
                    map.get("courseNum") == null ? 0 : map.get("courseNum"));
                creditsMap.put("requiredCourses",
                    map.get("credits") == null ? 0 : map.get("credits"));
                thisTimesumCreditsMap.put("requiredCourses",
                    map.get("thisTimeSumCredits") == null ? 0
                        : map.get("thisTimeSumCredits"));
                sumCreditsMap.put("requiredCourses",
                    map.get("sumCredits") == null ? 0 : map.get("sumCredits"));
            }
            else if (map.get("labelName").equals("跨院系或跨门类"))
            {
                minNumMap.put("interFaculty",
                    map.get("minNum") == null ? 0 : map.get("minNum"));
                courseNumMap.put("interFaculty",
                    map.get("courseNum") == null ? 0 : map.get("courseNum"));
                creditsMap.put("interFaculty",
                    map.get("credits") == null ? 0 : map.get("credits"));
                thisTimesumCreditsMap.put("interFaculty",
                    map.get("thisTimeSumCredits") == null ? 0
                        : map.get("thisTimeSumCredits"));
                sumCreditsMap.put("interFaculty",
                    map.get("sumCredits") == null ? 0 : map.get("sumCredits"));
            }
        }
        resultMap.put("minCourse", minNumMap);
        resultMap.put("selectedCourse", courseNumMap);
        resultMap.put("minCredits", creditsMap);
        resultMap.put("currentElecCredits", thisTimesumCreditsMap);
        resultMap.put("selectedCredits", sumCreditsMap);
        return resultMap;
    }
    
    @Override
    public List<TeachingClassCache> arrangementCourses(AllCourseVo allCourseVo)
    {
        List<ElcCourseResult> list = stuDao.getAllCourse(allCourseVo);
        List<TeachingClassCache> lessons =
            new ArrayList<TeachingClassCache>(list.size());
        
        //从缓存中拿到本轮次排课信息
        //List<Long> teachClassIds = list.stream().map(ElcCourseResult::getTeachClassId).collect(Collectors.toList());
        List<String> teachClassIds = new ArrayList<String>(list.size());
        for (ElcCourseResult elcCourseResult : list)
        {
            teachClassIds.add(elcCourseResult.getTeachClassId() + "");
        }
        
        HashOperations<String, String, TeachingClassCache> hash =
            opsTeachClass();
        lessons = hash.multiGet(Keys.getClassKey(), teachClassIds);
        // 过滤null
        lessons = lessons.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
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
    
    private List<TimeAndRoom> getTimeById(Long teachingClassId)
    {
        List<TimeAndRoom> list = new ArrayList<>();
        List<ClassTeacherDto> classTimeAndRoom =
            courseTakeDao.findClassTimeAndRoomStr(teachingClassId);
        if (CollectionUtil.isNotEmpty(classTimeAndRoom))
        {
            for (ClassTeacherDto classTeacherDto : classTimeAndRoom)
            {
                TimeAndRoom time = new TimeAndRoom();
                Integer dayOfWeek = classTeacherDto.getDayOfWeek();
                Integer timeStart = classTeacherDto.getTimeStart();
                Integer timeEnd = classTeacherDto.getTimeEnd();
                String roomID = classTeacherDto.getRoomID();
                String weekNumber = classTeacherDto.getWeekNumberStr();
                Long timeId = classTeacherDto.getTimeId();
                String[] str = weekNumber.split(",");
                
                List<Integer> weeks = Arrays.asList(str)
                    .stream()
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
                List<String> weekNums =
                    CalUtil.getWeekNums(weeks.toArray(new Integer[] {}));
                String weekNumStr = weekNums.toString();//周次
                String weekstr = WeekUtil.findWeek(dayOfWeek);//星期
                String timeStr = weekstr + " " + timeStart + "-" + timeEnd + " "
                    + weekNumStr + " ";
                time.setTimeId(timeId);
                time.setTimeAndRoom(timeStr);
                time.setRoomId(roomID);
                list.add(time);
            }
        }
        return list;
    }
    
    public Map<String, Object> getAdminElectResultCount(String studentId,
        ElecContext c, Long calendarId)
    {
        
        /** 调用培养：培养方案的课程分类学分 */
        String culturePath = ServicePathEnum.CULTURESERVICE
            .getPath("/studentCultureRel/getCultureMsg/{studentId}");
        RestResult<Map<String, Object>> restResult =
            restTemplate.getForObject(culturePath, RestResult.class, studentId);
        
        //获取当前已经完成的课程
        Set<PlanCourse> planCourse = c.getPlanCourses();
        Set<CompletedCourse> completedCourses = c.getCompletedCourses();
        
        //获取本学期已选课程
        Set<SelectedCourse> thisSelectedCourses = c.getSelectedCourses();
        for (SelectedCourse selectedCourse : thisSelectedCourses)
        {
            if (StringUtils.isEmpty(selectedCourse.getLabel()))
            {
                for (PlanCourse course : planCourse)
                {
                    if (course.getCourseCode()
                        .equals(selectedCourse.getCourseCode()))
                    {
                        selectedCourse.setLabel(course.getLabel() + "");
                    }
                }
            }
        }
        LOG.info("+++++++++++++++++++++++++++++DATA     ZUZHUANG");
        Map<String, Object> minNumMap = new HashMap<String, Object>();
        Map<String, Object> courseNumMap = new HashMap<String, Object>();
        Map<String, Object> creditsMap = new HashMap<String, Object>();
        Map<String, Object> thisTimesumCreditsMap =
            new HashMap<String, Object>();
        Map<String, Object> sumCreditsMap = new HashMap<String, Object>();
        Map<String, Object> resultMap = new HashMap<String, Object>();
        
        minNumMap.put("publicLessons", 0);
        courseNumMap.put("publicLessons", 0);
        creditsMap.put("publicLessons", 0);
        thisTimesumCreditsMap.put("publicLessons", 0);
        sumCreditsMap.put("publicLessons", 0);
        
        minNumMap.put("professionalCourses", 0);
        courseNumMap.put("professionalCourses", 0);
        creditsMap.put("professionalCourses", 0);
        thisTimesumCreditsMap.put("professionalCourses", 0);
        sumCreditsMap.put("professionalCourses", 0);
        
        minNumMap.put("nonDegreeCourses", 0);
        courseNumMap.put("nonDegreeCourses", 0);
        creditsMap.put("nonDegreeCourses", 0);
        thisTimesumCreditsMap.put("nonDegreeCourses", 0);
        sumCreditsMap.put("nonDegreeCourses", 0);
        
        minNumMap.put("requiredCourses", 0);
        courseNumMap.put("requiredCourses", 0);
        creditsMap.put("requiredCourses", 0);
        thisTimesumCreditsMap.put("requiredCourses", 0);
        sumCreditsMap.put("requiredCourses", 0);
        
        minNumMap.put("interFaculty", 0);
        courseNumMap.put("interFaculty", 0);
        creditsMap.put("interFaculty", 0);
        thisTimesumCreditsMap.put("interFaculty", 0);
        sumCreditsMap.put("interFaculty", 0);
        for (Entry<String, Object> entry : restResult.getData().entrySet())
        {
            Map<String, Object> map = (Map<String, Object>)entry.getValue();
            String key = entry.getKey();
            LOG.info("+++++++++++++++++++++++++++++DATA     ZUZHUANG" + key);
            //已完成课程数
            Integer courseNum = 0;
            //已完成学分
            Double sumCredits = 0.0;
            for (CompletedCourse completedCourse : completedCourses)
            {
                if (completedCourse != null)
                {
                    for (PlanCourse course : planCourse)
                    {
                        if (course.getCourseCode()
                            .equals(completedCourse.getCourseCode()))
                        {
                            completedCourse.setCourseLabelId(course.getLabel());
                        }
                    }
                    if (completedCourse.getCourseLabelId() != null
                        && completedCourse.getCourseLabelId() == Long
                            .parseLong(key.trim()))
                    {
                        courseNum++;
                        sumCredits += completedCourse.getCredits();
                    }
                }
            }
            //统计本次选课门数
            Integer thisTimecourseNum = 0;
            //统计本次选课学分
            Double thisTimeSumCredits = 0.0;
            for (SelectedCourse thisSelected : thisSelectedCourses)
            {
                
                if (StringUtils.equalsIgnoreCase(key, thisSelected.getLabel()))
                {
                    thisTimecourseNum++;
                    thisTimeSumCredits += thisSelected.getCredits();
                }
            }
            map.put("courseNum", courseNum + thisTimecourseNum);
            map.put("sumCredits", sumCredits + thisTimeSumCredits);
            map.put("thisTimeSumCredits", thisTimeSumCredits.doubleValue());
            
            if (map.get("labelName").equals("公共学位课"))
            {
                minNumMap.put("publicLessons",
                    map.get("minNum") == null ? 0 : map.get("minNum"));
                courseNumMap.put("publicLessons",
                    map.get("courseNum") == null ? 0 : map.get("courseNum"));
                creditsMap.put("publicLessons",
                    map.get("credits") == null ? 0 : map.get("credits"));
                thisTimesumCreditsMap.put("publicLessons",
                    map.get("thisTimeSumCredits") == null ? 0
                        : map.get("thisTimeSumCredits"));
                sumCreditsMap.put("publicLessons",
                    map.get("sumCredits") == null ? 0 : map.get("sumCredits"));
                
            }
            else if (map.get("labelName").equals("专业学位课"))
            {
                minNumMap.put("professionalCourses",
                    map.get("minNum") == null ? 0 : map.get("minNum"));
                courseNumMap.put("professionalCourses",
                    map.get("courseNum") == null ? 0 : map.get("courseNum"));
                creditsMap.put("professionalCourses",
                    map.get("credits") == null ? 0 : map.get("credits"));
                thisTimesumCreditsMap.put("professionalCourses",
                    map.get("thisTimeSumCredits") == null ? 0
                        : map.get("thisTimeSumCredits"));
                sumCreditsMap.put("professionalCourses",
                    map.get("sumCredits") == null ? 0 : map.get("sumCredits"));
            }
            else if (map.get("labelName").equals("非学位课"))
            {
                minNumMap.put("nonDegreeCourses",
                    map.get("minNum") == null ? 0 : map.get("minNum"));
                courseNumMap.put("nonDegreeCourses",
                    map.get("courseNum") == null ? 0 : map.get("courseNum"));
                creditsMap.put("nonDegreeCourses",
                    map.get("credits") == null ? 0 : map.get("credits"));
                thisTimesumCreditsMap.put("nonDegreeCourses",
                    map.get("thisTimeSumCredits") == null ? 0
                        : map.get("thisTimeSumCredits"));
                sumCreditsMap.put("nonDegreeCourses",
                    map.get("sumCredits") == null ? 0 : map.get("sumCredits"));
            }
            else if (map.get("labelName").equals("必修环节"))
            {
                minNumMap.put("requiredCourses",
                    map.get("minNum") == null ? 0 : map.get("minNum"));
                courseNumMap.put("requiredCourses",
                    map.get("courseNum") == null ? 0 : map.get("courseNum"));
                creditsMap.put("requiredCourses",
                    map.get("credits") == null ? 0 : map.get("credits"));
                thisTimesumCreditsMap.put("requiredCourses",
                    map.get("thisTimeSumCredits") == null ? 0
                        : map.get("thisTimeSumCredits"));
                sumCreditsMap.put("requiredCourses",
                    map.get("sumCredits") == null ? 0 : map.get("sumCredits"));
            }
            else if (map.get("labelName").equals("跨院系或跨门类"))
            {
                minNumMap.put("interFaculty",
                    map.get("minNum") == null ? 0 : map.get("minNum"));
                courseNumMap.put("interFaculty",
                    map.get("courseNum") == null ? 0 : map.get("courseNum"));
                creditsMap.put("interFaculty",
                    map.get("credits") == null ? 0 : map.get("credits"));
                thisTimesumCreditsMap.put("interFaculty",
                    map.get("thisTimeSumCredits") == null ? 0
                        : map.get("thisTimeSumCredits"));
                sumCreditsMap.put("interFaculty",
                    map.get("sumCredits") == null ? 0 : map.get("sumCredits"));
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
