package com.server.edu.election.studentelec.rules.bk;

import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;

/**
 * 检查学生是否缴费，未缴费的不能进入选课
 */
@Component("StdPayCostCheckerRule")
public class StdPayCostCheckerRule extends AbstractRuleExceutor {

	@Override
	public boolean checkRule(ElecContext context, TeachingClassCache courseClass) {
		StudentInfoCache studentInfo = context.getStudentInfo();
		if (studentInfo != null && courseClass.getTeacherClassId() != null) {
			if (studentInfo.isPaid()) {
				return true;
			} else {
				ElecRespose respose = context.getRespose();
				respose.getFailedReasons().put(courseClass.getTeacherClassId().toString(),
						I18nUtil.getMsg("ruleCheck.stdPayCostChecker"));
				return false;
			}
		}
		return true;
	}

}
