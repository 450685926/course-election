package com.server.edu.election.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.election.dto.ElcMedWithdrawApplyDto;
import com.server.edu.election.vo.ElcMedWithdrawApplyVo;

public interface ElcMedWithdrawApplyService {
	PageInfo<ElcMedWithdrawApplyVo> list(PageCondition<ElcMedWithdrawApplyDto> condition);
	
	int approval(List<Long> ids);

}
