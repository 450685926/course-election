package com.server.edu.election.studentelec.rules.bk;

import org.springframework.stereotype.Component;

import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecCourseClass;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;

/**
 * 是选修课门数上限规则检查
 *
 */
@Component("XuanXiuMaxCountCheckerRule")
public class XuanXiuMaxCountCheckerRule extends AbstractRuleExceutor {
	
	public static final String MAX_COURSE_COUNT_PARAM = "XUAN_XIU_MAX_COURSE_COUNT";

	public static final String GOT_COURSE_COUNT_PARAM = "XUAN_XIU_GOT_COURSE_COUNT";
	
	private static final int COURSE_COUNT_STEP = 1;
	
	////为了查询是否是公选课，做了公选课缓存的注入
	//protected PublicCourseDataProvider publicCourseDataProvider;
	
	//TODO 这里是根据服务器缓存的公选课数据判断Lesson是否公选课，对性能是否有影响？
//	private boolean isXuanXiu(Lesson lesson, ElectState state){
//		// 是否在公选课组里
//		Long projectId = state.getStd().getProjectId();
//		Long semesterId = state.getSemesterId();
//		boolean old = state.getStd().getGrade().equals("2009");
//		PublicCourseData data = publicCourseDataProvider.getPubCourses(projectId, semesterId, old);
//		if (null != data && data.courseIds.contains(lesson.getCourse().getId())) return true;
//		return false;
//	}

	
    public void prepare() {
//		// 放入选修课门数上限
//		RuleConfigParam maxCountParam = uniqueParam(context.getState().getProfile(entityDao).getElectConfigs());
//		Integer maxCount = Integer.valueOf((String)maxCountParam.getValue());
//		context.getState().getParams().put(MAX_COURSE_COUNT_PARAM, maxCount);
//		// 放入已选的选修课门数
//		Integer gotCount = 0;
//		for(CourseTake take : context.getTakes()) {
//			if(isXuanXiu(take.getLesson(), context.getState())) {
//				gotCount += COURSE_COUNT_STEP;
//			}
//		}
//		context.getState().getParams().put(GOT_COURSE_COUNT_PARAM, gotCount);
	}

	/**
	 * 执行选课操作时
	 */
	@Override
    public boolean checkRule(ElecContext context, ElecCourseClass courseClass) {
//		ElectionCourseContext electContext = (ElectionCourseContext) context;
//		// 如果不是选课那就算了
//		if(electContext.getOp() != ElectRuleType.ELECTION) {
//			return true;
//		}
//		
//		Lesson lesson = electContext.getLesson();
//		ElectState state = electContext.getState();
//		Integer maxCount = (Integer) state.getParams().get(MAX_COURSE_COUNT_PARAM);	
//		Integer gotCount = (Integer) state.getParams().get(GOT_COURSE_COUNT_PARAM);
//		boolean overMax = false;
//		// 如果是选修课需要判断一下如果选上是否会超出上限
//		if(isXuanXiu(lesson, state)) {
//			overMax = gotCount + COURSE_COUNT_STEP > maxCount;
//		}
//		if(overMax) {
//			context.addMessage(new ElectMessage("超过选修课门数上限:" + maxCount, ElectRuleType.ELECTION, false, lesson));
//		}
//		return !overMax;
	    return false;
	}

}
