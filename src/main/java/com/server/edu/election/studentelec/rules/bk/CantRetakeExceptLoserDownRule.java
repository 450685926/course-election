package com.server.edu.election.studentelec.rules.bk;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.TeachingClassDao;
import com.server.edu.election.entity.TeachingClass;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecCourseClass;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;

/**
 * 不能重修，但是留降级学生除外
 *
 */
@Component("CantRetakeExceptLoserDownRule")
public class CantRetakeExceptLoserDownRule extends AbstractRuleExceutor {
	@Autowired
	private TeachingClassDao teachingClassDao;

	@Override
	public boolean checkRule(ElecContext context, ElecCourseClass courseClass) {
		StudentInfoCache studentInfo = context.getStudentInfo();
		if (courseClass.getTeacherClassId()!=null) {
			TeachingClass teachingClass = teachingClassDao.selectByPrimaryKey(courseClass.getTeacherClassId());
			if(teachingClass!=null) {
				if (Constants.REBUILD_CALSS.equals(teachingClass.getClassType())) {
					if(studentInfo.isRepeater()) {
						return true;
					}else {
						ElecRespose respose = context.getRespose();
						respose.getFailedReasons().put(courseClass.getTeacherClassId(),
								I18nUtil.getMsg("ruleCheck.cantRetakeExceptLoserDown"));
					}
				}
			}
		} 
		return false;
	}

}