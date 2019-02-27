package com.server.edu.election.studentelec.rules.bk;

import org.springframework.stereotype.Component;

import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;
import com.server.edu.election.studentelec.rules.RulePriority;

/**
 * 考试通过的课程不能选择 
 */
@Component("UnElectLessonByPassed")
public class UnElectLessonByPassed extends AbstractRuleExceutor
{
    @Override
    public int getOrder()
    {
        return RulePriority.FIFTH.ordinal();
    }
    
    //	@Override
    @Override
    public boolean checkRule(ElecContext context)
    {
        //		Long courseId = lesson.getCourse().getId();
//        		Set<String>passedCourses = state.getPassedCourseIds();
        //		if (passedCourses.contains(courseId)) {
        //			return false;
        //		}
        //		Boolean passed=null;
        //		for (ElectCourseSubstitution courseSubstitution : state.getCourseSubstitutions()) {
        //			if (courseSubstitution.getSubstitutes().contains(courseId)) {
        //				for (Long originCourseId : courseSubstitution.getOrigins()) {
        //					if(null==passed) passed=Boolean.TRUE;
        //					passed&= (passedCourses.contains(originCourseId));
        //				}
        //			}
        //		}
        //		return null==passed?true:!passed;
        //      if (!result) {
        //          context.addMessage(new ElectMessage("通过课程不允许重修",ElectRuleType.ELECTION,false,context.getLesson()));
        //      }
        return false;
    }
    
}