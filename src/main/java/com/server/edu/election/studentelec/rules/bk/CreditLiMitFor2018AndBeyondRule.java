package com.server.edu.election.studentelec.rules.bk;

import java.util.*;
import java.util.stream.Collectors;

import com.server.edu.common.entity.BkPublicCourse;
import com.server.edu.common.entity.BkPublicCourseVo;
import com.server.edu.common.entity.PublicCourse;
import com.server.edu.election.constants.CourseTakeType;
import com.server.edu.election.dao.CourseDao;
import com.server.edu.election.dao.ElcStudentLimitDao;
import com.server.edu.election.entity.Course;
import com.server.edu.election.entity.ElcStudentLimit;
import com.server.edu.election.studentelec.context.ElecCourse;
import com.server.edu.election.studentelec.context.bk.CompletedCourse;
import com.server.edu.election.studentelec.context.bk.TsCourse;
import com.server.edu.election.studentelec.utils.ElecContextUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElectionConstantsDao;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.context.bk.SelectedCourse;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutorBk;
import com.server.edu.election.studentelec.rules.RulePriority;
import com.server.edu.election.studentelec.utils.RetakeCourseUtil;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;

/**
 * filter，过滤掉学生不能选的任务（校区不对），使得学生在界面上看不见
 *CampusFilter
 */
@Component("CreditLiMitFor2018AndBeyondRule")
public class CreditLiMitFor2018AndBeyondRule extends AbstractElecRuleExceutorBk
{
//    @Autowired
//    private ElectionConstantsDao electionConstantsDao;

	@Autowired
	private CourseDao courseDao;

    @Override
    public int getOrder()
    {
        return RulePriority.FIRST.ordinal();
    }
    
    @Override
    public boolean checkRule(ElecContextBk context,
        TeachingClassCache courseClass)
    {
    	//学生信息
        StudentInfoCache studentInfo = context.getStudentInfo();

		Integer grade1 = studentInfo.getGrade();
		if (grade1.intValue() < Constants.GRADE) {
			return true;
		}
        //请求参数
        ElecRequest request = context.getRequest();
        
        //已选课程
        Set<SelectedCourse> selectedCourses = context.getSelectedCourses();
        //已经完成的课程
		Set<CompletedCourse> completedCourses = context.getCompletedCourses();
		//当前正在修读的课程
		Set<CompletedCourse> unFinishedCourses = context.getUnFinishedCourses();

		//通识选修课
		List<BkPublicCourseVo> bkPublicCourse = ElecContextUtil.getBKPublicCourse();
		Map<String, Double> collectMap = new HashMap<>();
		Integer grade = studentInfo.getGrade();
		List<BkPublicCourse> list = new ArrayList<>();
		if (CollectionUtil.isNotEmpty(bkPublicCourse)){
			for (BkPublicCourseVo bkPublicCourseVo : bkPublicCourse) {
				String grades = bkPublicCourseVo.getGrades();
				if (compare(grade, grades)) {
					list = bkPublicCourseVo.getList();
					if (CollectionUtil.isNotEmpty(list)){
						List<PublicCourse> pList = new ArrayList<>();
						for (BkPublicCourse publicCourse : list) {
							if (CollectionUtil.isNotEmpty(publicCourse.getList())){
								pList.addAll(publicCourse.getList());
							}
						}
						collectMap = pList.stream().collect(Collectors.toMap(PublicCourse::getCourseCode, PublicCourse::getCreidits));
					}
					break;
				}
			}
		}
		//已选和已经完成的课程
		List<String>  courses = new ArrayList<>();
		if (CollectionUtil.isNotEmpty(selectedCourses)){
			for (SelectedCourse selectedCourse:selectedCourses) {
				courses.add(selectedCourse.getCourse().getCourseCode());
			}
		}
		if (CollectionUtil.isNotEmpty(completedCourses)){
			Double creidits = 0d;
			for (CompletedCourse completedCourse:completedCourses){
				courses.add(completedCourse.getCourse().getCourseCode());
				Double creidit = collectMap.get(completedCourse.getCourse().getCourseCode());
				if (creidit != null){
					creidits += creidit;
				}
			}
			if (creidits >= 8d){
				return true;
			}
		}
		if (CollectionUtil.isNotEmpty(unFinishedCourses)){
			for (CompletedCourse unFinishedCourse:unFinishedCourses){
				courses.add(unFinishedCourse.getCourse().getCourseCode());
			}
		}



		if (CollectionUtil.isNotEmpty(list)) {
			for (BkPublicCourse publicCourse : list) {
				List<PublicCourse> publicCourseList = publicCourse.getList();
				if (CollectionUtil.isNotEmpty(publicCourseList)) {
					List<String> collect = publicCourseList.stream().map(PublicCourse::getCourseCode).collect(Collectors.toList());
					if(collect.contains(courseClass.getCourseCode())){
						List<String> courseList = new ArrayList<>();
						int count = 0;
						for (String cours : courses) {
							if (collect.contains(cours)){
								courseList.add(cours);
								count ++;
							}
						}
						if (count <= 1){
							return true;
						}else{
							List<String> oldCourseList = new ArrayList<>();
							for (String s : courseList) {
								Example example = new Example(Course.class);
								example.createCriteria().andEqualTo("code",s);
								List<Course> coursesName = courseDao.selectByExample(example);
								if (CollectionUtil.isNotEmpty(coursesName)){
									oldCourseList.add(coursesName.get(0).getName());
								}
							}
							ElecRespose respose = context.getRespose();
							respose.getFailedReasons()
									.put(courseClass.getTeachClassCode() + courseClass.getCourseName(),
											I18nUtil
													.getMsg("ruleCheck.CreditLiMitFor2018AndBeyondLimit",publicCourse.getTag(),StringUtils.join(oldCourseList.toArray(),",")));
							return false;
						}
					}
				}
			}
		}

		return true;


/*
		//是否是重修课
		boolean count = RetakeCourseUtil.isRetakeCourseBk(context,
				courseClass.getCourseCode());
		String studentId = studentInfo.getStudentId();
		Long calendarId = context.getCalendarId();

		//查看选课限制：门数及学分
		ElcStudentLimit elcStudentLimit = getLimitNum(studentId, calendarId);
        
        //学生年级
        Integer grade = studentInfo.getGrade();
        if (grade.intValue() >= Constants.GRADE) {
			if (count)
			{//重修
				int totalNumber = 0;
				if(elcStudentLimit.getRebuildLimitNumber() != null) {
					totalNumber = elcStudentLimit.getRebuildLimitNumber();
				} else {
					String number = electionConstantsDao.findRebuildCourseNumber();
					if (StringUtils.isBlank(number))
					{
						return true;
					}
					totalNumber = Integer.valueOf(number);
				}
				Set<SelectedCourse> collect = selectedCourses.stream()
						.filter(selectedCourse -> CourseTakeType.RETAKE
								.eq(selectedCourse.getCourseTakeType()))
						.collect(Collectors.toSet());
				Set<SelectedCourse> publicCoursesCollect  = new HashSet<>();
				for (SelectedCourse selected:collect) {
					for (ElecCourse elec:publicCourses) {
						if (StringUtils.equalsIgnoreCase(selected.getCourse().getCourseCode(),elec.getCourseCode())){
							publicCoursesCollect.add(selected);
						}
					}
				}
				int size = publicCoursesCollect.size();//已选通识选修课重修门数
				if (size + 1 <= totalNumber)
				{
					return true;
				}
				else
				{
					ElecRespose respose = context.getRespose();
					respose.getFailedReasons()
							.put(courseClass.getCourseCodeAndClassCode(),
									I18nUtil
											.getMsg("ruleCheck.rebuildElecNumberLimit"));
					return false;
				}
			}
			else
			{//新选
				double num = 0.0;
				if(elcStudentLimit.getNewLimitCredits() != null) {
					num = elcStudentLimit.getNewLimitCredits();
				}else {
					String credits = electionConstantsDao.findNewCreditsLimit();
					if (StringUtils.isBlank(credits))
					{
						return true;
					}
					num = Double.parseDouble(credits);
				}
				Set<SelectedCourse> collect = selectedCourses.stream()
						.filter(selectedCourse -> CourseTakeType.NORMAL
								.eq(selectedCourse.getCourseTakeType()))
						.collect(Collectors.toSet());
				Set<SelectedCourse> publicCoursesCollect  = new HashSet<>();
				for (SelectedCourse selected:collect) {
					for (ElecCourse elec:publicCourses) {
						if (StringUtils.equalsIgnoreCase(selected.getCourse().getCourseCode(),elec.getCourseCode())){
							publicCoursesCollect.add(selected);
						}
					}
				}
				double size = publicCoursesCollect.stream()
						.collect(
								Collectors.summingDouble(c -> {return c.getCourse().getCredits();}));//已经新选学分
				Double curCredits = courseClass.getCredits();//当前课程学分
				if (curCredits + size <= num)
				{
					return true;
				}
				else
				{
					ElecRespose respose = context.getRespose();
					respose.getFailedReasons()
							.put(courseClass.getCourseCodeAndClassCode(),
									I18nUtil.getMsg("ruleCheck.creditsLimit"));
					return false;
				}
			}

		}
        return true;*/
    }

	/*private ElcStudentLimit getLimitNum(String studentId, Long calendarId) {
		Example example  = new Example(ElcStudentLimit.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("studentId", studentId);
		criteria.andEqualTo("calendarId", calendarId);
		ElcStudentLimit elcStudentLimit = elcStudentLimitDao.selectOneByExample(example);
		if(elcStudentLimit==null) {
			elcStudentLimit = new ElcStudentLimit();
		}
		return elcStudentLimit;
	}*/
	// 判断学生年级是否符合通识选修课年级
	private boolean compare(Integer grade, String grades) {
		if (StringUtils.isNotBlank(grades)) {
			String[] split = grades.split(",");
			if (split.length > 1) {
				Integer start = Integer.parseInt(split[0]);
				Integer end = Integer.parseInt(split[1]);
				int value = grade.intValue();
				if (start.intValue() <= value && value <= end.intValue()) {
					return true;
				}
			} else {
				Integer start = Integer.parseInt(split[0]);
				if (start.intValue() <= grade) {
					return true;
				}
			}
		}
		return false;
	}
}