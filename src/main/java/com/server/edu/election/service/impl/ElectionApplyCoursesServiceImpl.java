package com.server.edu.election.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.CourseDao;
import com.server.edu.election.dao.ElectionApplyCoursesDao;
import com.server.edu.election.dto.ElectionApplyCoursesDto;
import com.server.edu.election.entity.Course;
import com.server.edu.election.entity.ElectionApplyCourses;
import com.server.edu.election.service.ElectionApplyCoursesService;
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
	@Override
	public PageInfo<ElectionApplyCoursesVo> applyCourseList(PageCondition<ElectionApplyCoursesDto> condition){
		ElectionApplyCoursesDto dto = condition.getCondition();
		PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
		List<ElectionApplyCoursesVo> list = electionApplyCoursesDao.selectApplyCourse(dto);
		PageInfo<ElectionApplyCoursesVo> pageInfo = new PageInfo<>(list);
		return pageInfo;
	}
	
	@Override
	public PageInfo<Course> courseList(PageCondition<Course> condition){
		PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
		Example example = new Example(Course.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("status", Constants.THREE);
		List<Course> list = courseDao.selectByExample(example);
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
		Example example = new Example(ElectionApplyCourses.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("calendarId", dto.getCalendarId());
		criteria.andIn("courseCode",dto.getCourses());
		int result = electionApplyCoursesDao.deleteByExample(example);
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
		int result = electionApplyCoursesDao.deleteByExample(example);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.failSuccess",I18nUtil.getMsg("electionApply.electionApplyCourses")));
		}
		return result;
	}

}
