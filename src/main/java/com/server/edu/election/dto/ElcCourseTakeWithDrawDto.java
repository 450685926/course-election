package com.server.edu.election.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.server.edu.common.validator.AddGroup;
import com.server.edu.common.validator.DelGroup;

/**
 * 研究生退课DTO
 * 
 * 
 */
public class ElcCourseTakeWithDrawDto
{
    @NotNull(groups = {AddGroup.class, DelGroup.class})
    private Long calendarId;
    
    /**
     * 学号
     */
    @NotNull(groups = {AddGroup.class})
    private List<String> students;
    
    /**
     * 教学班ID
     */
    @NotEmpty(groups = {AddGroup.class})
    private Long teachingClassId;
    
    /**
     * 教学班ID
     */
    private String courseCode;

	public Long getCalendarId() {
		return calendarId;
	}

	public void setCalendarId(Long calendarId) {
		this.calendarId = calendarId;
	}

	public List<String> getStudents() {
		return students;
	}

	public void setStudents(List<String> students) {
		this.students = students;
	}

	public Long getTeachingClassId() {
		return teachingClassId;
	}

	public void setTeachingClassId(Long teachingClassId) {
		this.teachingClassId = teachingClassId;
	}

	public String getCourseCode() {
		return courseCode;
	}

	public void setCourseCode(String courseCode) {
		this.courseCode = courseCode;
	}

}
