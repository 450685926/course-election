package com.server.edu.mutual.studentelec.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.server.edu.common.entity.CulturePlan;
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
import com.server.edu.election.rpc.CultureSerivceInvoker;
import com.server.edu.election.service.RebuildCourseChargeService;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.IElecContext;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.dto.ElecTeachClassDto;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;
import com.server.edu.election.studentelec.service.cache.TeachClassCacheService;
import com.server.edu.election.studentelec.service.impl.RoundDataProvider;
import com.server.edu.election.studentelec.utils.RetakeCourseUtil;
import com.server.edu.election.vo.ElcLogVo;
import com.server.edu.election.vo.ElectionRuleVo;
import com.server.edu.mutual.studentelec.context.ElecContextMutual;
import com.server.edu.mutual.studentelec.rule.AbstractMutualElecRuleExceutor;
import com.server.edu.mutual.studentelec.rule.AbstractMutualWithdrwRuleExceutor;
import com.server.edu.mutual.studentelec.rule.LimitCountCheckerRule;
import com.server.edu.mutual.studentelec.service.MutualElecService;
import com.server.edu.mutual.vo.SelectedCourse;
import com.server.edu.util.BeanUtil;
import com.server.edu.util.CollectionUtil;


/**
 * 本科生选课
 * @author xlluoc
 */
@Service
public class MutualElecServiceImpl implements MutualElecService{
	Logger LOG = LoggerFactory.getLogger(MutualElecServiceImpl.class);
	
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
        Long roundId = request.getRoundId();
        String studentId = request.getStudentId();
        Long calendarId = request.getCalendarId();
        
        Assert.notNull(calendarId, "calendarId must be not null");
        
        ElecContextMutual context =
            new ElecContextMutual(studentId, calendarId, request);
        
        List<ElectionRuleVo> rules = dataProvider.getRules(roundId);
        
        List<AbstractMutualElecRuleExceutor> elecExceutors = new ArrayList<>();
        List<AbstractMutualWithdrwRuleExceutor> cancelExceutors = new ArrayList<>();
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
                    cancelExceutors.add((AbstractMutualWithdrwRuleExceutor)excetor);
                }
                else
                {
                    elecExceutors.add((AbstractMutualElecRuleExceutor)excetor);
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
    private void doElec(ElecContextMutual context,
        List<AbstractMutualElecRuleExceutor> exceutors,
        List<ElecTeachClassDto> teachClassIds, ElectionRounds round)
    {
        if (CollectionUtil.isEmpty(teachClassIds))
        {
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
            for (AbstractMutualElecRuleExceutor exceutor : exceutors)
            {
                if (!exceutor.checkRule(context, teachClass))
                {
                    // 校验不通过时跳过后面的校验进行下一个
                    allSuccess = false;
                    String key = teachClass.getCourseCode() + "[" + teachClass.getCourseName() + "]";
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
            	try {
            		if (!hasRetakeCourse) {
            			if (StringUtils.equals(round.getProjectId(), Constants.PROJ_UNGRADUATE)) {
            				ElecContext contextYjs = new ElecContext();
            				BeanUtil.copyProperties(contextYjs, context);
            				hasRetakeCourse = RetakeCourseUtil.isRetakeCourse(contextYjs, teachClass.getCourseCode());
            			}else {
            				ElecContextBk contextBk = new ElecContextBk();
            				BeanUtil.copyProperties(contextBk, context);
            				hasRetakeCourse = RetakeCourseUtil.isRetakeCourseBk(contextBk, teachClass.getCourseCode());
						}
            		}
				} catch (Exception e) {
					LOG.error(e.getMessage());
				}
            	
                // 学生选课
                this.saveElc(context, teachClass, ElectRuleType.ELECTION,hasRetakeCourse);
            }
        }
        // 判断学生是否要重修缴费
        String studentId = context.getStudentInfo().getStudentId();
        Long roundId = context.getRequest().getRoundId();
        ElectionRounds elcRound = dataProvider.getRound(roundId);
        if (hasRetakeCourse && !chargeService.isNoNeedPayForRetake(studentId,elcRound.getCalendarId()))
        {
            context.getRespose().getData().put("retakePay", "true");
        }
    }
    
    @Transactional
    @Override
    public void saveElc(ElecContextMutual context, TeachingClassCache teachClass,
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
            
            LOG.info("-------------- elec START --------------");
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
        LOG.info("-------------- elec log START --------------");
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
            //更新选课申请数据
            //electionApplyService
            //    .update(studentId, round.getCalendarId(), courseCode);
            
        	// 更新缓存
            dataProvider.incrementElecNumber(teachClassId);
            respose.getSuccessCourses().add(teachClassId);
            SelectedCourse course = new SelectedCourse(teachClass);
            course.setTurn(round.getTurn());
            course.setCourseTakeType(courseTakeType);
            course.setChooseObj(request.getChooseObj());
            course.setCourseCode(teachClass.getCourseCode());
            course.setCourseName(teachClass.getCourseName());
            course.setCredits(teachClass.getCredits());
            course.setStatus(Constants.ELECTED);
             
            // 更新本研互选和学生选课数据
            context.getSelectedMutualCourses().add(course);
            context.getSelectedCourses().add(course);
            
            // 深复制
            Set<SelectedCourse> unSelectedMutualCourses = context.getUnSelectedMutualCourses();
            for (SelectedCourse unsSelectedCourse : unSelectedMutualCourses) {
				if (StringUtils.equals(unsSelectedCourse.getCourseCode(), course.getCourseCode())) {
					unSelectedMutualCourses.remove(unsSelectedCourse);
					break;
				}
			}
        }
        // 更新缓存中教学班人数
        teachClassCacheService.updateTeachingClassNumber(teachClassId);
        
        /*************************选课/退课后修改学生培养计划中课程选课状态************************/
        try {
			updateSelectCourse(studentId,courseCode,type);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    /**退课*/
    private void doWithdraw(ElecContextMutual context,
        List<AbstractMutualWithdrwRuleExceutor> exceutors,
        List<ElecTeachClassDto> teachClassIds)
    {
        if (CollectionUtil.isEmpty(teachClassIds))
        {
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
            Set<SelectedCourse> selectedMutualCourses = context.getSelectedMutualCourses();
            for (SelectedCourse selectCourse : selectedMutualCourses)
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
                    TeachingClassCache course = teachClass.getCourse();
                    String key = course.getCourseCode() + "[" + course.getCourseName() + "]";
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
                // 删除缓存中的互选数据
                Set<SelectedCourse> unSelectedMutualCourses = context.getUnSelectedMutualCourses();
                Iterator<SelectedCourse> iterator = selectedMutualCourses.iterator();
                while (iterator.hasNext())
                {
                	SelectedCourse c = new SelectedCourse();
                	try {
						BeanUtil.copyProperties(c, iterator.next());
						if (c.getCourse().getTeachClassId().equals(teachClassId))
						{
							c.setCourse(null);
							c.setStatus(Constants.UN_ELECTED);
							c.setTurn(null);
							unSelectedMutualCourses.add(c);
							
							iterator.remove();
							break;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
                }
                
                // 删除缓存中的选课数据
                iterator = selectedCourses.iterator();
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
    
    /**
     * 选课后跟新学生培养计划课程的选课状态
     * @param studentId   学号
     * @param courseCode  课程编号
     * @param type        类型
     * @throws Exception 
     */
    public void updateSelectCourse(String studentId,String courseCode,ElectRuleType type) throws Exception {
    	// 根据学号查询学生培养计划中的全部课程号
        List<String> courseCodes = CultureSerivceInvoker.getCourseCodes(studentId);
        
        // 如果选课的课程是该学生培养计划中的课程，则修改培养计划课程选课状态（1-已选课;0-未选课）
        if (CollectionUtil.isNotEmpty(courseCodes) && courseCodes.contains(courseCode)) {
        	LOG.info("-------------------update culture start-----------------");
        	CulturePlan culturePlan = new CulturePlan();
        	culturePlan.setPageSize_(10);
        	culturePlan.setPageNum_(0);
        	culturePlan.setStudentId(studentId);
        	culturePlan.setCourseCode(courseCode);
        	if (ElectRuleType.ELECTION.equals(type)) {
        		culturePlan.setSelCourse("1"); // 1-已选课
			}else {
				culturePlan.setSelCourse("0"); // 0-未选课
			}
        	CultureSerivceInvoker.updateSelectCourse(culturePlan);
		}
    }
}
