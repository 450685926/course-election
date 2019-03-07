package com.server.edu.election.studentelec.rules.bk;

import java.util.*;


import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.dao.TeachingClassDao;
import com.server.edu.election.studentelec.context.ClassTimeUnit;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.SelectedCourse;

import com.server.edu.util.CollectionUtil;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;

/**
 * 时间冲突检查 实施中可以设置三个参数，顺序不能混淆 1、检查的冲突节次数量 取值必须是正整数,如果小于零系统会认为是0 2、检查类型
 * 用来设置是检查冲突节次还是检查空闲节次 取值为 YES、Y、 T、TRUE、真、是 3、是否连续 表示检查的时候小节按照连续还是不连续来进行检查
 */
@Component("TimeConflictCheckerRule")
public class TimeConflictCheckerRule extends AbstractRuleExceutor {

	public static final Boolean CHECK_CONFLICT = true;

	public static final Boolean CHECK_UN_CONFLICT = false;

	@Autowired
	private TeachingClassDao teachingClassDao;

	@Override
	@SuppressWarnings("unchecked")
	public boolean checkRule(ElecContext context, TeachingClassCache courseClass) {
		Long teachClassId = courseClass.getTeachClassId();//通过teachingClassId查询时间
		if(teachClassId!=null){
			List<ClassTimeUnit> teachingClassTime = teachingClassDao.findTeachingClassIdTime(teachClassId);
			if(CollectionUtil.isNotEmpty(teachingClassTime)){
				Set<SelectedCourse> selectedCourses = context.getSelectedCourses();//已经选择的课程，时间班级
				if(CollectionUtil.isNotEmpty(selectedCourses)){
					for (SelectedCourse selectedCours : selectedCourses) {
						List<ClassTimeUnit> times = selectedCours.getTimes();
						for (ClassTimeUnit v0 : teachingClassTime) {
							for (ClassTimeUnit v1 : times) {
								if(v0.getDayOfWeek()==v1.getDayOfWeek() && v0.getTimeStart()==v1.getTimeStart() && v0.getWeekNumber().intValue()==v1.getWeekNumber().intValue()){
									ElecRespose respose = context.getRespose();
									respose.getFailedReasons().put(courseClass.getTeachClassId().toString(),
											I18nUtil.getMsg("ruleCheck.timeConflict"));
									return false;
								}
							}
						}
					}
					return true;

				}else{
					return true;
				}
			}
		}
		return false;
	}



}