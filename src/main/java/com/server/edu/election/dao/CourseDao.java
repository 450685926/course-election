package com.server.edu.election.dao;

import java.util.List;

import com.server.edu.election.entity.Course;
import com.server.edu.election.entity.ElectionConstants;

import tk.mybatis.mapper.common.Mapper;

public interface CourseDao extends Mapper<Course> {
	List<Course> getPEorEnglishCourses(ElectionConstants electionConstants);

	String getCourseLabelName(Long label);

}