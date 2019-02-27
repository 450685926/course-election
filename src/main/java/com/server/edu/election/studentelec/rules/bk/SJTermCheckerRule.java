package com.server.edu.election.studentelec.rules.bk;

import org.springframework.stereotype.Component;

import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;

/**
 * 控制实践课学期
 * 
 */
@Component("SJTermCheckerRule")
public class SJTermCheckerRule extends AbstractRuleExceutor
{
    
    //	protected PlanCreditLimitPrepare planCreditLimitPrepare;
    
    @Override
    public boolean checkRule(ElecContext context)
    {
        return true;
    }
    
    public void prepare()
    {
        //		//if (context.isPreparedData(PreparedDataName.SJ_COURSE_PLAN)) return;
        //		ElectState state = context.getState();
        //
        //		CoursePlan plan = context.getPlan();
        //		// 选课计划
        //		ElectCoursePlan electPlan = state.getCoursePlan();
        //		if (null == electPlan) {
        //			electPlan = new ElectCoursePlan();
        //			context.getState().setCoursePlan(electPlan);
        //		}
        //		// 根据培养计划设定courseIds(course.id->courseType.id的map)
        //		if (null != plan) {
        //			List<CourseGroup> groups = getTopCourseGroups(plan);
        //			for (CourseGroup group : groups) {
        //				addGroup(group, electPlan, null, state);
        //			}
        //		}
        //		
        //		//context.addPreparedDataName(PreparedDataName.SJ_COURSE_PLAN);
        
    }
    
    //	protected void addGroup(CourseGroup group, ElectCoursePlan electPlan, ElectCourseGroup parent,
    //	        ElectState state) {
    //		OqlBuilder<Long> query = OqlBuilder.from(CourseGrade.class.getName(),"grade");
    //		query.select(" grade.course.id").where(" grade.std.id =:stdId",state.getStd().getId()).cacheable();
    //		List<Long> hasLearnedCourseIds = entityDao.search(query);
    //		
    //		
    //		TJElectCourseGroup electGroup = new TJElectCourseGroup(group.getCourseType());
    //		electGroup.setParent(parent);
    //		electPlan.addGroup(electGroup);
    //		for (CourseGroup childGroup : group.getChildren()) {
    //			addGroup(childGroup, electPlan, electGroup, state);
    //		}
    //		@SuppressWarnings("unchecked")
    //		Integer term = (Integer) state.getParams().get("CURRENT_TERM");
    //		for (PlanCourse planCourse : group.getPlanCourses()) {
    //			
    //			Long courseId = planCourse.getCourse().getId();
    //			electPlan.courseIds.put(courseId, group.getCourseType().getId());
    //			if (TermCalculator.lessThanTerm(planCourse.getTerms(), term)) {//TODO 获取页面参数来决定该判断是否执行
    //				electGroup.addPlanCourse(planCourse);
    //			}else if(hasLearnedCourseIds.contains(planCourse.getCourse().getId())){
    //				electGroup.addPlanCourse(planCourse);
    //			}
    //		}
    //	}
    
    //	protected final List<CourseGroup> getTopCourseGroups(CoursePlan plan) {
    //		if (plan.getGroups() == null) { return new ArrayList<CourseGroup>(); }
    //		List<CourseGroup> res = new ArrayList<CourseGroup>();
    //		for (CourseGroup group : plan.getGroups()) {
    //			if (group != null && group.getParent() == null) {
    //				res.add((CourseGroup) group);
    //			}
    //		}
    //		return res;
    //	}
    
}