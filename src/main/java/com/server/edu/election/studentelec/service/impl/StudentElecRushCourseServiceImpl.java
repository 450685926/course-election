package com.server.edu.election.studentelec.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.server.edu.election.constants.ElectRuleType;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecCourseClass;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.SelectedCourse;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;
import com.server.edu.election.studentelec.service.AbstractElecQueueComsumerService;
import com.server.edu.election.studentelec.service.ElecQueueService;
import com.server.edu.election.studentelec.service.StudentElecRushCourseService;
import com.server.edu.election.studentelec.service.StudentElecService;
import com.server.edu.election.studentelec.utils.ElecContextUtil;
import com.server.edu.election.studentelec.utils.ElecStatus;
import com.server.edu.election.studentelec.utils.QueueGroups;
import com.server.edu.election.vo.ElectionRuleVo;

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
        // 使用4个线程来处理选课申请
        super(8, QueueGroups.STUDENT_ELEC, queueService);
    }
    
    @Override
    public void consume(ElecRequest request)
    {
        LOG.info("");
        Long roundId = request.getRoundId();
        String studentId = request.getStudentId();
        try
        {
            ElecContext context = new ElecContext(studentId, roundId);
            context.setRequest(request);
            
            List<Long> elecTeachingClasses = request.getElecTeachingClasses();
            ElectionRounds round = dataProvider.getRound(roundId);
            List<ElectionRuleVo> rules = dataProvider.getRules(roundId);
            
            // 获取执行规则
            Map<String, AbstractRuleExceutor> map =
                applicationContext.getBeansOfType(AbstractRuleExceutor.class);
            List<AbstractRuleExceutor> exceutors = new ArrayList<>();
            for (ElectionRuleVo ruleVo : rules)
            {
                AbstractRuleExceutor excetor = map.get(ruleVo.getServiceName());
                if (null != excetor)
                {
                    excetor.setProjectId(ruleVo.getManagerDeptId());
                    excetor.setType(ElectRuleType.valueOf(ruleVo.getType()));
                    exceutors.add(excetor);
                }
            }
            Collections.sort(exceutors);
            
            ElecRespose respose = context.getRespose();
            List<ElecCourseClass> successList = new ArrayList<>();
            for (Long teachClassId : elecTeachingClasses)
            {
                ElecCourseClass teachClass =
                    dataProvider.getTeachClass(roundId, teachClassId);
                if (teachClass == null)
                {
                    continue;
                }
                boolean allSuccess = true;
                for (AbstractRuleExceutor exceutor : exceutors)
                {
                    if (!exceutor.checkRule(context, teachClass))
                    {
                        // 校验不通过时跳过后面的校验进行下一个
                        allSuccess = false;
                        break;
                    }
                }
                if (allSuccess)
                {
                    respose.getSuccessCourses().add(teachClassId);
                    successList.add(teachClass);
                }
                else
                {
                    respose.getFailedCourses().add(teachClassId);
                }
            }
            
            // 对校验成功的课程进行入库保存
            for (ElecCourseClass courseClass : successList)
            {
                elecService.saveElc(context, courseClass);
                
                SelectedCourse course = new SelectedCourse(courseClass);
//                course.setPublicElec();
                course.setSelectedRound(round.getTurn());
//                course.setTime(time);
//                course.setWeeks(weeks);
                context.getSelectedCourses().add(course);
            }
            // 数据保存到缓存
            context.saveToCache();
        }
        finally
        {
            // 不管选课有没有成功，结束时表示可以进行下一个选课请求
            ElecContextUtil.setElecStatus(roundId, studentId, ElecStatus.Ready);
        }
        
    }
}
