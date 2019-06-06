package com.server.edu.election.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.election.entity.ElcNoGradCouSubs;
import com.server.edu.election.vo.ElcNoGradCouSubsVo;

public interface ElcNoGradCouSubsService {
	PageInfo<ElcNoGradCouSubsVo> page(PageCondition<ElcNoGradCouSubs> condition);
	int add(ElcNoGradCouSubs elcNoGradCouSubs);
	int update(ElcNoGradCouSubs elcNoGradCouSubs);
	int delete(List<Long> ids);
}
