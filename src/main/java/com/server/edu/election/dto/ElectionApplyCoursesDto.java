package com.server.edu.election.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.server.edu.election.entity.Course;

public class ElectionApplyCoursesDto extends Course {
    private static final long serialVersionUID = 1L;
    @NotNull
    private Long calendarId;
    @NotEmpty
    private List<String> courses;
    private String keyCode;
    private Integer weekNum;
    
    
	public Integer getWeekNum() {
		return weekNum;
	}
	public void setWeekNum(Integer weekNum) {
		this.weekNum = weekNum;
	}
	public String getKeyCode() {
		return keyCode;
	}
	public void setKeyCode(String keyCode) {
		this.keyCode = keyCode;
	}
	public Long getCalendarId() {
		return calendarId;
	}
	public void setCalendarId(Long calendarId) {
		this.calendarId = calendarId;
	}
	public List<String> getCourses() {
		return courses;
	}
	public void setCourses(List<String> courses) {
		this.courses = courses;
	}
    

}
