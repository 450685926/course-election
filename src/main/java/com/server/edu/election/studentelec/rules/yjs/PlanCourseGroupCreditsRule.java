package com.server.edu.election.studentelec.rules.yjs;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.CourseTakeType;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.CourseGroup;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.PlanCourse;
import com.server.edu.election.studentelec.context.SelectedCourse;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutor;
import com.server.edu.util.CollectionUtil;

/**
 * 培养计划课程组学分限制<br>
 * 被RetakeCourseBuildInPrepare这个ElectBuildInPrepare调用，因此不论用户页面有没有选这个规则，都会调用它的prepare
 * planCourseGroupCreditsChecker
 */
@Component("yjsPlanCourseGroupCreditsRule")
public class PlanCourseGroupCreditsRule extends AbstractElecRuleExceutor
{
	public static final Boolean IS_PLAN_COURSE = true;
	
	public static final Boolean IS_NOT_PLAN_COURSE = false;
	
    @Override
    public boolean checkRule(ElecContext context,
        TeachingClassCache courseClass)
    {
    	//课程code
    	String courseCode = courseClass.getCourseCode();
    	
    	//培养计划课程
    	Set<PlanCourse> planCourses = context.getPlanCourses();
    	
    	for (PlanCourse planCourse : planCourses) {
			if (StringUtils.equalsIgnoreCase(planCourse.getCourseCode(), courseCode)) {
				return IS_PLAN_COURSE;
			}
		}
    	
    	
        ElecRespose respose = context.getRespose();
        respose.getFailedReasons()
            .put(courseClass.getCourseCodeAndClassCode(),
                I18nUtil
                    .getMsg("ruleCheck.planCultureLimit"));
        return IS_NOT_PLAN_COURSE;
    }
    
}