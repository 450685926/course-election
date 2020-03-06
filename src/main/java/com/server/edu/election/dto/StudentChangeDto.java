package com.server.edu.election.dto;

import java.util.List;

public class StudentChangeDto {
	private String studentId;
	
	private Integer year;
	
	private Integer term;
	
	private Integer xjydType;
	
	private List<Integer> xjydTypes;

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getTerm() {
		return term;
	}

	public void setTerm(Integer term) {
		this.term = term;
	}

	public Integer getXjydType() {
		return xjydType;
	}

	public void setXjydType(Integer xjydType) {
		this.xjydType = xjydType;
	}

	public List<Integer> getXjydTypes() {
		return xjydTypes;
	}

	public void setXjydTypes(List<Integer> xjydTypes) {
		this.xjydTypes = xjydTypes;
	}
	
	

}
