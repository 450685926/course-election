package com.server.edu.election.vo;

import com.server.edu.election.entity.ElcNoGradCouSubs;

public class ElcNoGradCouSubsVo extends ElcNoGradCouSubs {
    private static final long serialVersionUID = 1L;
    private String origsCourseName;
    private Integer origsCredits;
    private String subCourseName;
    private Integer subCredits;
    private Integer grade;
    private String faculty;
    private String profession;
    private String studentName;
    public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public Integer getGrade() {
		return grade;
	}
	public void setGrade(Integer grade) {
		this.grade = grade;
	}
	public String getFaculty() {
		return faculty;
	}
	public void setFaculty(String faculty) {
		this.faculty = faculty;
	}
	public String getProfession() {
		return profession;
	}
	public void setProfession(String profession) {
		this.profession = profession;
	}
	public String getOrigsCourseName() {
		return origsCourseName;
	}
	public void setOrigsCourseName(String origsCourseName) {
		this.origsCourseName = origsCourseName;
	}
	public Integer getOrigsCredits() {
		return origsCredits;
	}
	public void setOrigsCredits(Integer origsCredits) {
		this.origsCredits = origsCredits;
	}
	public String getSubCourseName() {
		return subCourseName;
	}
	public void setSubCourseName(String subCourseName) {
		this.subCourseName = subCourseName;
	}
	public Integer getSubCredits() {
		return subCredits;
	}
	public void setSubCredits(Integer subCredits) {
		this.subCredits = subCredits;
	}
	//原课程信息
    private String origsCourseInfo;
    //新课程信息
    private String subCourseInfo;
	public String getOrigsCourseInfo() {
		return origsCourseInfo;
	}
	public void setOrigsCourseInfo(String origsCourseInfo) {
		this.origsCourseInfo = origsCourseInfo;
	}
	public String getSubCourseInfo() {
		return subCourseInfo;
	}
	public void setSubCourseInfo(String subCourseInfo) {
		this.subCourseInfo = subCourseInfo;
	}
    
}
