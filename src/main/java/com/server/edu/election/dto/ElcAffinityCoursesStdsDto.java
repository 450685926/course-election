package com.server.edu.election.dto;

import com.server.edu.election.entity.ElcAffinityCoursesStds;

public class ElcAffinityCoursesStdsDto extends ElcAffinityCoursesStds{
    private static final long serialVersionUID = 1L;
    
    private String studentCode;
    
    private String studentName;
    
    private String courseCode;
    
	public String getCourseCode() {
		return courseCode;
	}

	public void setCourseCode(String courseCode) {
		this.courseCode = courseCode;
	}

	public String getStudentCode() {
		return studentCode;
	}

	public void setStudentCode(String studentCode) {
		this.studentCode = studentCode;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
    
    

}
