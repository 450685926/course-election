package com.server.edu.election.studentelec.rules.bk;

import org.springframework.stereotype.Component;

import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;

/**
 * 培养计划课程组学分限制<br>
 * 被RetakeCourseBuildInPrepare这个ElectBuildInPrepare调用，因此不论用户页面有没有选这个规则，都会调用它的prepare
 */
@Component("PlanCourseGroupCreditsRule")
public class PlanCourseGroupCreditsRule extends AbstractRuleExceutor
{
    
    //protected PlanCreditLimitPrepare planCreditLimitPrepare;
    
    @Override
    public boolean checkRule(ElecContext context)
    {
//        ElectionCourseContext electContext = (ElectionCourseContext)context;
        //		Lesson lesson = electContext.getLesson();
        //		ElectState state = electContext.getState();
        //		boolean noRetake = Boolean.TRUE.equals(state.getParams().get(NoRetakeRule.PARAM));
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
    
    public void prepare()
    {
        //planCreditLimitPrepare.run(context);
    }
    
}