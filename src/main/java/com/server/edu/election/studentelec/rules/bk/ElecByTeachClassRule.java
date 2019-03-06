package com.server.edu.election.studentelec.rules.bk;

import org.springframework.stereotype.Component;

import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;
import com.server.edu.election.studentelec.rules.RulePriority;

/**
 * 按教学班选课
 */
@Component("ElecByTeachClassRule")
public class ElecByTeachClassRule extends AbstractRuleExceutor {

	// 是否按教学班选课
	private static final String IF_ELECT_BY_TEACHCLASS = "TEACHCLASS";

	// 学生专业限制项
	private static final String RULE_PARAM_MAJOR = "MAJOR";

	// 学生行政班限制项
	private static final String RULE_PARAM_ADMINCLASS = "ADMINCLASS";

	// 学生性别限制项
	private static final String RULE_PARAM_GENDER = "GENDER";

	// 学生年级限制项
	private static final String RULE_PARAM_GRADE = "GRADE";

	// 学生类别限制项
	private static final String RULE_PARAM_STDTYPE = "STDTYPE";

	// 学生学生方向限制项
	private static final String RULE_PARAM_DIRECTION = "DIRECTION";

	// 学历层次
	private static final String RULE_PARAM_EDUCATION = "EDUCATION";

	// 培养计划限制项（对教务管理上没有“行政班”这一概念的学校有用）
	private static final String RULE_PARAM_PROGRAM = "PROGRAM";

	// 学生专业所在院系限制项
	private static final String RULE_PARAM_DEPARTMENT = "DEPARTMENT";

	// 是否新生选课
//	private static final String RULE_PARAM_IFNEW = "IFNEW";

	@Override
	public int getOrder() {
		return RulePriority.FIFTH.ordinal();
	}

	public ElecByTeachClassRule() {
		super();
	}

	@Override
	public boolean checkRule(ElecContext context, TeachingClassCache courseClass) {

		return false;
	}

}