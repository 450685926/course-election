package com.server.edu.election.dto;

import com.server.edu.election.entity.Student;

public class StudentDto extends Student {
    private static final long serialVersionUID = 1L;
    private String  courseCode;
	public String getCourseCode() {
		return courseCode;
	}
	public void setCourseCode(String courseCode) {
		this.courseCode = courseCode;
	}
    
}
