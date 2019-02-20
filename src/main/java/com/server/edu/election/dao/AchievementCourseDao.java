package com.server.edu.election.dao;

import java.util.List;

import com.server.edu.election.entity.AchievementCourse;

import tk.mybatis.mapper.common.Mapper;

public interface AchievementCourseDao extends Mapper<AchievementCourse> {
	int batchInsert(List<AchievementCourse> list);
}