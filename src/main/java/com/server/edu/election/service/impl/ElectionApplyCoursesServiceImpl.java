package com.server.edu.election.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.CourseDao;
import com.server.edu.election.dao.ElectionApplyCoursesDao;
import com.server.edu.election.dao.ElectionConstantsDao;
import com.server.edu.election.dto.CourseDto;
import com.server.edu.election.dto.ElectionApplyCoursesDto;
import com.server.edu.election.entity.Course;
import com.server.edu.election.entity.ElectionApplyCourses;
import com.server.edu.election.entity.ElectionConstants;
import com.server.edu.election.service.ElectionApplyCoursesService;
import com.server.edu.election.studentelec.utils.ElecContextUtil;
import com.server.edu.election.vo.ElectionApplyCoursesVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;
@Service
public class ElectionApplyCoursesServiceImpl implements ElectionApplyCoursesService {
	@Autowired
	private CourseDao courseDao;
	@Autowired
	private  ElectionApplyCoursesDao electionApplyCoursesDao;
	@Autowired
	private  ElectionConstantsDao electionConstantsDao;
	@Override
	public PageInfo<ElectionApplyCoursesVo> applyCourseList(PageCondition<ElectionApplyCoursesDto> condition){
		ElectionApplyCoursesDto dto = condition.getCondition();
		PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
		List<ElectionApplyCoursesVo> list = electionApplyCoursesDao.selectApplyCourse(dto);
		PageInfo<ElectionApplyCoursesVo> pageInfo = new PageInfo<>(list);
		return pageInfo;
	}
	
	@Override
	public PageInfo<Course> courseList(PageCondition<CourseDto> condition){
		PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
		CourseDto dto = condition.getCondition();
		Integer model = dto.getMode();
		List<Course> list = new ArrayList<>();
		ElectionConstants electionConstants = new ElectionConstants();
		if(Constants.NORMAL_MODEL.equals(model)) {
			Example example = new Example(Course.class);
			Example.Criteria criteria = example.createCriteria();
			criteria.andEqualTo("status", Constants.THREE);
			if(StringUtils.isNotBlank(dto.getCode())) {
				criteria.andLike("code", dto.getCode()+'%');
			}
			list = courseDao.selectByExample(example);
		}else if (Constants.ENGLISH_MODEL.equals(model)) {
			electionConstants.setManagerDeptId("1");
			electionConstants.setKey("ENGLISH_COURSE_CODES");
			list = courseDao.getPEorEnglishCourses(electionConstants);
		}else {
			electionConstants.setManagerDeptId("1");
			electionConstants.setKey("PE_COURSE_CODES");
			list = courseDao.getPEorEnglishCourses(electionConstants);
		}
		PageInfo<Course> pageInfo = new PageInfo<>(list);
		return pageInfo;
		
	}
	
	@Override
	public int addCourses(ElectionApplyCoursesDto dto) {
		Example courseExample = new Example(Course.class);
		Example.Criteria courdseCriteria = courseExample.createCriteria();
		courdseCriteria.andIn("code",dto.getCourses());
		List<Course> courses = courseDao.selectByExample(courseExample);
		if(CollectionUtil.isEmpty(courses)) {
			throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
		}
		List<ElectionApplyCourses> list = new ArrayList<>();
		Set<String> applyCourses = new HashSet<>();
		for(String course:dto.getCourses()) {
			ElectionApplyCourses electionApplyCourses = new ElectionApplyCourses();
			electionApplyCourses.setMode(dto.getMode());
			electionApplyCourses.setCourseCode(course);
			electionApplyCourses.setCalendarId(dto.getCalendarId());
			list.add(electionApplyCourses);
			applyCourses.add(course);
		}
		int result = electionApplyCoursesDao.insertList(list);
		//存入redis
		ElecContextUtil.setApplyCourse(dto.getCalendarId(), applyCourses);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.saveError",I18nUtil.getMsg("electionApply.electionApplyCourses")));
		}
		return result;
	}
	
	@Override
	public int deleteCourses(List<Long> ids) {
		Example example = new Example(ElectionApplyCourses.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andIn("id",ids);
		List<ElectionApplyCourses> list = electionApplyCoursesDao.selectByExample(example);
		if(CollectionUtil.isEmpty(list)) {
			throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
		}
		Long calendarId= list.get(0).getCalendarId();
		Set<String> applyCourses = new HashSet<>();
		for(ElectionApplyCourses electionApplyCourses:list) {
			applyCourses.add(electionApplyCourses.getCourseCode());
		}
		//存入redis
		ElecContextUtil.setApplyCourse(calendarId, applyCourses);
		int result = electionApplyCoursesDao.deleteByExample(example);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.failSuccess",I18nUtil.getMsg("electionApply.electionApplyCourses")));
		}
		return result;
	}

}
