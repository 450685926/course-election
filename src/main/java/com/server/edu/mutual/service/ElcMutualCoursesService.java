package com.server.edu.mutual.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.mutual.dto.ElcMutualCoursesDto;
import com.server.edu.mutual.vo.ElcMutualCoursesVo;

public interface ElcMutualCoursesService {
	PageInfo<ElcMutualCoursesVo> getElcMutualCourseList(PageCondition<ElcMutualCoursesDto> dto);
	
	int add(Long calendarId,String courseList,Integer mode);
	
	int batchAdd(Long calendarId,String college,Integer mode);
	
	int addAll(Long calendarId,Integer mode);
	
	int delete(List<Long> ids);
	
	int deleteAll(Long calendarId,Integer mode);
	
	int getElcMutualCourseCount(Long calendarId,Integer mode);
	
}
