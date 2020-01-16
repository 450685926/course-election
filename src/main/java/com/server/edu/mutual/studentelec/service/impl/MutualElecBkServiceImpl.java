package com.server.edu.mutual.studentelec.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.ChooseObj;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.constants.ElectRuleType;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.dao.ElcLogDao;
import com.server.edu.election.dao.TeachingClassDao;
import com.server.edu.election.entity.ElcCourseTake;
import com.server.edu.election.entity.ElcLog;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.service.RebuildCourseChargeService;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.IElecContext;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.context.bk.SelectedCourse;
import com.server.edu.election.studentelec.dto.ElecTeachClassDto;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;
import com.server.edu.election.studentelec.service.cache.TeachClassCacheService;
import com.server.edu.election.studentelec.service.impl.RoundDataProvider;
import com.server.edu.election.studentelec.utils.RetakeCourseUtil;
import com.server.edu.election.vo.ElcLogVo;
import com.server.edu.election.vo.ElectionRuleVo;
import com.server.edu.mutual.studentelec.context.ElecContextMutualBk;
import com.server.edu.mutual.studentelec.rule.AbstractMutualElecRuleExceutor;
import com.server.edu.mutual.studentelec.rule.AbstractMutualWithdrwRuleExceutor;
import com.server.edu.mutual.studentelec.rule.LimitCountCheckerRule;
import com.server.edu.mutual.studentelec.service.MutualElecBkService;
import com.server.edu.util.BeanUtil;
import com.server.edu.util.CollectionUtil;


/**
 * 本科生选课
 * @author xlluoc
 */
@Service
public class MutualElecBkServiceImpl implements MutualElecBkService{
	Logger LOG = LoggerFactory.getLogger(MutualElecBkServiceImpl.class);
	
    @Autowired
    private RoundDataProvider dataProvider;
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private RebuildCourseChargeService chargeService;
    
    @Autowired
    private TeachingClassDao classDao;
    
    @Autowired
    private ElcCourseTakeDao courseTakeDao;
    
    @Autowired
    private ElcLogDao elcLogDao;
    
    @Autowired
    private TeachClassCacheService teachClassCacheService;
	
	@SuppressWarnings("rawtypes")
	@Override
	public IElecContext doELec(ElecRequest request) {
		LOG.info("-----------------------111444------------doELec start -------------------");
		 
        Long roundId = request.getRoundId();
        String studentId = request.getStudentId();
        Long calendarId = request.getCalendarId();

        LOG.info("-----------------------111555------------roundId:" + roundId + "-------------------");
        LOG.info("-----------------------111666------------studentId:" + studentId + "-------------------");
        LOG.info("-----------------------111777------------calendarId:" + calendarId + "-------------------");
        
        Assert.notNull(calendarId, "calendarId must be not null");
        
        ElecContextMutualBk context =
            new ElecContextMutualBk(studentId, calendarId, request);
        
        List<ElectionRuleVo> rules = dataProvider.getRules(roundId);
        
        LOG.info("-----------------------111888------------rules:" + rules.size() + "-------------------");
        
        List<AbstractMutualElecRuleExceutor> elecExceutors = new ArrayList<>();
        List<AbstractMutualWithdrwRuleExceutor> cancelExceutors = new ArrayList<>();
        // 获取执行规则
        Map<String, AbstractRuleExceutor> map =
            applicationContext.getBeansOfType(AbstractRuleExceutor.class);
        
        Set<Entry<String,AbstractRuleExceutor>> set = map.entrySet();
        for (Entry<String, AbstractRuleExceutor> entry : set) {
			LOG.info("============entry" + entry.getKey() + "====================");
		}
        
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
                    cancelExceutors.add((AbstractMutualWithdrwRuleExceutor)excetor);
                }
                else
                {
                    elecExceutors.add((AbstractMutualElecRuleExceutor)excetor);
                }
            }
        }
        LOG.info("--------------111999-------cancelExceutors:"+ cancelExceutors.size() +"-------------------------------");
        LOG.info("--------------202020-------elecExceutors:"+ elecExceutors.size() +"-------------------------------");
        
        ElecRespose respose = context.getRespose();
        respose.getSuccessCourses().clear();
        respose.getFailedReasons().clear();
        
        // 退课
        doWithdraw(context, cancelExceutors, request.getWithdrawClassList());
        
        // 选课
        ElectionRounds round = dataProvider.getRound(roundId);
        doElec(context, elecExceutors, request.getElecClassList(), round);
        
        List<SelectedCourse> list = context.getSelectedMutualCourses();
        for (SelectedCourse selectedCourse : list) {
        	TeachingClassCache classCache = selectedCourse.getCourse();
        	LOG.info("--------------%%%%%%%%%%%%%%%-------classCache:"+ classCache.getCourseCode() + "===" + classCache.getCourseName() +"------");
		}
        
        return context;
	}
	
    /**选课*/
    private void doElec(ElecContextMutualBk context,
        List<AbstractMutualElecRuleExceutor> exceutors,
        List<ElecTeachClassDto> teachClassIds, ElectionRounds round)
    {
        if (CollectionUtil.isEmpty(teachClassIds))
        {
        	LOG.info("-------232323 teachClassIds :{} ----", teachClassIds.size());
            return;
        }
        Collections.sort(exceutors);
        LOG.info("---mutual election exceutors :{} ----", exceutors.size());
        
        ElecRespose respose = context.getRespose();
        Map<String, String> failedReasons = respose.getFailedReasons();
        boolean hasRetakeCourse = false;
        for (ElecTeachClassDto data : teachClassIds)
        {
            Long teachClassId = data.getTeachClassId();
            
            LOG.info("--------------242424 teachClassId :{} --------------", teachClassId);
            
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
            LOG.info("--------------252525 teachClass :"+ teachClass.toString() +" --------------");

            boolean allSuccess = true;
            for (AbstractMutualElecRuleExceutor exceutor : exceutors)
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
            	ElecContextBk contextBk = new ElecContextBk();
            	LOG.info("--------------262626 allSuccess :"+ allSuccess +" --------------");
            	LOG.info("--------------272727 contextBk :"+ contextBk.toString() +" --------------");
            	LOG.info("--------------282828 context :"+ context.toString() +" --------------");
            	try {
					BeanUtil.copyProperties(contextBk, context);
				} catch (Exception e) {
					LOG.error(e.getMessage());
				}
            	
            	LOG.info("--------------292929 contextBk :"+ contextBk.toString() +" --------------");
            	
            	
                // 判断是否有重修课
                if (!hasRetakeCourse && RetakeCourseUtil
                    .isRetakeCourseBk(contextBk, teachClass.getCourseCode()))
                {
                    hasRetakeCourse = true;
                }
                this.saveElc(context, teachClass, ElectRuleType.ELECTION,hasRetakeCourse);
            }
        }
        // 判断学生是否要重修缴费
        String studentId = context.getStudentInfo().getStudentId();
        if (hasRetakeCourse && !chargeService.isNoNeedPayForRetake(studentId))
        {
            context.getRespose().getData().put("retakePay", "true");
        }
    }
    
    @Transactional
    @Override
    public void saveElc(ElecContextMutualBk context, TeachingClassCache teachClass,
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
        
        LOG.info("--------------303030 teachClass :"+ teachClass.toString() +" --------------");
        
        Integer logType = ElcLogVo.TYPE_1;
        
//        Integer courseTakeType =
//            Constants.REBUILD_CALSS.equals(teachClass.getTeachClassType())
//                ? CourseTakeType.RETAKE.type()
//                : CourseTakeType.NORMAL.type();
        
        Integer courseTakeType = hasRetakeCourse==true?2:1;
        if (ElectRuleType.ELECTION.equals(type))
        {
        	LOG.info("--------------313131 teachClass :"+ teachClass.toString() +" --------------");
        	
            if (dataProvider.containsRule(roundId,
                LimitCountCheckerRule.class.getSimpleName()))
            {
                LOG.info("---- LimitCountCheckerRule ----");
                LOG.info("--------------323232 teachClass :"+ teachClass.toString() +" --------------");
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
            	LOG.info("--------------343434 teachClass :"+ teachClass.toString() +" --------------");
                classDao.increElcNumber(teachClassId);
            }
            
            LOG.info("--------------353535 take take take START --------------");
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
            LOG.info("--------------363636 take take take STOP --------------");
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
        LOG.info("--------------373737 log log log START --------------");
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
        LOG.info("--------------383838 log log log STOP --------------");
        
        if (ElectRuleType.ELECTION.equals(type))
        {
            //更新选课申请数据
            //electionApplyService
            //    .update(studentId, round.getCalendarId(), courseCode);
            
        	// 更新缓存
        	LOG.info("--------------393939  --------------");
            dataProvider.incrementElecNumber(teachClassId);
            respose.getSuccessCourses().add(teachClassId);
            SelectedCourse course = new SelectedCourse(teachClass);
            course.setTurn(round.getTurn());
            course.setCourseTakeType(courseTakeType);
            course.setChooseObj(request.getChooseObj());
            
            // 更新本研互选和学生选课数据
            context.getSelectedMutualCourses().add(course);
            context.getSelectedCourses().add(course);
            
            LOG.info("--------------404040 selected size: "+ context.getSelectedCourses().size() +" --------------");
        }
        // 更新缓存中教学班人数
        teachClassCacheService.updateTeachingClassNumber(teachClassId);
    }

    /**退课*/
    private void doWithdraw(ElecContextMutualBk context,
        List<AbstractMutualWithdrwRuleExceutor> exceutors,
        List<ElecTeachClassDto> teachClassIds)
    {
        if (CollectionUtil.isEmpty(teachClassIds))
        {
        	LOG.info("--------------202020-------teachClassIds:"+ teachClassIds.size() +"-------------------------------");
            return;
        }
        
        Collections.sort(exceutors);
        LOG.info("----mutual election widthdrawExceutors :{} ----", exceutors.size());
        
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
            for (AbstractMutualWithdrwRuleExceutor exceutor : exceutors)
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
    
    
}
