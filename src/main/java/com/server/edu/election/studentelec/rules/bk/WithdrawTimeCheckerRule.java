package com.server.edu.election.studentelec.rules.bk;

import org.springframework.stereotype.Component;

import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;

/**
 * 只能退本轮选的课
 * 
 * 
 */
@Component("WithdrawTimeCheckerRule")
public class WithdrawTimeCheckerRule extends AbstractRuleExceutor
{
    @Override
    public boolean checkRule(ElecContext context)
    {
        // TODO Auto-generated method stub
        return false;
    }
    
}
