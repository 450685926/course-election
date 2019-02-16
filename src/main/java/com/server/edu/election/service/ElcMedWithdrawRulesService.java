package com.server.edu.election.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.election.entity.ElcMedWithdrawRules;
import com.server.edu.election.vo.ElcMedWithdrawRulesVo;

public interface ElcMedWithdrawRulesService {
	 PageInfo<ElcMedWithdrawRules> list(PageCondition<ElcMedWithdrawRules> condition);
	 
	 int add(ElcMedWithdrawRules elcMedWithdrawRules);
	 
	 int update(ElcMedWithdrawRules elcMedWithdrawRules);
	 
	 int delete(List<Long> ids);
	 
	 ElcMedWithdrawRulesVo getRule(Long id);

}
