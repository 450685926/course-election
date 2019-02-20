package com.server.edu.election.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.election.dto.AchievementLevelDto;
import com.server.edu.election.vo.AchievementLevelVo;

public interface AchievementLevelService {
	PageInfo<AchievementLevelVo> list(PageCondition<AchievementLevelDto> condition);
	int copy(AchievementLevelDto dto);
	int add(AchievementLevelDto dto);
	AchievementLevelVo getLevel(Long id);
	int update(AchievementLevelDto dto);
	int delete(List<Long> ids);

}
