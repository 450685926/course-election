package com.server.edu.election.dto;

import com.server.edu.election.entity.ElcAffinityCourses;

public class ElcAffinityCoursesDto extends ElcAffinityCourses {
    private static final long serialVersionUID = 1L;
    
    private String courseCode;

    private String courseName;

    private String courseNameEn;
    
	public String getCourseCode() {
		return courseCode;
	}

	public void setCourseCode(String courseCode) {
		this.courseCode = courseCode;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getCourseNameEn() {
		return courseNameEn;
	}

	public void setCourseNameEn(String courseNameEn) {
		this.courseNameEn = courseNameEn;
	}
    
    


}
