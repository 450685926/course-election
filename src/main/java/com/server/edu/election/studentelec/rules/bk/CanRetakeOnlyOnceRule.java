package com.server.edu.election.studentelec.rules.bk;

import org.springframework.stereotype.Component;

import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;

/**
 * 通过课程只能重修一次
 * 
 */
@Component("CanRetakeOnlyOnceRule")
public class CanRetakeOnlyOnceRule extends AbstractRuleExceutor
{
    @Override
    public boolean checkRule(ElecContext context)
    {
        // TODO Auto-generated method stub
        return false;
    }
    
}
