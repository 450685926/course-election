package com.server.edu.election.studentelec.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.server.edu.election.constants.ElectRuleType;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.SelectedCourse;
import com.server.edu.election.studentelec.dto.ElecTeachClassDto;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutor;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;
import com.server.edu.election.studentelec.rules.AbstractWithdrwRuleExceutor;
import com.server.edu.election.studentelec.service.AbstractElecQueueComsumerService;
import com.server.edu.election.studentelec.service.ElecQueueService;
import com.server.edu.election.studentelec.service.StudentElecRushCourseService;
import com.server.edu.election.studentelec.service.StudentElecService;
import com.server.edu.election.studentelec.utils.ElecContextUtil;
import com.server.edu.election.studentelec.utils.ElecStatus;
import com.server.edu.election.studentelec.utils.QueueGroups;
import com.server.edu.election.vo.ElectionRuleVo;
import com.server.edu.util.CollectionUtil;

@Service
public class StudentElecRushCourseServiceImpl
    extends AbstractElecQueueComsumerService<ElecRequest>
    implements StudentElecRushCourseService
{
    private static final Logger LOG =
        LoggerFactory.getLogger(StudentElecRushCourseServiceImpl.class);
    
    @Autowired
    private RoundDataProvider dataProvider;
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private StudentElecService elecService;
    
    @Autowired
    protected StudentElecRushCourseServiceImpl(
        ElecQueueService<ElecRequest> queueService)
    {
        super(QueueGroups.STUDENT_ELEC, queueService);
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public void consume(ElecRequest request)
    {
        LOG.info("");
        Long roundId = request.getRoundId();
        String studentId = request.getStudentId();
        ElecContext context = null;
        try
        {
            ElectionRounds round = dataProvider.getRound(roundId);
            context = new ElecContext(studentId, round.getCalendarId(), request);
            
            List<ElectionRuleVo> rules = dataProvider.getRules(roundId);
            
            // 获取执行规则
            Map<String, AbstractRuleExceutor> map =
                applicationContext.getBeansOfType(AbstractRuleExceutor.class);
            
            List<AbstractElecRuleExceutor> elecExceutors = new ArrayList<>();
            List<AbstractWithdrwRuleExceutor> cancelExceutors =
                new ArrayList<>();
            
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
            ElecRespose respose = context.getRespose();
            respose.getSuccessCourses().clear();
            respose.getFailedReasons().clear();
            
            // 退课
            doWithdraw(context,
                cancelExceutors,
                request.getWithdrawClassList());
            
            // 选课
            doElec(context, elecExceutors, request.getElecClassList(), round);
        }
        catch (Exception e)
        {
            LOG.error(e.getMessage(), e);
            if (context != null)
            {
                context.getRespose()
                    .getFailedReasons()
                    .put("error", e.getMessage());
            }
        }
        finally
        {
            // 不管选课有没有成功，结束时表示可以进行下一个选课请求
            ElecContextUtil.setElecStatus(roundId, studentId, ElecStatus.Ready);
            if (null != context)
            {
                // 数据保存到缓存
                context.saveToCache();
            }
        }
        
    }
    
    /**选课*/
    private void doElec(ElecContext context,
        List<AbstractElecRuleExceutor> exceutors,
        List<ElecTeachClassDto> teachClassIds, ElectionRounds round)
    {
        if (CollectionUtil.isEmpty(teachClassIds))
        {
            return;
        }
        Collections.sort(exceutors);
        LOG.info("---- exceutors:{} ----", teachClassIds.size());
        
        List<TeachingClassCache> successList = new ArrayList<>();
        
        Long roundId = context.getRequest().getRoundId();
        ElecRespose respose = context.getRespose();
        Map<String, String> failedReasons = respose.getFailedReasons();
        for (ElecTeachClassDto data : teachClassIds)
        {
            Long teachClassId = data.getTeachClassId();
            TeachingClassCache teachClass =
                dataProvider.getTeachClass(roundId, teachClassId);
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
            if (allSuccess)
            {
                successList.add(teachClass);
            }
        }
        
        // 对校验成功的课程进行入库保存
        for (TeachingClassCache courseClass : successList)
        {
            elecService.saveElc(context, courseClass, ElectRuleType.ELECTION);
            
            Long teachClassId = courseClass.getTeachClassId();
            respose.getSuccessCourses().add(teachClassId);
            
            SelectedCourse course = new SelectedCourse(courseClass);
            course.setTeachClassId(teachClassId);
            course.setTurn(round.getTurn());
            context.getSelectedCourses().add(course);
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
        LOG.info("---- widthdrawExceutors:{} ----", exceutors.size());
        
        ElecRespose respose = context.getRespose();
        Map<String, String> failedReasons = respose.getFailedReasons();
        List<TeachingClassCache> successList = new ArrayList<>();
        
        Set<SelectedCourse> selectedCourses = context.getSelectedCourses();
        for (ElecTeachClassDto data : teachClassIds)
        {
            Long teachClassId = data.getTeachClassId();
            SelectedCourse teachClass = null;
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
                    data.getTeachClassCode()), "教学班不存在无法选课");
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
            if (allSuccess)
            {
                successList.add(teachClass);
            }
        }
        
        // 对校验成功的课程进行入库保存
        for (TeachingClassCache courseClass : successList)
        {
            Long teachClassId = courseClass.getTeachClassId();
            elecService.saveElc(context, courseClass, ElectRuleType.WITHDRAW);
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
