package com.server.edu.election.dao;

import java.util.List;

import com.server.edu.election.entity.ElcAffinityCoursesStds;

import tk.mybatis.mapper.common.Mapper;

public interface ElcAffinityCoursesStdsDao extends Mapper<ElcAffinityCoursesStds> {
	int batchInsert(List<ElcAffinityCoursesStds> list);
}