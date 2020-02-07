package com.server.edu.election.dto;

import javax.validation.constraints.NotNull;

public class StuHonorDto {
	private String studentId;
    /**
     * 校历ID（学年学期）
     */
    @NotNull
    private Long calendarId;
    
    private Integer index;
    
    
	public Integer getIndex() {
		return index;
	}
	public void setIndex(Integer index) {
		this.index = index;
	}
	public String getStudentId() {
		return studentId;
	}
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	public Long getCalendarId() {
		return calendarId;
	}
	public void setCalendarId(Long calendarId) {
		this.calendarId = calendarId;
	} 
    
}
