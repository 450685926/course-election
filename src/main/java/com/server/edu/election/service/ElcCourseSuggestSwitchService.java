package com.server.edu.election.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.election.dto.CourseOpenDto;
import com.server.edu.election.vo.CourseOpenVo;

public interface ElcCourseSuggestSwitchService {
	PageInfo<CourseOpenVo>  page(PageCondition<CourseOpenDto> condition);
	int start(List<String> courses);
	int stop(List<String> courses);
}
