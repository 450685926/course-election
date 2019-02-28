package com.server.edu.election.studentelec.rules.bk;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.TeachingClassDao;
import com.server.edu.election.entity.TeachingClass;
import com.server.edu.election.studentelec.context.CompletedCourse;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecCourseClass;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;
import com.server.edu.util.CollectionUtil;

/**
 * 通过课程只能重修一次
 * 
 */
@Component("CanRetakeOnlyOnceRule")
public class CanRetakeOnlyOnceRule extends AbstractRuleExceutor {
	@Autowired
	private TeachingClassDao teachingClassDao;

	@Override
	public boolean checkRule(ElecContext context, ElecCourseClass courseClass) {
		// TODO Auto-generated method stub
		List<CompletedCourse> completedCourses = context.getCompletedCourses();
		Long id = courseClass.getTeacherClassId();
		if (id != null) {
    		TeachingClass teachingClass = teachingClassDao.selectByPrimaryKey(id);
			if (teachingClass!=null && CollectionUtil.isNotEmpty(completedCourses)) {
				if (courseClass.getCourseCode() != null) {
					List<CompletedCourse> list = completedCourses.stream()
							.filter(c -> courseClass.getCourseCode().equals(c.getCourseCode()))
							.collect(Collectors.toList());
					if (list.size() > Constants.ONE) {
						if (Constants.REBUILD_CALSS.equals(teachingClass.getClassType())) {
							ElecRespose respose = context.getRespose();
							respose.getFailedReasons().put(courseClass.getTeacherClassId(),
									I18nUtil.getMsg("ruleCheck.canRetakeOnlyOnce"));
							return false;
						} 
					}
				}
			}
		}
		return true;
	}

}
