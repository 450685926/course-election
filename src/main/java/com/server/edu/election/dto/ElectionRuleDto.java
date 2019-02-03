package com.server.edu.election.dto;

import java.util.List;

import com.server.edu.election.entity.ElectionParameter;
import com.server.edu.election.entity.ElectionRule;

public class ElectionRuleDto extends ElectionRule{
	private static final long serialVersionUID = 1L;
	private List<ElectionParameter> list;

	public List<ElectionParameter> getList() {
		return list;
	}

	public void setList(List<ElectionParameter> list) {
		this.list = list;
	}
}
