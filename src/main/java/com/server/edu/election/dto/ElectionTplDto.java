package com.server.edu.election.dto;

import java.util.List;

import com.server.edu.election.entity.ElectionTpl;

public class ElectionTplDto extends ElectionTpl{
	private List<Long> ruleIds;

	public List<Long> getRuleIds() {
		return ruleIds;
	}

	public void setRuleIds(List<Long> ruleIds) {
		this.ruleIds = ruleIds;
	}
	

}
