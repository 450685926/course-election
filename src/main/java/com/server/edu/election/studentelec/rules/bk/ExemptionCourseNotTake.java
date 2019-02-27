package com.server.edu.election.studentelec.rules.bk;

import org.springframework.stereotype.Component;

import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecCourseClass;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;

/**
 * 免修申请期间不能选课
 * 
 */
@Component("ExemptionCourseNotTake")
public class ExemptionCourseNotTake extends AbstractRuleExceutor
{
    @Override
    public boolean checkRule(ElecContext context, ElecCourseClass courseClass)
    {
        // TODO Auto-generated method stub
        return false;
    }
    
}
