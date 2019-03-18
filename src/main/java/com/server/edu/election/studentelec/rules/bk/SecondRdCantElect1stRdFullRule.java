package com.server.edu.election.studentelec.rules.bk;

import org.springframework.stereotype.Component;

import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutor;

/**
 * 第二轮选课时，不能选第一轮选课已经选满的课程<br>
 * 具体描述： A任务人数上限90人，第一轮选满了90人，那么第二轮选课时<br>
 * 学生可能能够看到A任务（依据HideWhenFullRule是否勾上来确定），但是不能选A任务。
 * 如果在第二轮选课时，第一轮选上的某人退了A任务，那么学生就能选该任务了。
 */
@Component("SecondRdCantElect1stRdFullRule")
public class SecondRdCantElect1stRdFullRule extends AbstractElecRuleExceutor
{
    
    @Override
    public int getOrder()
    {
        return Integer.MAX_VALUE;
    }
    
    @Override
    public boolean checkRule(ElecContext context,
        TeachingClassCache courseClass)
    {
        
        return true;
    }
    
}
