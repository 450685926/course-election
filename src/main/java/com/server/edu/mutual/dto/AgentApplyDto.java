package com.server.edu.mutual.dto;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

public class AgentApplyDto {
	@NotNull
    private Long calendarId;
    @NotBlank
    private String studentId;
    @NotNull
    private Long mutualCourseId;
    
    private Integer category;
    
    private Integer mode;

	public Long getCalendarId() {
		return calendarId;
	}

	public void setCalendarId(Long calendarId) {
		this.calendarId = calendarId;
	}

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public Long getMutualCourseId() {
		return mutualCourseId;
	}

	public void setMutualCourseId(Long mutualCourseId) {
		this.mutualCourseId = mutualCourseId;
	}

	public Integer getMode() {
		return mode;
	}

	public void setMode(Integer mode) {
		this.mode = mode;
	}

	public Integer getCategory() {
		return category;
	}

	public void setCategory(Integer category) {
		this.category = category;
	}
    

}
