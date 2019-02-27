package com.server.edu.election.studentelec.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecCourseClass;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;
import com.server.edu.election.studentelec.service.AbstractElecQueueComsumerService;
import com.server.edu.election.studentelec.service.ElecQueueService;
import com.server.edu.election.studentelec.service.StudentElecRushCourseService;
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
    protected StudentElecRushCourseServiceImpl(
        ElecQueueService<ElecRequest> queueService)
    {
        super(4, QueueGroups.STUDENT_ELEC, queueService);
        super.listen("thread-rushcourses");
    }
    
    @Override
    public void consume(ElecRequest data)
    {
        LOG.info("");
        long roundId = data.getRoundId().longValue();
        ElecContext context = new ElecContext(data.getStudentId(), roundId);
        List<Long> elecTeachingClasses = data.getElecTeachingClasses();
        List<ElectionRuleVo> rules = dataProvider.getRules(roundId);
        
        // 获取执行规则
        Map<String, AbstractRuleExceutor> map =
            applicationContext.getBeansOfType(AbstractRuleExceutor.class);
        List<AbstractRuleExceutor> exceutors = new ArrayList<>();
        for (ElectionRuleVo ruleVo : rules)
        {
            exceutors.add(map.get(ruleVo.getServiceName()));
        }
        
        List<ElecCourseClass> successList = new ArrayList<>();
        for (Long teachClassId : elecTeachingClasses)
        {
            ElecCourseClass teachClass =
                dataProvider.getTeachClass(roundId, teachClassId);
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
                successList.add(teachClass);
            }
        }
        // TODO 对校验成功的课程进行入库保存
        // 需要再次判断人数是否已满
        
    }
}
