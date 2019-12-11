package com.server.edu.election.studentelec.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.server.edu.common.ServicePathEnum;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.election.dao.*;
import com.server.edu.election.entity.*;
import com.server.edu.election.rpc.BaseresServiceInvoker;
import com.server.edu.election.util.EmailSend;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import com.server.edu.election.entity.RebuildCourseRecycle;
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
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.IElecContext;
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
        
        // 退课
        doWithdraw(context, cancelExceutors, request.getWithdrawClassList());
        
        // 选课
        ElectionRounds round = dataProvider.getRound(roundId);
        doElec(context, elecExceutors, request.getElecClassList(), round);
        
        return context;
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
            boolean allSuccess = true;
            for (AbstractElecRuleExceutorBk exceutor : exceutors)
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
                // 判断是否有重修课
                if (!hasRetakeCourse && RetakeCourseUtil
                    .isRetakeCourseBk(context, teachClass.getCourseCode()))
                {
                    hasRetakeCourse = true;
                }
                this.saveElc(context, teachClass, ElectRuleType.ELECTION,hasRetakeCourse);
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
                failedReasons.put(String.format("%s[%s]",
                    data.getCourseCode(),
                    data.getTeachClassCode()), "教学班不存在无法退课");
                continue;
            }
            boolean allSuccess = true;
            for (AbstractWithdrwRuleExceutorBk exceutor : exceutors)
            {
                if (!exceutor.checkRule(context, teachClass))
                {
                    // 校验不通过时跳过后面的校验进行下一个
                    allSuccess = false;
                    String key =
                        teachClass.getCourse().getCourseCodeAndClassCode();
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
                this.saveElc(context,
                    teachClass.getCourse(),
                    ElectRuleType.WITHDRAW,false);
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
        if(ChooseObj.STU.type() != 1){
            if (ElectRuleType.ELECTION.equals(type)){
                this.syncRemindTime(ElcLogVo.TYPE_1,studentId,courseCode+"("+courseName+")");

            }else{
                this.syncRemindTime(ElcLogVo.TYPE_2,studentId,courseCode+"("+courseName+")");

            }
        }
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
        try {
        	CultureSerivceInvoker.updateElecStatus(studentPlanCoure);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // 更新缓存中教学班人数
        teachClassCacheService.updateTeachingClassNumber(teachClassId);
    }


    public RestResult<?> syncRemindTime(Integer num,String studentId,String courseNameAndCode) {
        try {
            List<RemindTimeBean> errorList = new ArrayList<>();
            // 获取系统当前时间
            SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd HH");
            Long currentTime = System.currentTimeMillis();
            String time = dff.format(currentTime);
            Long calendarId = BaseresServiceInvoker.getCurrentCalendar();/* 当前学期学年 */
            String calendarName = getCalendarName(calendarId);
            RemindTimeBean remindTimeBean = new RemindTimeBean();
            remindTimeBean.setCalendarId(calendarId);
            remindTimeBean.setRemindTime(time);
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
}
