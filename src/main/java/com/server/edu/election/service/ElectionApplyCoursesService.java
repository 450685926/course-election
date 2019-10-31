package com.server.edu.election.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dto.CourseDto;
import com.server.edu.election.dto.ElectionApplyCoursesDto;
import com.server.edu.election.entity.Course;
import com.server.edu.election.vo.ElectionApplyCoursesVo;

public interface ElectionApplyCoursesService {
	PageInfo<ElectionApplyCoursesVo> applyCourseList(PageCondition<ElectionApplyCoursesDto> condition);
	PageInfo<Course> courseList(PageCondition<CourseDto> condition);
	int addCourses(ElectionApplyCoursesDto dto);
	int deleteCourses(List<Long> ids);
	PageResult<Course> courseList1(PageCondition<CourseDto> condition);
}
