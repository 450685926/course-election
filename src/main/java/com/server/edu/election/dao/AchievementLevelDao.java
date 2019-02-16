package com.server.edu.election.dao;

import java.util.List;

import com.server.edu.election.dto.AchievementLevelDto;
import com.server.edu.election.entity.AchievementLevel;
import com.server.edu.election.vo.AchievementLevelVo;

import tk.mybatis.mapper.common.Mapper;

public interface AchievementLevelDao extends Mapper<AchievementLevel> {
	List<AchievementLevelVo> selectAchievementLevels(AchievementLevelDto dto);
}