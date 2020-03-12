package com.server.edu.mutual.dto;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import java.util.List;

public class AgentApplyDto {
	@NotNull
    private Long calendarId;
//    @NotBlank
    private String studentId;
//    @NotNull
    private Long mutualCourseId;
    
    private Integer category;
    
    private Integer mode;

    //学号列表
    private List<String> studentIdList;

    //申请课程id列表
    private List<Long> mutualCourseIdList;

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

	public List<String> getStudentIdList() {
		return studentIdList;
	}

	public void setStudentIdList(List<String> studentIdList) {
		this.studentIdList = studentIdList;
	}

	public List<Long> getMutualCourseIdList() {
		return mutualCourseIdList;
	}

	public void setMutualCourseIdList(List<Long> mutualCourseIdList) {
		this.mutualCourseIdList = mutualCourseIdList;
	}

	public AgentApplyDto(){
		//无参构造
	}

	public AgentApplyDto(Long calendarId, String studentId, Long mutualCourseId, Integer category, Integer mode) {
		setCalendarId(calendarId);
		setStudentId(studentId);
		setMutualCourseId(mutualCourseId);
		setCategory(category);
		setMode(mode);
	}

	@Override
	public String toString() {
		return "[calendarId = " + this.getCalendarId() + ";studentId = "
				+ this.getStudentId() + ";mutualCourseId = "
				+ this.getMutualCourseId() + ";category = "
				+ this.getCategory() + ";mode = "
				+ this.getMode() + "]";
	}

}
