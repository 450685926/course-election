package com.server.edu.election.dto;

import java.util.List;

import com.server.edu.election.entity.ElectionTpl;

public class ElectionTplDto extends ElectionTpl{
	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    private List<Long> ruleIds;

	public List<Long> getRuleIds() {
		return ruleIds;
	}

	public void setRuleIds(List<Long> ruleIds) {
		this.ruleIds = ruleIds;
	}
	

}
