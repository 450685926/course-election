package com.server.edu.election.studentelec.rules.bk;

import org.springframework.stereotype.Component;

import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;

/**
 * 不能退教务员选的课
 * 
 */
@Component("AssignedWithdrawRule")
public class AssignedWithdrawRule extends AbstractRuleExceutor {
	@Override
	public boolean checkRule(ElecContext context, TeachingClassCache courseClass) {
		// TODO Auto-generated method stub
		return false;
	}

}
