package com.server.edu.election.studentelec.rules.bk;

import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;
import com.server.edu.election.studentelec.rules.RulePriority;

/**
 * 只允许选重修课
 * 
 */
@Component("OnlyRetakeFilter")
public class OnlyRetakeFilter extends AbstractRuleExceutor {
	@Override
	public int getOrder() {
		return RulePriority.FIRST.ordinal();
	}

	@Override
	public boolean checkRule(ElecContext context, TeachingClassCache courseClass) {
		if (courseClass.getTeachClassId() != null) {
			if (Constants.REBUILD_CALSS.equals(courseClass.getTeachClassType())) {
				return true;
			} else {
				ElecRespose respose = context.getRespose();
				respose.getFailedReasons().put(courseClass.getTeachClassId().toString(),
						I18nUtil.getMsg("ruleCheck.onlyRetakeFilter"));
			}
		}
		return false;
	}

}