package com.server.edu.election.dao;

import java.util.List;

import com.server.edu.election.entity.ExemptionApplyGraduteCondition;

public class ExemptionApplyGraduteConditionDto extends ExemptionApplyGraduteCondition {
	private static final long serialVersionUID = 1L;
	
    /**
     * 关键字（课程代码或者课程名称）
     */
    private String keyWord;
    
    /**
     * 课程code集合
     */
    private List<String> courseCodes;  
    
	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public List<String> getCourseCodes() {
		return courseCodes;
	}

	public void setCourseCodes(List<String> courseCodes) {
		this.courseCodes = courseCodes;
	}
    
}
