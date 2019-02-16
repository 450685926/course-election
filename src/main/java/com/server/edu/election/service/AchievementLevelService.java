package com.server.edu.election.service;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.election.dto.AchievementLevelDto;
import com.server.edu.election.vo.AchievementLevelVo;

public interface AchievementLevelService {
	PageInfo<AchievementLevelVo> list(PageCondition<AchievementLevelDto> condition);

}
