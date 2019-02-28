package com.server.edu.election.studentelec.rules.bk;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.TeachingClassDao;
import com.server.edu.election.entity.TeachingClass;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecCourseClass;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;

/**
 * 限制不能选择重修课
 *
 */
@Component("NoRetakeRule")
public class NoRetakeRule extends AbstractRuleExceutor
{
	@Autowired
	private TeachingClassDao teachingClassDao;
    
    @Override
    public boolean checkRule(ElecContext context, ElecCourseClass courseClass)
    {
    	if(courseClass.getTeacherClassId()!=null) {
    		TeachingClass teachingClass = teachingClassDao.selectByPrimaryKey(courseClass.getTeacherClassId());
			if(teachingClass!=null) {
				if(Constants.ORDINARY_CALSS.equals(teachingClass.getClassType())) {
					return true;
				}
			}
    	}
        return false;
    }
    
}