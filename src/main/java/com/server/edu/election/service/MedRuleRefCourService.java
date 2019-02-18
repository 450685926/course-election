package com.server.edu.election.service;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.election.dto.ElcMedWithdrawRuleRefCourDto;
import com.server.edu.election.vo.ElcMedWithdrawRuleRefCourVo;

public interface MedRuleRefCourService {
	PageInfo<ElcMedWithdrawRuleRefCourVo> list(PageCondition<ElcMedWithdrawRuleRefCourDto> condition);
	int add(ElcMedWithdrawRuleRefCourDto elcMedWithdrawRuleRefCourDto);
	int addAll(ElcMedWithdrawRuleRefCourDto dto);
}
