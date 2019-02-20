package com.server.edu.election.dao;

import java.util.List;

import com.server.edu.election.entity.CourseOpen;

import tk.mybatis.mapper.common.Mapper;

public interface CourseOpenDao extends Mapper<CourseOpen> {
	List<CourseOpen> selectCourseList(CourseOpen courseOpen);
}