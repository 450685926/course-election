package com.server.edu.election.service.rule.election;

import com.server.edu.election.dto.ElcCourseTakeDto;
import com.server.edu.election.service.rule.context.AbstractElectRuleExecutor;
import com.server.edu.election.service.rule.context.ElectState;

/**
 * 培养计划课程组学分限制<br>
 * 被RetakeCourseBuildInPrepare这个ElectBuildInPrepare调用，因此不论用户页面有没有选这个规则，都会调用它的prepare
 */
public class PlanCourseGroupCreditsChecker extends AbstractElectRuleExecutor
{
    
    //protected PlanCreditLimitPrepare planCreditLimitPrepare;
    
    @Override
    public boolean execute(ElectState state, ElcCourseTakeDto courseTake)
    {
//        ElectionCourseContext electContext = (ElectionCourseContext)context;
        //		Lesson lesson = electContext.getLesson();
        //		ElectState state = electContext.getState();
        //		boolean noRetake = Boolean.TRUE.equals(state.getParams().get(ElectableLessonNoRetakeFilter.PARAM));
        //		boolean unCheckCredits = Boolean.TRUE.equals(state.getParams().get(
        //		        RetakeCheckByCoursePrepare.STATE_PARAM));
        //		// 不开放重修 或者 重修需要检查学分
        //		if (noRetake || !unCheckCredits) {
        //			if (electContext.getState().getCoursePlan().isOverMaxCredit(lesson)) {
        //				context.addMessage(new ElectMessage(lesson.getCourseType().getName() + " 学分已达上限",
        //				        ElectRuleType.ELECTION, false, lesson));
        //				return false;
        //			}
        //		}
        return true;
    }
    
    @Override
    public void prepare(ElectState context)
    {
        //planCreditLimitPrepare.prepare(context);
    }
    
}