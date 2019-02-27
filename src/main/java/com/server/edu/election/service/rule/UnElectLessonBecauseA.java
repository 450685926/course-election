package com.server.edu.election.service.rule;

import com.server.edu.election.entity.TeachingClass;
import com.server.edu.election.service.rule.context.AbstractElectRuleExecutor;
import com.server.edu.election.service.rule.context.ElectState;
import com.server.edu.election.service.rule.filter.AbstractElectableLessonFilter;

public class UnElectLessonBecauseA extends AbstractElectableLessonFilter
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
        //		List<Long> gotACourseIds = state.getGotAcourseIds();
        //		if (gotACourseIds.contains(courseId)) {
        //			return false;
        //		}
        //		Boolean gotA=null;
        //		for (ElectCourseSubstitution courseSubstitution : state.getCourseSubstitutions()) {
        //			if (courseSubstitution.getSubstitutes().contains(courseId)) {
        //				for (Long originCourseId : courseSubstitution.getOrigins()) {
        //					if(null==gotA) gotA=Boolean.TRUE;
        //					gotA &= (gotACourseIds.contains(originCourseId));
        //				}
        //			}
        //		}
        //		return null==gotA?true:!gotA;
        //      if (!result) {
        //          context.addMessage(new ElectMessage("通过课程得优不允许重修",ElectRuleType.ELECTION,false,context.getLesson()));
        //      }
        return false;
    }
    
}