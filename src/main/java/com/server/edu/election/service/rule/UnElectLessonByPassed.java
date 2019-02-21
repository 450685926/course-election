package com.server.edu.election.service.rule;

import java.util.Set;

import com.server.edu.election.entity.TeachingClass;
import com.server.edu.election.service.rule.context.AbstractElectRuleExecutor;
import com.server.edu.election.service.rule.context.ElectState;
import com.server.edu.election.service.rule.filter.AbstractElectableLessonFilter;

/**
 * 
 * 考试通过的课程不能选择 
 */
public class UnElectLessonByPassed extends AbstractElectableLessonFilter
{
    @Override
    public int getOrder()
    {
        return AbstractElectRuleExecutor.Priority.FIFTH.ordinal();
    }
    
    //	@Override
    @Override
    public boolean isElectable(TeachingClass lesson, ElectState state)
    {
        //		Long courseId = lesson.getCourse().getId();
        		Set<String>passedCourses = state.getPassedCourseIds();
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
    
    @Override
    public void prepare(ElectState context)
    {
        //context.getState().getParams().put("PASSED_CANNOT_RETAKE", true);
    }
    
}