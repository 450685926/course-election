package com.server.edu.election.dto;

import javax.validation.constraints.NotNull;

import com.server.edu.election.entity.Student;

public class StudentDto extends Student {
    private static final long serialVersionUID = 1L;
    private String  courseCode;
    @NotNull
    private Long  courseId;
    
    
	public Long getCourseId() {
		return courseId;
	}
	public void setCourseId(Long courseId) {
		this.courseId = courseId;
	}
	public String getCourseCode() {
		return courseCode;
	}
	public void setCourseCode(String courseCode) {
		this.courseCode = courseCode;
	}
    
}
