package com.server.edu.election.vo;

import java.util.List;

import com.server.edu.election.entity.ElectionTpl;

public class ElectionTplVo extends ElectionTpl{
	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    private List<ElectionRuleVo> list;
	
	private String rules;
	

	public List<ElectionRuleVo> getList() {
		return list;
	}

	public void setList(List<ElectionRuleVo> list) {
		this.list = list;
	}

	public String getRules() {
		return rules;
	}

	public void setRules(String rules) {
		this.rules = rules;
	}
	
	

}
