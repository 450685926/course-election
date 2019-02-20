package com.server.edu.election.dao;

import java.util.List;

import com.server.edu.election.dto.ElcAffinityCoursesDto;
import com.server.edu.election.entity.ElcAffinityCourses;
import com.server.edu.election.vo.ElcAffinityCoursesVo;

import tk.mybatis.mapper.common.Mapper;

public interface ElcAffinityCoursesDao extends Mapper<ElcAffinityCourses> {
	List<ElcAffinityCoursesVo> selectElcAffinityCourses(ElcAffinityCoursesDto dto);
	int batchInsert(List<ElcAffinityCourses> list);
}