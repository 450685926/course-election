package com.server.edu.mutual.dao;

import java.util.List;

import com.server.edu.mutual.dto.ElcMutualCoursesDto;
import com.server.edu.mutual.entity.ElcMutualCourses;
import com.server.edu.mutual.vo.ElcMutualCoursesVo;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

public interface ElcMutualCoursesDao extends Mapper<ElcMutualCourses>,MySqlMapper<ElcMutualCourses> {
	List<ElcMutualCoursesVo> getElcMutualCourseList(ElcMutualCoursesDto dto);
	int getElcMutualCourseCount(ElcMutualCoursesDto dto);
}