package com.server.edu.mutual.dto;

import java.util.List;

import com.server.edu.mutual.entity.ElcMutualCourses;

public class ElcMutualCoursesDto extends ElcMutualCourses {

    private static final long serialVersionUID = 1L;
    
    private String courseCode;
    
    private String courseName;
    
    private String college;
    
    /** 本科生课程性质 */
    private String isElective;
    
    /** 研究生课程性质 */
    private String nature;
    
    /**
     * 课程层次
     */
    private String trainingLevel;
    
    private String courseList;
    
    private String projectId;
    private List<String> projectIds;
    
    private String studentId;
    
    private Integer mode;
    
    private String openCollege;
    
    /** 开课学院 */
    private String faculty;
    
	public String getOpenCollege() {
		return openCollege;
	}
	public void setOpenCollege(String openCollege) {
		this.openCollege = openCollege;
	}
	public Integer getMode() {
		return mode;
	}
	public void setMode(Integer mode) {
		this.mode = mode;
	}
	public String getStudentId() {
		return studentId;
	}
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	public String getCourseList() {
		return courseList;
	}
	public void setCourseList(String courseList) {
		this.courseList = courseList;
	}
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
	public String getCollege() {
		return college;
	}
	public void setCollege(String college) {
		this.college = college;
	}
	public String getNature() {
		return nature;
	}
	public void setNature(String nature) {
		this.nature = nature;
	}
	public String getTrainingLevel() {
		return trainingLevel;
	}
	public void setTrainingLevel(String trainingLevel) {
		this.trainingLevel = trainingLevel;
	}
	public List<String> getProjectIds() {
		return projectIds;
	}
	public void setProjectIds(List<String> projectIds) {
		this.projectIds = projectIds;
	}
	public String getFaculty() {
		return faculty;
	}
	public void setFaculty(String faculty) {
		this.faculty = faculty;
	}
	public String getIsElective() {
		return isElective;
	}
	public void setIsElective(String isElective) {
		this.isElective = isElective;
	}
	
}
