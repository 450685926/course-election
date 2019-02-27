package com.server.edu.election.studentelec.rules.bk;

import org.springframework.stereotype.Component;

import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecCourseClass;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;
import com.server.edu.election.studentelec.rules.RulePriority;

/**
 * 只允许选重修课
 * 
 */
@Component("OnlyRetakeFilter")
public class OnlyRetakeFilter extends AbstractRuleExceutor
{
    
    //    @Autowired
    //	protected CourseGradePrepare courseGradePrepare;
    
    @Override
    public int getOrder()
    {
        return RulePriority.FIRST.ordinal();
    }
    
    // @Override
    @Override
    public boolean checkRule(ElecContext context, ElecCourseClass courseClass)
    {
        //return state.isRetakeCourse(lesson.getCourse().getId());
        //      if (!result) {
        //          context.addMessage(new ElectMessage("只能选择重修课", ElectRuleType.ELECTION, false, context.getLesson()));
        //      }
        //courseGradePrepare.run(context);
        return false;
    }
    
}