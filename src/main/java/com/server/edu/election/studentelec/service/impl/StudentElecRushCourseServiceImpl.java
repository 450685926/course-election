package com.server.edu.election.studentelec.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;
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
    
    @Override
    public void consume(ElecRequest request)
    {
        LOG.info("");
        Long roundId = request.getRoundId();
        String studentId = request.getStudentId();
        ElecContext context = null;
        try
        {
            context = new ElecContext(studentId, roundId);
            context.setRequest(request);
            
            ElectionRounds round = dataProvider.getRound(roundId);
            List<ElectionRuleVo> rules = dataProvider.getRules(roundId);
            
            // 获取执行规则
            Map<String, AbstractRuleExceutor> map =
                applicationContext.getBeansOfType(AbstractRuleExceutor.class);
            List<AbstractRuleExceutor> elecExceutors = new ArrayList<>();
            List<AbstractRuleExceutor> widthdrawExceutors = new ArrayList<>();
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
                        widthdrawExceutors.add(excetor);
                    }
                    else
                    {
                        elecExceutors.add(excetor);
                    }
                }
            }
            ElecRespose respose = context.getRespose();
            respose.getSuccessCourses().clear();
            respose.getFailedReasons().clear();
            
            // 退课
            if (CollectionUtil.isNotEmpty(request.getWithdrawClassList()))
            {
                Collections.sort(widthdrawExceutors);
                LOG.info("---- widthdrawExceutors:{} ----",
                    widthdrawExceutors.size());
                
                List<TeachingClassCache> withdrawList = doRuleCheck(context,
                    widthdrawExceutors,
                    request.getWithdrawClassList());
                
                // 对校验成功的课程进行入库保存
                for (TeachingClassCache courseClass : withdrawList)
                {
                    elecService
                        .saveElc(context, courseClass, ElectRuleType.WITHDRAW);
                    // 删除缓存中的数据
                    Iterator<SelectedCourse> iterator =
                        context.getSelectedCourses().iterator();
                    while (iterator.hasNext())
                    {
                        SelectedCourse c = iterator.next();
                        if (c.getTeachClassId()
                            .equals(courseClass.getTeachClassId()))
                        {
                            iterator.remove();
                            break;
                        }
                    }
                }
            }
            // 选课
            if (CollectionUtil.isNotEmpty(request.getElecClassList()))
            {
                Collections.sort(elecExceutors);
                LOG.info("---- exceutors:{} ----", elecExceutors.size());
                
                List<TeachingClassCache> successList = doRuleCheck(context,
                    elecExceutors,
                    request.getElecClassList());
                
                // 对校验成功的课程进行入库保存
                for (TeachingClassCache courseClass : successList)
                {
                    elecService
                        .saveElc(context, courseClass, ElectRuleType.ELECTION);
                    
                    SelectedCourse course = new SelectedCourse(courseClass);
                    course.setTeachClassId(courseClass.getTeachClassId());
                    course.setTurn(round.getTurn());
                    context.getSelectedCourses().add(course);
                }
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
    
    private List<TeachingClassCache> doRuleCheck(ElecContext context,
        List<AbstractRuleExceutor> exceutors,
        List<ElecTeachClassDto> teachClassIds)
    {
        Long roundId = context.getRoundId();
        ElecRespose respose = context.getRespose();
        Map<String, String> failedReasons = respose.getFailedReasons();
        List<TeachingClassCache> successList = new ArrayList<>();
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
            for (AbstractRuleExceutor exceutor : exceutors)
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
                respose.getSuccessCourses().add(teachClassId);
                successList.add(teachClass);
            }
        }
        return successList;
    }
}
