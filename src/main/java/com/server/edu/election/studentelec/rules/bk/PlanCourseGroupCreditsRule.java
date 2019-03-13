package com.server.edu.election.studentelec.rules.bk;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.rpc.CultureSerivceInvoker;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.util.CollectionUtil;
import org.springframework.stereotype.Component;

import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;

import java.util.List;

/**
 * 培养计划课程组学分限制<br>
 * 被RetakeCourseBuildInPrepare这个ElectBuildInPrepare调用，因此不论用户页面有没有选这个规则，都会调用它的prepare
 */
@Component("PlanCourseGroupCreditsRule")
public class PlanCourseGroupCreditsRule extends AbstractRuleExceutor {

	// protected PlanCreditLimitPrepare planCreditLimitPrepare;

	@Override
	public boolean checkRule(ElecContext context, TeachingClassCache courseClass) {
		String courseCode = courseClass.getCourseCode();
		StudentInfoCache studentInfo = context.getStudentInfo();
		List<String> courseCodes =
				CultureSerivceInvoker.getCourseCodes(studentInfo.getStudentId());
		if(CollectionUtil.isNotEmpty(courseCodes)){
			if(courseCodes.contains(courseCode)){
				return true;
			}

			ElecRespose respose = context.getRespose();
			respose.getFailedReasons().put(courseClass.getTeachClassId().toString(),
					I18nUtil.getMsg("ruleCheck.planCultureLimit"));
			return false;
		}
		return false;
	}



}