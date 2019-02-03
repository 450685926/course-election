package com.server.edu.election.vo;

import java.util.List;

import com.server.edu.election.entity.ElectionParameter;
import com.server.edu.election.entity.ElectionRule;

public class ElectionRuleVo extends ElectionRule{
	private List<ElectionParameter>  list;
	//0:未选中，1选中
	private Integer checkIndex;
	
	public static final Integer enable = 1;
	
	public static final Integer unEnable = 0;
	
	public static final Integer check = 1;
	
	public static final Integer unCheck = 0;

	public List<ElectionParameter> getList() {
		return list;
	}

	public void setList(List<ElectionParameter> list) {
		this.list = list;
	}

	public Integer getCheckIndex() {
		return checkIndex;
	}

	public void setCheckIndex(Integer checkIndex) {
		this.checkIndex = checkIndex;
	}
	
	

}
