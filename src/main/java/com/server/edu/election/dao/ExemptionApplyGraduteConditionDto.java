package com.server.edu.election.dao;

import com.server.edu.election.entity.ExemptionApplyGraduteCondition;

public class ExemptionApplyGraduteConditionDto extends ExemptionApplyGraduteCondition {
	private static final long serialVersionUID = 1L;
	
    /**
     * 关键字（课程代码或者课程名称）
     */
    private String keyWord;

	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}
    
}
