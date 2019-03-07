package com.server.edu.election.studentelec.rules.bk;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.studentelec.context.CompletedCourse;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;
import com.server.edu.election.studentelec.rules.RulePriority;
import com.server.edu.util.CollectionUtil;

/**
 * 得优课程不能重修
 * 
 */
@Component("UnElectBecauseARule")
public class UnElectBecauseARule extends AbstractRuleExceutor {
	@Override
	public int getOrder() {
		return RulePriority.FIFTH.ordinal();
	}

	// @Override
	@Override
	public boolean checkRule(ElecContext context, TeachingClassCache courseClass) {
		Set<CompletedCourse> completedCourses = context.getCompletedCourses();
		if (CollectionUtil.isNotEmpty(completedCourses) && courseClass.getTeachClassId() != null) {
			List<CompletedCourse> list = completedCourses.stream().filter(temp -> temp.isExcellent())
					.collect(Collectors.toList());
			if (CollectionUtil.isNotEmpty(list)) {
				if (StringUtils.isNotBlank(courseClass.getCourseCode())) {
					List<CompletedCourse> courses = list.stream()
							.filter(c -> courseClass.getCourseCode().equals(c.getCourseCode()))
							.collect(Collectors.toList());
					if (CollectionUtil.isNotEmpty(courses)) {
						return true;
					} else {
						ElecRespose respose = context.getRespose();
						respose.getFailedReasons().put(courseClass.getTeachClassId().toString(),
								I18nUtil.getMsg("ruleCheck.unElectBecauseA"));
					}
				}

			}
		}
		return false;
	}

}