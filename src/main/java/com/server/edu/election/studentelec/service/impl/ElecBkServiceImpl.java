package com.server.edu.election.studentelec.service.impl;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.server.edu.common.ServicePathEnum;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.election.dao.*;
import com.server.edu.election.entity.*;
import com.server.edu.election.rpc.BaseresServiceInvoker;
import com.server.edu.election.studentelec.context.*;
import com.server.edu.election.studentelec.context.bk.CompletedCourse;
import com.server.edu.election.studentelec.context.bk.PlanCourse;
import com.server.edu.election.util.EmailSend;
import com.server.edu.election.util.TableIndexUtil;
import com.server.edu.election.vo.ElcCourseTakeVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import com.server.edu.election.rpc.CultureSerivceInvoker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.server.edu.common.entity.StudentPlanCoure;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.ChooseObj;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.constants.ElectRuleType;
import com.server.edu.election.service.ElectionApplyService;
import com.server.edu.election.service.RebuildCourseChargeService;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.context.bk.SelectedCourse;
import com.server.edu.election.studentelec.dto.ElecTeachClassDto;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutorBk;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;
import com.server.edu.election.studentelec.rules.AbstractWithdrwRuleExceutorBk;
import com.server.edu.election.studentelec.rules.bk.LimitCountCheckerRule;
import com.server.edu.election.studentelec.service.ElecBkService;
import com.server.edu.election.studentelec.service.cache.TeachClassCacheService;
import com.server.edu.election.studentelec.utils.RetakeCourseUtil;
import com.server.edu.election.vo.ElcLogVo;
import com.server.edu.election.vo.ElectionRuleVo;
import com.server.edu.util.CollectionUtil;
import tk.mybatis.mapper.entity.Example;

/**
 * 本科生选课
 * 
 */
@Service
public class ElecBkServiceImpl implements ElecBkService
{
    Logger LOG = LoggerFactory.getLogger(getClass());

    private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private CourseDao courseDao;

    @Autowired
    private RoundDataProvider dataProvider;
    
    @Autowired
    private RebuildCourseChargeService chargeService;
    
    @Autowired
    private ElcCourseTakeDao courseTakeDao;
    
    @Autowired
    private TeachingClassDao classDao;
    
    @Autowired
    private ElcLogDao elcLogDao;

    @Autowired
    private ElectionConstantsDao constantsDao;
    
    @Autowired
    private ElectionApplyService electionApplyService;
    
    @Autowired
    private TeachClassCacheService teachClassCacheService;

    @SuppressWarnings("rawtypes")
    @Override
    public IElecContext doELec(ElecRequest request)
    {
        Long roundId = request.getRoundId();
        String studentId = request.getStudentId();
        Long calendarId = request.getCalendarId();
        
        Assert.notNull(calendarId, "calendarId must be not null");
        
        ElecContextBk context =
            new ElecContextBk(studentId, calendarId, request);
        
        List<ElectionRuleVo> rules = dataProvider.getRules(roundId);
        
        List<AbstractElecRuleExceutorBk> elecExceutors = new ArrayList<>();
        List<AbstractWithdrwRuleExceutorBk> cancelExceutors = new ArrayList<>();
        // 获取执行规则
        Map<String, AbstractRuleExceutor> map =
            applicationContext.getBeansOfType(AbstractRuleExceutor.class);
        
        for (ElectionRuleVo ruleVo : rules)
        {
            AbstractRuleExceutor excetor = map.get(ruleVo.getServiceName());
            if (null != excetor)
            {
                excetor.setProjectId(ruleVo.getManagerDeptId());
                ElectRuleType type = ElectRuleType.valueOf(ruleVo.getType());
                excetor.setType(type);
                excetor.setDescription(ruleVo.getName());
                if (ElectRuleType.WITHDRAW.equals(type))
                {
                    cancelExceutors.add((AbstractWithdrwRuleExceutorBk)excetor);
                }
                else if(ElectRuleType.ELECTION.equals(type))
                {
                    elecExceutors.add((AbstractElecRuleExceutorBk)excetor);
                }
            }
        }
        
        ElecRespose respose = context.getRespose();
        respose.getSuccessCourses().clear();
        respose.getFailedReasons().clear();
        //判断是否存在换班的情况
        List<ElecTeachClassDto> withdrawClassList = request.getWithdrawClassList();
        List<ElecTeachClassDto> elecClassList = request.getElecClassList();

        List<String> withdrawList = new ArrayList<String>(withdrawClassList.stream().map(ElecTeachClassDto :: getCourseCode).collect(Collectors.toList()));

        List<String> elecList = new ArrayList<String>(elecClassList.stream().map(ElecTeachClassDto :: getCourseCode).collect(Collectors.toList()));

        //拿到换班的课程
        List<String> withdrawAndElecList = new ArrayList<>();
        if(CollectionUtil.isNotEmpty(withdrawList) && CollectionUtil.isNotEmpty(elecList)){
            withdrawAndElecList = withdrawList.stream().filter(item -> elecList.contains(item)).collect(Collectors.toList());
        }
        if(CollectionUtil.isNotEmpty(withdrawAndElecList)){
            Map<String, ElecTeachClassDto> withdrawClassMap = withdrawClassList.stream().collect(Collectors.toMap(ElecTeachClassDto::getCourseCode, elecTeachClass -> elecTeachClass));
            Map<String, ElecTeachClassDto> elecClassMap = elecClassList.stream().collect(Collectors.toMap(ElecTeachClassDto::getCourseCode, elecTeachClass -> elecTeachClass));
            //返回可以退课后可以选课的集合
            List<String> goodList =  checkWithdrawAndEleRule(withdrawAndElecList,withdrawClassMap,cancelExceutors,elecClassMap,elecExceutors,context,roundId);
            //拿到不能完成完整加退课流程的集合
            List<String> badList = withdrawAndElecList.stream().filter(item -> !goodList.contains(item)).collect(Collectors.toList());
            //移除这些不能完成换班的课程
            for (String badCourse : badList) {
                withdrawClassMap.remove(badCourse);
                elecClassMap.remove(badCourse);
            }
            //将map转化为list
            withdrawClassList = withdrawClassMap.values().stream().collect(Collectors.toList());
            elecClassList = elecClassMap.values().stream().collect(Collectors.toList());
        }


        // 退课
        doWithdraw(context, cancelExceutors, withdrawClassList);
        
        // 选课
        ElectionRounds round = dataProvider.getRound(roundId);
        doElec(context, elecExceutors, elecClassList, round);
        
        return context;
    }

    private List<String> checkWithdrawAndEleRule(List<String> withdrawAndElecList, Map<String, ElecTeachClassDto> withdrawClassMap, List<AbstractWithdrwRuleExceutorBk> cancelExceutors, Map<String, ElecTeachClassDto> elecClassMap, List<AbstractElecRuleExceutorBk> elecExceutors, ElecContextBk context,Long roundId) {
        ElecRespose respose = context.getRespose();
        ElectionRounds round = dataProvider.getRound(roundId);

        ElecRequest request = context.getRequest();
        StudentInfoCache studentInfo = context.getStudentInfo();
        Map<String, String> failedReasons = respose.getFailedReasons();

        //可以进行后续完整操作的课程集合
        List<String> goodList = new ArrayList<>(20);

        for (String courseCode : withdrawAndElecList) {
            //先校验退课
            //拿到退课的教学班
            ElecTeachClassDto withdrawTeachClass = withdrawClassMap.get(courseCode);
            Long withdrawTeachClassId = withdrawTeachClass.getTeachClassId();
            SelectedCourse teachClass = null;
            Set<SelectedCourse> selectedCourses = context.getSelectedCourses();
            for (SelectedCourse selectCourse : selectedCourses)
            {
                if (selectCourse.getCourse()
                        .getTeachClassId()
                        .equals(withdrawTeachClassId))
                {
                    teachClass = selectCourse;
                    break;
                }
            }
            if (teachClass == null)
            {
                //查询数据库，看这个教学班是否存在
                TeachingClass teachingClass = classDao.selectByPrimaryKey(withdrawTeachClassId);
                if (teachingClass != null){
                    List<ElcCourseTakeVo> elcCourseTakeVo = courseTakeDao.findElcCourse(studentInfo.getStudentId(), round.getCalendarId(),TableIndexUtil.getIndex(round.getCalendarId()), courseCode);
                    if (CollectionUtil.isNotEmpty(elcCourseTakeVo)){
                        ElcCourseTakeVo c = elcCourseTakeVo.get(0);
                        teachClass = new SelectedCourse();
                        TeachingClassCache lesson = new TeachingClassCache();
                        lesson.setApply(c.getApply());
                        lesson.setCourseCode(c.getCourseCode());
                        lesson.setCourseName(c.getCourseName());
                        lesson.setCalendarId(c.getCalendarId());
                        lesson.setTeachClassId(c.getTeachingClassId());
                        lesson.setTeachClassCode(c.getTeachingClassCode());
                        lesson.setFaculty(c.getFaculty());
                        lesson.setTerm(c.getTerm());
                        teachClass.setCourse(lesson);
                        teachClass.setChooseObj(c.getChooseObj());
                        teachClass.setCourseTakeType(c.getCourseTakeType());
                        teachClass.setTurn(c.getTurn());
                    }else{
                        failedReasons.put(String.format("%s[%s]",
                                withdrawTeachClass.getCourseCode(),
                                withdrawTeachClass.getTeachClassCode()), "教学班不存在无法退课");
                        continue;
                    }
                    
                }else{
                    failedReasons.put(String.format("%s[%s]",
                            withdrawTeachClass.getCourseCode(),
                            withdrawTeachClass.getTeachClassCode()), "教学班不存在无法退课");
                    continue;
                }
            }
            Collections.sort(cancelExceutors);
            boolean withdrawSuccess = true;
            for (AbstractWithdrwRuleExceutorBk exceutor : cancelExceutors)
            {
                if (!exceutor.checkRule(context, teachClass))
                {
                    // 校验不通过时跳过后面的校验进行下一个
                    withdrawSuccess = false;
                    String key =
                            teachClass.getCourse().getTeachClassCode() + teachClass.getCourse().getCourseName();
                    if (!failedReasons.containsKey(key))
                    {
                        failedReasons.put(key, exceutor.getDescription());
                    }
                    break;
                }
            }
            if(withdrawSuccess){
                //再校验选课
                boolean hasRetakeCourse = false;
                //拿到退课的教学班
                ElecTeachClassDto data = elecClassMap.get(courseCode);
                Collections.sort(elecExceutors);
                Long elecTeachClassId = data.getTeachClassId();
                TeachingClassCache elecTeachClass =
                        dataProvider.getTeachClass(round.getId(),
                                data.getCourseCode(),
                                elecTeachClassId);
                if (elecTeachClass == null)
                {
                    failedReasons.put(String.format("%s[%s]",
                            data.getCourseCode(),
                            data.getTeachClassCode()), "教学班不存在无法选课");
                    continue;
                }

                //校验选课时先把课程从context中剔除掉
                Iterator<SelectedCourse> iterator = selectedCourses.iterator();
                while (iterator.hasNext())
                {
                    SelectedCourse c = iterator.next();
                    if (c.getCourse().getTeachClassId().equals(withdrawTeachClassId))
                    {
                        iterator.remove();
                        break;
                    }
                }
                boolean elecSuccess = true;
                for (AbstractElecRuleExceutorBk exceutor : elecExceutors)
                {
                    if (!exceutor.checkRule(context, elecTeachClass))
                    {
                        // 校验不通过时跳过后面的校验进行下一个
                        elecSuccess = false;
                        String key = elecTeachClass.getTeachClassCode() + elecTeachClass.getCourseName();
                        if (!failedReasons.containsKey(key))
                        {
                            failedReasons.put(key, exceutor.getDescription());
                        }
                        break;
                    }
                }
                if (elecSuccess){
                    goodList.add(courseCode);
                }
                //不管成功与否，将数据放回去
                SelectedCourse course = new SelectedCourse(teachClass.getCourse());

                int index = TableIndexUtil.getIndex(round.getCalendarId());
                List<ElcCourseTakeVo> elcCourseTakeVo = courseTakeDao.findElcCourse(studentInfo.getStudentId(), round.getCalendarId(),index, teachClass.getCourse().getCourseCode());
                course.setTurn(elcCourseTakeVo.get(0).getTurn());
                course.setCourseTakeType(elcCourseTakeVo.get(0).getCourseTakeType());
                course.setChooseObj(elcCourseTakeVo.get(0).getChooseObj());
                context.getSelectedCourses().add(course);
            }

        }
        return goodList;
    }

    /**选课*/
    private void doElec(ElecContextBk context,
        List<AbstractElecRuleExceutorBk> exceutors,
        List<ElecTeachClassDto> teachClassIds, ElectionRounds round)
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
            TeachingClassCache teachClass =
                dataProvider.getTeachClass(round.getId(),
                    data.getCourseCode(),
                    teachClassId);
            if (teachClass == null)
            {
                failedReasons.put(String.format("%s[%s]",
                    data.getCourseCode(),
                    data.getTeachClassCode()), "教学班不存在无法选课");
                continue;
            }
            boolean checkPublicEnglish = checkPublicEnglish(context, teachClass);
            if (!checkPublicEnglish){
                continue;
            }
            boolean allSuccess = true;
            for (AbstractElecRuleExceutorBk exceutor : exceutors)
            {
                if (!exceutor.checkRule(context, teachClass))
                {
                    // 校验不通过时跳过后面的校验进行下一个
                    allSuccess = false;
                    String key = teachClass.getTeachClassCode() + teachClass.getCourseName();
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
                // 判断是否有重修课
                if (!hasRetakeCourse && RetakeCourseUtil
                    .isRetakeCourseBk(context, teachClass.getCourseCode()))
                {
                    hasRetakeCourse = true;
                }
                StudentInfoCache studentInfo = context.getStudentInfo();
                ElecRequest request = context.getRequest();
                int index = TableIndexUtil.getIndex(request.getCalendarId());
                List<ElcCourseTakeVo> elcCourseTakeVo = courseTakeDao.findElcCourse(studentInfo.getStudentId(), round.getCalendarId(),index, teachClass.getCourseCode());
                if (CollectionUtil.isNotEmpty(elcCourseTakeVo)){
                    Integer courseTakeType = hasRetakeCourse==true?2:1;
                    // 更新缓存中教学班人数
                    teachClassCacheService.updateTeachingClassNumber(teachClassId);
                    SelectedCourse course = new SelectedCourse(teachClass);
                    course.setTurn(round.getTurn());
                    course.setCourseTakeType(courseTakeType);
                    course.setChooseObj(request.getChooseObj());
                    context.getSelectedCourses().add(course);
                    respose.getSuccessCourses().add(teachClassId);
                }else{
                    this.saveElc(context, teachClass, ElectRuleType.ELECTION,hasRetakeCourse);
                }
            }
        }
        // 判断学生是否要重修缴费
        String studentId = context.getStudentInfo().getStudentId();
        if (hasRetakeCourse && !chargeService.isNoNeedPayForRetake(studentId)&&chargeService.hasRetakeCourseNoPay(round.getCalendarId(),studentId))
        {
            context.getRespose().getData().put("retakePay", "true");
        }
    }
    
    /**退课*/
    private void doWithdraw(ElecContextBk context,
        List<AbstractWithdrwRuleExceutorBk> exceutors,
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
        ElecRequest request = context.getRequest();
        Long roundId = request.getRoundId();
        ElectionRounds round = dataProvider.getRound(roundId);
        for (ElecTeachClassDto data : teachClassIds)
        {
            Long teachClassId = data.getTeachClassId();
            SelectedCourse teachClass = null;
            Set<SelectedCourse> selectedCourses = context.getSelectedCourses();
            for (SelectedCourse selectCourse : selectedCourses)
            {
                if (selectCourse.getCourse()
                    .getTeachClassId()
                    .equals(teachClassId))
                {
                    teachClass = selectCourse;
                    break;
                }
            }
            if (teachClass == null)
            {

                StudentInfoCache studentInfo = context.getStudentInfo();
                //查询数据库，看这个教学班是否存在
                TeachingClass teachingClass = classDao.selectByPrimaryKey(teachClassId);
                if (teachingClass != null){
                    List<ElcCourseTakeVo> elcCourseTakeVo = courseTakeDao.findElcCourse(studentInfo.getStudentId(), round.getCalendarId(),TableIndexUtil.getIndex(round.getCalendarId()), data.getCourseCode());
                    if (CollectionUtil.isNotEmpty(elcCourseTakeVo)){
                        ElcCourseTakeVo c = elcCourseTakeVo.get(0);
                        teachClass = new SelectedCourse();
                        TeachingClassCache lesson = new TeachingClassCache();
                        lesson.setApply(c.getApply());
                        lesson.setCourseCode(c.getCourseCode());
                        lesson.setCourseName(c.getCourseName());
                        lesson.setCalendarId(c.getCalendarId());
                        lesson.setTeachClassId(c.getTeachingClassId());
                        lesson.setTeachClassCode(c.getTeachingClassCode());
                        lesson.setFaculty(c.getFaculty());
                        lesson.setTerm(c.getTerm());
                        teachClass.setCourse(lesson);
                        teachClass.setChooseObj(c.getChooseObj());
                        teachClass.setCourseTakeType(c.getCourseTakeType());
                        teachClass.setTurn(c.getTurn());
                    }else{
                        failedReasons.put(String.format("%s[%s]",
                                data.getCourseCode(),
                                data.getTeachClassCode()), "教学班不存在无法退课");
                        continue;
                    }

                }else{
                    failedReasons.put(String.format("%s[%s]",
                            data.getCourseCode(),
                            data.getTeachClassCode()), "教学班不存在无法退课");
                    continue;
                }
            }
            boolean allSuccess = true;
            for (AbstractWithdrwRuleExceutorBk exceutor : exceutors)
            {
                if (!exceutor.checkRule(context, teachClass))
                {
                    // 校验不通过时跳过后面的校验进行下一个
                    allSuccess = false;
                    String key =
                            teachClass.getCourse().getTeachClassCode() + teachClass.getCourse().getCourseName();
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
                StudentInfoCache studentInfo = context.getStudentInfo();
                int index = TableIndexUtil.getIndex(request.getCalendarId());
                List<ElcCourseTakeVo> elcCourseTakeVo = courseTakeDao.findElcCourse(studentInfo.getStudentId(), round.getCalendarId(),index, teachClass.getCourse().getCourseCode());
                if (CollectionUtil.isEmpty(elcCourseTakeVo)){
                    // 更新缓存中教学班人数
                    teachClassCacheService.updateTeachingClassNumber(teachClassId);
                }else{
                    this.saveElc(context,
                            teachClass.getCourse(),
                            ElectRuleType.WITHDRAW,false);
                }

                // 删除缓存中的数据
                Iterator<SelectedCourse> iterator = selectedCourses.iterator();
                while (iterator.hasNext())
                {
                    SelectedCourse c = iterator.next();
                    if (c.getCourse().getTeachClassId().equals(teachClassId))
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
    public void saveElc(ElecContextBk context, TeachingClassCache teachClass,
        ElectRuleType type,boolean hasRetakeCourse)
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
        Integer turn = round.getTurn();
        Integer logType = ElcLogVo.TYPE_1;
        
//        Integer courseTakeType =
//            Constants.REBUILD_CALSS.equals(teachClass.getTeachClassType())
//                ? CourseTakeType.RETAKE.type()
//                : CourseTakeType.NORMAL.type();
        Integer courseTakeType = hasRetakeCourse==true?2:1;
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
            if(ChooseObj.STU.type() != request.getChooseObj()){
                this.syncRemindTime(round.getCalendarId(),ElcLogVo.TYPE_1,studentId,stu.getStudentName(),courseName+"("+courseCode+")");

            }
        }
        else
        {
            List<ElcCourseTakeVo> elcCourseTakeVo = courseTakeDao.findElcCourse(studentId, round.getCalendarId(),TableIndexUtil.getIndex(round.getCalendarId()), teachClass.getCourseCode());
            turn = elcCourseTakeVo.get(0).getTurn();
            logType = ElcLogVo.TYPE_2;
            ElcCourseTake take = new ElcCourseTake();
            take.setCalendarId(round.getCalendarId());
            take.setCourseCode(courseCode);
            take.setStudentId(studentId);
            take.setTeachingClassId(teachClassId);
            courseTakeDao.delete(take);
            if(ChooseObj.STU.type() != request.getChooseObj()){
                this.syncRemindTime(round.getCalendarId(),ElcLogVo.TYPE_2,studentId,stu.getStudentName(),courseName+"("+courseCode+")");
            }
            int count = classDao.decrElcNumber(teachClassId);
            if (count > 0)
            {
                dataProvider.decrElcNumber(teachClassId);
            }
            if (round.getTurn() == Constants.THIRD_TURN
                || round.getTurn() == Constants.FOURTH_TURN)
            {
            	count= classDao.increDrawNumber(teachClassId);
            	 if (count > 0)
                 {
                     dataProvider.incrementDrawNumber(teachClassId);
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
        //更新选课申请数据
        electionApplyService
            .update(studentId, round.getCalendarId(), courseCode,type);
        String elecStatus = Constants.UN_ELEC;
        if (ElectRuleType.ELECTION.equals(type))
        {
        	elecStatus = Constants.IS_ELEC;
            // 更新缓存
            dataProvider.incrementElecNumber(teachClassId);
            respose.getSuccessCourses().add(teachClassId);
            Set<PlanCourse> planCourses = context.getPlanCourses();
            if (CollectionUtil.isNotEmpty(planCourses)) {
                for (PlanCourse planCours : planCourses) {
                    ElecCourse course = planCours.getCourse();
                    if (course != null) {
                        if (StringUtils.equalsIgnoreCase(courseCode, course.getCourseCode())) {
                            teachClass.setCompulsory(course.getCompulsory());
                            break;
                        }
                    }
                }
            }
            SelectedCourse course = new SelectedCourse(teachClass);
            course.setTurn(round.getTurn());
            course.setCourseTakeType(courseTakeType);
            course.setChooseObj(request.getChooseObj());
            context.getSelectedCourses().add(course);
        }
        //更新培养的选课状态
        StudentPlanCoure studentPlanCoure = new StudentPlanCoure();
        studentPlanCoure.setStudentId(studentId);
        studentPlanCoure.setCourseCode(courseCode);
        studentPlanCoure.setElecStatus(elecStatus);
        // 更新缓存中教学班人数
        teachClassCacheService.updateTeachingClassNumber1(teachClassId,type,turn);
        try {
        	CultureSerivceInvoker.updateElecStatus(studentPlanCoure);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }

    @Override
    public RestResult<?> syncRemindTime(Long calendarId,Integer num,String studentId,String studentName,String courseNameAndCode) {
        try {
            List<RemindTimeBean> errorList = new ArrayList<>();
            // 获取系统当前时间
            SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Long currentTime = System.currentTimeMillis();
            String time = dff.format(currentTime);
//            Long calendarId = BaseresServiceInvoker.getCurrentCalendar();/* 当前学期学年 */
            String calendarName = getCalendarName(calendarId);
            RemindTimeBean remindTimeBean = new RemindTimeBean();
            remindTimeBean.setCalendarId(calendarId);
            remindTimeBean.setRemindTime(time);
            remindTimeBean.setStudentId(studentId);
            remindTimeBean.setStudentName(studentName);
            String email = studentDao.findStuEmail(studentId);
            remindTimeBean.setStudentEmail(email);
            remindTimeBean.setCourseNameAndCode(courseNameAndCode);
            List<RemindTimeBean> alllist = new ArrayList<>();
            alllist.add(remindTimeBean);
            LOG.info("AssessSettingServiceImpl.syncRemindTime() start! 定时发送邮件，alllist：" + alllist.size() + ",time:" + df.format(currentTime));
            if (CollectionUtils.isNotEmpty(alllist)) {
                List<RemindTimeBean> list = alllist.stream().filter(bean -> null != (bean.getStudentEmail())).collect(Collectors.toList());
                EmailSend emailSend = new EmailSend();
                for (RemindTimeBean bean : list) {
                    List<String> emailList = new ArrayList<>();
                    if (StringUtils.isNotBlank(bean.getStudentEmail())) {
                        emailList.add(bean.getStudentEmail());
                    }
                    if (CollectionUtils.isNotEmpty(emailList)) {
                        try {
                            // send email
                            emailSend.sendStatisticsEmail(emailList, bean, calendarName, num, "");
                        } catch (Exception e) {
                            errorList.add(bean);
                            e.printStackTrace();
                            LOG.info("syncRemindTime() 邮件发送失败。错误信息：{}", e);
                        }
                    }
                }
            }


            LOG.info("AssessSettingServiceImpl.syncRemindTime() end! errorList.size():" + errorList);
            return RestResult.success("时间规则设置-提醒时间发送邮件成功");
        } catch (Exception e) {
            LOG.error("syncRemindTime():" + e);
            return RestResult.fail("时间规则设置-提醒时间发送邮件失败");
        }
    }
    /**
     * 获取学年学期
     *
     * @param calendarId
     * @return
     */
    private String getCalendarName(Long calendarId) {
        RestResult<SchoolCalendarVo> schoolCalendarVoResult = ServicePathEnum.BASESERVICE.getForObject("/schoolCalendar/{id}", RestResult.class, calendarId);
        SchoolCalendarVo calendarVo = schoolCalendarVoResult.getData();
        return calendarVo.getFullName();
    }

    //校验学生公共英语课
    private boolean checkPublicEnglish(ElecContextBk context, TeachingClassCache teachClass) {

        ElecRespose respose = context.getRespose();
        Map<String, String> failedReasons = respose.getFailedReasons();
        //已选课程
        Set<SelectedCourse> selectedCourses = context.getSelectedCourses();
        List<CompletedCourse> list = new ArrayList<>();
        //已完成课程
        Set<CompletedCourse> completedCourses = context.getCompletedCourses();
        Set<CompletedCourse> failedCourse = context.getFailedCourse();
        list.addAll(failedCourse);
        list.addAll(completedCourses);


        List<String> asList =
                courseDao.getAllCoursesLevelCourse();
        String courseCode = teachClass.getCourseCode();
        // 查询不到英语课-通过
        if (CollectionUtil.isEmpty(asList)) {
            return true;
        }

        if (!asList.contains(courseCode)) {
            return true;
        }
        //判断是否是重修课
        boolean isRetake = RetakeCourseUtil.isRetakeCourseBk(context, teachClass.getCourseCode());

        if (isRetake) {
            if (CollectionUtil.isEmpty(completedCourses)){
                return true;
            }
            Set<CompletedCourse> selectedcourse1 = new HashSet<>();
            for (CompletedCourse course:list){
                for (String string:asList){
                    if(StringUtils.equalsIgnoreCase(course.getCourse().getCourseCode(),string)){
                        selectedcourse1.add(course);
                    }
                }
            }

            if (selectedcourse1.size()<2) {
                return true;
            }else{
                failedReasons.put(String.format("%s[%s]",
                        teachClass.getCourseCode(),
                        teachClass.getTeachClassCode()), "最多能重修两门公共英语课");
                return false;
            }
        } else {
            if (CollectionUtil.isEmpty(selectedCourses)){
                return true;
            }
            //本学期已选公共外语课
            Set<SelectedCourse> selectedcourse1 = new HashSet<>();
            for (SelectedCourse course:selectedCourses){
                for (String string:asList){
                    if(StringUtils.equalsIgnoreCase(course.getCourse().getCourseCode(),string)){
                        selectedcourse1.add(course);
                    }
                }
            }
            if (CollectionUtil.isEmpty(selectedcourse1)) {
                return true;
            }
            //为了数据准确性，会对这个学期已经选取的课程进行查库操作
            ElecRequest request = context.getRequest();
            StudentInfoCache studentInfo = context.getStudentInfo();
            ElectionRounds round = dataProvider.getRound(request.getRoundId());
            Long calendarId = round.getCalendarId();
            List<ElcCourseTakeVo> courseTakes = courseTakeDao.findBkSelectedCourses(studentInfo.getStudentId(), calendarId, TableIndexUtil.getIndex(calendarId));
            List<String> collect = courseTakes.stream().map(ElcCourseTakeVo::getCourseCode).collect(Collectors.toList());
            Set<String> selectedcourse2 = new HashSet<>();
            for (String course:collect){
                for (String string:asList) {
                    if (StringUtils.equalsIgnoreCase(course, string)) {
                        selectedcourse2.add(course);
                    }
                }

            }
            if (CollectionUtil.isEmpty(selectedcourse2)) {
                return true;
            }
            failedReasons.put(String.format("%s[%s]",
                    teachClass.getTeachClassCode(),
                    teachClass.getCourseName()), "只能选一门公共英语课");
            return false;
        }

    }
}
