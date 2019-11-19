package com.server.edu.election.entity;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * 教学班转移实体
 */
public class TeachingClassChange {
	@NotEmpty
	private List<String> studentIds;
	
    /**
     * 旧教学班id
     */
	@NotNull
	private Long oldTeachingClassId;
	
    /**
     * 新教学班id
     */
	@NotNull
	private Long newTeachingClassId;
	
	@NotNull
	private Long calendarId;
	
	public Long getCalendarId() {
		return calendarId;
	}

	public void setCalendarId(Long calendarId) {
		this.calendarId = calendarId;
	}
	
	public List<String> getStudentIds() {
		return studentIds;
	}

	public void setStudentIds(List<String> studentIds) {
		this.studentIds = studentIds;
	}

	public Long getOldTeachingClassId() {
		return oldTeachingClassId;
	}

	public void setOldTeachingClassId(Long oldTeachingClassId) {
		this.oldTeachingClassId = oldTeachingClassId;
	}

	public Long getNewTeachingClassId() {
		return newTeachingClassId;
	}

	public void setNewTeachingClassId(Long newTeachingClassId) {
		this.newTeachingClassId = newTeachingClassId;
	}
	
	

}
