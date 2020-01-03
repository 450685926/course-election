package com.server.edu.election.dao;

import java.util.List;

import com.github.pagehelper.Page;
import com.server.edu.election.dto.CourseDto;
import com.server.edu.election.dto.ElectionApplyCoursesDto;
import com.server.edu.election.entity.Course;
import com.server.edu.election.entity.ElectionApplyCourses;
import com.server.edu.election.vo.ElectionApplyCoursesVo;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

public interface ElectionApplyCoursesDao extends Mapper<ElectionApplyCourses>,MySqlMapper<ElectionApplyCourses>{
	List<ElectionApplyCoursesVo> selectApplyCourse(ElectionApplyCoursesDto dto);

	Page<Course> getApplyCourse2Add(CourseDto course);

	Page<Course> getApplyCourse3Add(CourseDto course);

	Page<Course> getApplyCourse4Add(CourseDto course);
}