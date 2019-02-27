package com.server.edu.election.studentelec.rules.bk;

import org.springframework.stereotype.Component;

import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecCourseClass;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;
import com.server.edu.election.studentelec.rules.RulePriority;

/**
 * 结业生通过课程不能重修
 */
@Component("UnElectLessonByPassedFornoGraduate")
public class UnElectLessonByPassedFornoGraduate extends AbstractRuleExceutor
{
    
    @Override
    public int getOrder()
    {
        return RulePriority.FIFTH.ordinal();
    }
    
    @Override
    public boolean checkRule(ElecContext context, ElecCourseClass courseClass)
    {
        //		Long courseId = lesson.getCourse().getId();
        //        		Set<String> passedCourses = state.getPassedCourseIds();
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
    
}