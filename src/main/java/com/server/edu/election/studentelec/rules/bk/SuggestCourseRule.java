package com.server.edu.election.studentelec.rules.bk;

import org.springframework.stereotype.Component;

import com.server.edu.election.entity.TeachingClass;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;
import com.server.edu.election.studentelec.rules.RulePriority;

/**
 * 如果有课程开启了“建议课表”选课，并且该选课批次也开启了“建议课表”选课。<br>
 * 那么学生看到的这些课的任务都是适合自己的（逻辑类似于根据教学班过滤任务）
 */
@Component("SuggestCourseRule")
public class SuggestCourseRule extends AbstractRuleExceutor {
	private static final String PARAM_NAME = "SUGGESTED_COURSE_LIST";

	@Override
	public int getOrder() {
		return RulePriority.FOURTH.ordinal();
	}

	public void prepare() {
		// if(context.getState().getParams().get(PARAM_NAME) == null){
		// context.getState().getParams().put(PARAM_NAME, getSuggestCourses());
		// }
	}

	/**
	 * 在学生选课时，被onExecuteRuleReturn调用<br>
	 * 在学生进入选课界面时被调用 FIXME 这个数据有多少???
	 */
	@Override
	public boolean checkRule(ElecContext context, TeachingClassCache courseClass) {
		// List<Course> courses = (List<Course>) state.getParams().get(PARAM_NAME);
		// if(courses == null) {
		// courses = getSuggestCourses();
		// state.getParams().put(PARAM_NAME, getSuggestCourses());
		// }
		// if(!courses.contains(lesson.getCourse())) {
		// return true;
		// }
		//
		// return dirtyWork(lesson, state);
//      if(!result) {
		// context.addMessage(new ElectMessage("请按照建议课表选课", ElectRuleType.ELECTION,
		// false, context.getLesson()));
		// }
		return false;
	}

	/**
	 * 获得开启建议课表选课的课程
	 * 
	 * @return FIXME 这个数据有多少???
	 */
	// private List<Course> getSuggestCourses() {
	// OqlBuilder query = OqlBuilder.from(CourseSuggestSwitch.class, "sw");
	// query.select("sw.course").where("sw.open = true");
	// List<Course> courses = entityDao.search(query);
	// return courses;
	// }

	/**
	 * 检查是否能选课的脏活类活
	 * 
	 * @param lesson
	 * @param state
	 * @return
	 */
	public boolean dirtyWork(TeachingClass lesson) {
		// // 如果是重修，且重修不检查教学班，那么学生肯定能选这个课
		// if(retakeService.isRetakeCourse(state, lesson.getCourse().getId()) &&
		// !retakeService.isCheckTeachClass(state.getProfile(entityDao).getElectConfigs())){
		// return true;
		// }
		// // 只有当不是重修，或者重修时也检查教学班的时候才需要检查
		// TeachClass teachClass = lesson.getTeachClass();
		//
		// for(CourseLimitGroup group: teachClass.getLimitGroups()){
		// boolean groupPass = true;
		// for(CourseLimitItem item: group.getItems()){
		// boolean itemPass = true;
		//
		// Operator op = item.getOperator();
		// Long metaId = item.getMeta().getId();
		// Set<String> values = CollectUtils.newHashSet(item.getContent().split(","));
		// String value = null;
		// if (metaId.equals(CourseLimitMeta.ADMINCLASS)) {
		// value = state.getStd().getAdminclassId() + "";
		// } else if (metaId.equals(CourseLimitMeta.DEPARTMENT)) {
		// value = state.getStd().getDepartId() + "";
		// } else if (metaId.equals(CourseLimitMeta.DIRECTION)) {
		// value = state.getStd().getDirectionId() + "";
		// } else if (metaId.equals(CourseLimitMeta.EDUCATION)) {
		// value = state.getStd().getEducationId() + "";
		// } else if (metaId.equals(CourseLimitMeta.GENDER)) {
		// value = state.getStd().getGenderId() + "";
		// } else if (metaId.equals(CourseLimitMeta.GRADE)) {
		// value = state.getStd().getGrade();
		// } else if (metaId.equals(CourseLimitMeta.MAJOR)) {
		// value = state.getStd().getMajorId() + "";
		// } else if (metaId.equals(CourseLimitMeta.STDTYPE)) {
		// value = state.getStd().getStdTypeId() + "";
		// } else if (metaId.equals(CourseLimitMeta.PROGRAM)) {
		//// if(state.getStd().isHasPersonalProgram()) {
		//// // 如果学生的是个人计划
		//// if(!values.isEmpty()) {
		//// // hack 如果学生是个人计划，那么实际上不用限制他的个人计划必须和专业计划匹配
		//// // 所以将value变成values里面的第一个值，这样在下面的values.contains(value)就能够返回true了。
		//// value = values.iterator().next();
		//// }
		//// } else {
		//// // 如果学生的没有个人计划（有专业计划，或者连专业计划也没有）
		//// value = state.getStd().getProgramId() + "";
		//// }
		// value = state.getStd().getProgramId() + "";
		// }
		//
		// if (op.equals(Operator.EQUAL) || op.equals(Operator.IN)) {
		// itemPass = values.isEmpty() || values.contains(value);
		// } else {
		// itemPass = !values.isEmpty() && !values.contains(value);
		// }
		// // 如果单项没通过，那么就整个组就不通过了
		// if(!itemPass) {
		// groupPass = false;
		// break;
		// }
		// }
		// // 只要有一个组通过，那么就算通过了
		// if(groupPass) {
		// return groupPass;
		// }
		// }
		// 如果没有在循环里return，那么就说明学生不适合这个任务
		return false;
	}

}
