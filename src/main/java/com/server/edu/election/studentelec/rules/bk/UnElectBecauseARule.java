package com.server.edu.election.studentelec.rules.bk;

import org.springframework.stereotype.Component;

import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecCourseClass;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;
import com.server.edu.election.studentelec.rules.RulePriority;

/**
 * 得优课程不能重修
 * 
 */
@Component("UnElectBecauseARule")
public class UnElectBecauseARule extends AbstractRuleExceutor
{
    @Override
    public int getOrder()
    {
        return RulePriority.FIFTH.ordinal();
    }
    
    //	@Override
    @Override
    public boolean checkRule(ElecContext context, ElecCourseClass courseClass)
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