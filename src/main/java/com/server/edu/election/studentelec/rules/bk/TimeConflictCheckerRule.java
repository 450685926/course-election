package com.server.edu.election.studentelec.rules.bk;

import java.util.*;
import java.util.stream.Collectors;

import com.server.edu.election.dao.TeachingClassDao;
import com.server.edu.election.studentelec.context.SelectedCourse;
import com.server.edu.election.studentelec.context.TimeUnit;
import com.server.edu.election.vo.SelectedCourseVo;
import com.server.edu.util.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.election.entity.ElcCourseTake;
import com.server.edu.election.entity.TeachingClass;
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
		Long teacherClassId = courseClass.getTeacherClassId();//通过teachingClassId查询时间
		if(teacherClassId!=null){
			List<SelectedCourseVo> teachingClassTime = teachingClassDao.findTeachingClassIdTime(teacherClassId);
			if(CollectionUtil.isNotEmpty(teachingClassTime)){
				List<TimeUnit> list=new ArrayList<>();
				Map<Long, List<SelectedCourseVo>> collect = teachingClassTime.stream().collect(Collectors.groupingBy(SelectedCourseVo::getArrangeTimeId));
				for (List<SelectedCourseVo> selectedCourseVos : collect.values()) {
					TimeUnit timeUnit=new TimeUnit();
					int dayOfWeek = selectedCourseVos.get(0).getDayOfWeek();
					int timeStart = selectedCourseVos.get(0).getTimeStart();
					int timeEnd = selectedCourseVos.get(0).getTimeEnd();
					timeUnit.setDayOfWeek(dayOfWeek);
					timeUnit.setTimeStart(timeStart);
					timeUnit.setTimeEnd(timeEnd);
					selectedCourseVos.forEach(temp->{
						timeUnit.getWeeks().add(temp.getWeek());
					});
					list.add(timeUnit);
				}
				Set<SelectedCourse> selectedCourses = context.getSelectedCourses();//已经选择的课程，时间班级
				if(CollectionUtil.isNotEmpty(selectedCourses)){
					selectedCourses.forEach(temp ->{
						List<TimeUnit> times = temp.getTimes();//比较是否冲突
						times.forEach(timeUnit -> {
							list.forEach(vo->{
								if(timeUnit.getDayOfWeek()==vo.getDayOfWeek()&&){

								}
							});
						});
					});
				}else{
					return true;
				}
			}else{//教学班按排没有上课时间
				return false;
			}
		}
		return false;
	}



}