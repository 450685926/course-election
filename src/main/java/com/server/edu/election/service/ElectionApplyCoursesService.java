package com.server.edu.election.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.election.dto.ElectionApplyCoursesDto;
import com.server.edu.election.entity.Course;
import com.server.edu.election.vo.ElectionApplyCoursesVo;

public interface ElectionApplyCoursesService {
	PageInfo<ElectionApplyCoursesVo> applyCourseList(PageCondition<ElectionApplyCoursesDto> condition);
	PageInfo<Course> courseList(PageCondition<Course> condition);
	int addCourses(ElectionApplyCoursesDto dto);
	int deleteCourses(List<Long> ids);
}
