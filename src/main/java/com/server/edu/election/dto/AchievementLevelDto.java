package com.server.edu.election.dto;

import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

import com.server.edu.election.entity.AchievementLevel;

public class AchievementLevelDto extends AchievementLevel {
    private static final long serialVersionUID = 1L;
    @NotEmpty
    private List<String> courseCodes;
	public List<String> getCourseCodes() {
		return courseCodes;
	}
	public void setCourseCodes(List<String> courseCodes) {
		this.courseCodes = courseCodes;
	}
    
}
