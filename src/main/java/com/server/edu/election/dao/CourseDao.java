package com.server.edu.election.dao;

import com.server.edu.election.entity.Course;

import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CourseDao extends Mapper<Course> {

	String getCourseLabelName(Long label);


	List<String> getAllCoursesLevelCourse();

}