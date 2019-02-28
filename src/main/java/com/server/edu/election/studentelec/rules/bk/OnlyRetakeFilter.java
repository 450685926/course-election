package com.server.edu.election.studentelec.rules.bk;

import org.springframework.stereotype.Component;

import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecCourseClass;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;
import com.server.edu.election.studentelec.rules.RulePriority;

/**
 * 只允许选重修课
 * 
 */
@Component("OnlyRetakeFilter")
public class OnlyRetakeFilter extends AbstractRuleExceutor
{
    @Override
    public int getOrder()
    {
        return RulePriority.FIRST.ordinal();
    }
    
    @Override
    public boolean checkRule(ElecContext context, ElecCourseClass courseClass)
    {
    	if(courseClass.getTeacherClassId()!=null) {
    		
    	}
        return false;
    }
    
}