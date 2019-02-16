package com.server.edu.election.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.election.dao.AchievementLevelDao;
import com.server.edu.election.dto.AchievementLevelDto;
import com.server.edu.election.service.AchievementLevelService;
import com.server.edu.election.vo.AchievementLevelVo;
@Service
public class AchievementLevelServiceImpl implements AchievementLevelService {
	@Autowired
	private AchievementLevelDao achievementLevelDao;
	@Override
	public PageInfo<AchievementLevelVo> list(PageCondition<AchievementLevelDto> condition){
		PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
		List<AchievementLevelVo> list = achievementLevelDao.selectAchievementLevels(condition.getCondition());
		PageInfo<AchievementLevelVo> pageInfo =new PageInfo<>(list);
		return pageInfo;
	}

}
