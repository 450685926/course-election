package com.server.edu.election.dao;

import java.util.List;

import com.server.edu.election.dto.CourseOpenDto;
import com.server.edu.election.entity.CourseOpen;
import com.server.edu.election.vo.CourseOpenVo;

import tk.mybatis.mapper.common.Mapper;

public interface CourseOpenDao extends Mapper<CourseOpen> {
	List<CourseOpenVo> selectCourseList(CourseOpen courseOpen);
	List<CourseOpenVo> selectCourseSuggestSwitch(CourseOpenDto dto);
	String selectFaculty(String courseCode);
}