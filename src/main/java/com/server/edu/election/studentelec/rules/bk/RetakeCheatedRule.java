package com.server.edu.election.studentelec.rules.bk;

import org.springframework.stereotype.Component;

import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecCourseClass;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;

/**
 * 重修违纪检查
 *
 */
@Component("RetakeCheatedRule")
public class RetakeCheatedRule extends AbstractRuleExceutor
{
    
    //private SemesterService semesterService;
    
    public static final String STATE_PARAM_CHECTED_COURSES = "CHECTED_COURSES";
    
    public static final String STATE_PARAM_CHECTED_SUB_COURSES =
        "CHECTED_SUB_COURSES";
    
    private static final String RULE_PARAM_VIOLATIONS = "VIOLATIONS";
    
    private static final String RULE_PARAM_SEMESTER = "SEMESTER";
    
    public void prepare()
    {
        //		if(!context.isPreparedData(PreparedDataName.CHECTED_COURSES)){
        //			Set<Long> cheatedCourseSubstitutionIds = CollectUtils.newHashSet();
        //			//初始化违纪记录
        //			String[] examStatusNames = null;
        //			
        //			Set<RuleConfigParam> params = getParams(context.getState().getProfile(entityDao).getElectConfigs());
        //			
        //			RuleConfigParam violationParam = null;
        //			RuleConfigParam semesterParam = null;
        //			for(RuleConfigParam param : params) {
        //				if(RULE_PARAM_VIOLATIONS.equals(param.getParam().getName())) {
        //					violationParam = param;
        //				} else if (RULE_PARAM_SEMESTER.equals(param.getParam().getName())) {
        //					semesterParam = param;
        //				}
        //			}
        //			// 保证已经实施的学校的在使用该规则时不会受到这次更改的影响
        //			if(violationParam == null) {
        //				violationParam = uniqueParam(context.getState().getProfile(entityDao).getElectConfigs());
        //			}
        //			
        //			if(null != violationParam && StringUtils.isNotBlank(violationParam.getValue())){
        //				examStatusNames = StringUtils.split(StringUtils.trim(violationParam.getValue()),",");
        //			}
        //			Map<Long,Course> cheatedCourses = CollectUtils.newHashMap();
        //			if(null != examStatusNames){
        //				OqlBuilder<Course> builder = OqlBuilder.from(CourseGrade.class.getName() + " courseGrade");
        //				builder.select("select distinct courseGrade.course");
        //				builder.where("courseGrade.std=:std", context.getStudent());
        //				builder.join("courseGrade.examGrades", "examGrade");
        //				builder.where("examGrade.examStatus.name in(:violationNames)", examStatusNames);	
        //				if(semesterParam != null) {
        //					Semester prevSemester = semesterService.getPrevSemester(context.getState().getSemester(entityDao));
        //					if(prevSemester != null) {
        //						builder.where("courseGrade.semester = :prevSemester", prevSemester);
        //					}
        //				}
        //				Set<Course> cheatedCourseSet = CollectUtils.newHashSet(entityDao.search(builder));
        //				for (Course course : cheatedCourseSet) {
        //					for (ElectCourseSubstitution courseSubstitution : context.getState().getCourseSubstitutions()) {
        //						if(courseSubstitution.getOrigins().contains(course.getId())){
        //							cheatedCourseSubstitutionIds.addAll(courseSubstitution.getSubstitutes());
        //						}
        //					}
        //					Course _course = Model.newInstance(Course.class, course.getId());
        //					_course.setId(course.getId());
        //					_course.setCode(course.getCode());
        //					_course.setName(course.getName());
        //					_course.setEngName(course.getEngName());
        //					_course.setCredits(course.getCredits());
        //					cheatedCourses.put(course.getId(), _course);
        //				}
        //			}
        //			
        //			context.getState().getParams().put(STATE_PARAM_CHECTED_COURSES, cheatedCourses);
        //			context.getState().getParams().put(STATE_PARAM_CHECTED_SUB_COURSES, cheatedCourseSubstitutionIds);
        //			context.addPreparedDataName(PreparedDataName.CHECTED_COURSES);
        //		}
    }
    
    @Override
    public boolean checkRule(ElecContext context, ElecCourseClass courseClass)
    {
        //		Map<Long,Course> cheatedCourses = (Map<Long, Course>) state.getParams().get(STATE_PARAM_CHECTED_COURSES);
        //		Set<Long> cheatedCourseSubstitutionIds = (Set <Long>) state.getParams().get(STATE_PARAM_CHECTED_SUB_COURSES);
        //		return !cheatedCourses.containsKey(lesson.getCourse().getId()) && !cheatedCourseSubstitutionIds.contains(lesson.getCourse().getId());
        //      if(!result){
        //          context.addMessage(new ElectMessage("有违纪记录,不能重修",ElectRuleType.ELECTION,false,context.getLesson()));
        //      }
        return false;
    }
    
}