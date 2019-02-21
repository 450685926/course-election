package com.server.edu.election.service.rule.election.filter;

import java.util.Set;

import com.server.edu.election.entity.TeachingClass;
import com.server.edu.election.service.rule.context.AbstractElectRuleExecutor;
import com.server.edu.election.service.rule.context.ElectState;
import com.server.edu.election.service.rule.filter.AbstractElectableLessonFilter;

/**
 * 结业生通过课程不能重修
 */
public class UnElectLessonByPassedFornoGraduate
    extends AbstractElectableLessonFilter
{
    
    @Override
    public int getOrder()
    {
        return AbstractElectRuleExecutor.Priority.FIFTH.ordinal();
    }
    
    @Override
    public boolean isElectable(TeachingClass lesson, ElectState state)
    {
        //		Long courseId = lesson.getCourse().getId();
        		Set<String> passedCourses = state.getPassedCourseIds();
        //		if (passedCourses.contains(courseId)) { return false; }
        //		for (ElectCourseSubstitution courseSubstitution : state.getCourseSubstitutions()) {
        //			if (courseSubstitution.getSubstitutes().contains(courseId)) {
        //				for (Long originCourseId : courseSubstitution.getOrigins()) {
        //					if (passedCourses.contains(originCourseId)) { return false; }
        //				}
        //			}
        //		}
//        if (!result)
//        {
//            new ElectMessage("通过课程不允许重修", ElectRuleType.ELECTION, false,
//                getTeachingClass(context.getCourseTake().getTeachingClassId()));
//        }
        return true;
    }
    
    
    @Override
    public void prepare(ElectState context)
    {
        //context.getState().getParams().put("PASSED_CANNOT_RETAKE", true);
    }
    
}