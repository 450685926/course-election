package com.server.edu.election.dao;

import java.util.List;

import com.server.edu.election.dto.ElectionApplyCoursesDto;
import com.server.edu.election.entity.ElectionApplyCourses;
import com.server.edu.election.vo.ElectionApplyCoursesVo;

import tk.mybatis.mapper.common.Mapper;

public interface ElectionApplyCoursesDao extends Mapper<ElectionApplyCourses> {
	List<ElectionApplyCoursesVo> selectApplyCourse(ElectionApplyCoursesDto dto);
}