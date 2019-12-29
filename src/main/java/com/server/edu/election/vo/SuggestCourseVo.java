package com.server.edu.election.vo;

import com.server.edu.election.entity.ElcCourseTake;

public class SuggestCourseVo extends ElcCourseTake{
	
	private Integer grade;
	
	private String profession;
	
	private String teachingClassCode;

	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public String getTeachingClassCode() {
		return teachingClassCode;
	}

	public void setTeachingClassCode(String teachingClassCode) {
		this.teachingClassCode = teachingClassCode;
	}
	
	

}
