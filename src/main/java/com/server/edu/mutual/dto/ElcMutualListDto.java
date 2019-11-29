package com.server.edu.mutual.dto;

import java.util.List;
import javax.validation.constraints.NotNull;

public class ElcMutualListDto{
	
    private String projectId;
	
	private List<String> projectIds;
    /**
     * 学年学期
     */
	@NotNull
    private Long calendarId;
    /**
     * 年级
     */
    private Integer grade;
    
    /**
     * 学生学院（学生所在行政学院）
     */
    private String college;
    
    /**
     * 开课学院
     */
    private String openCollege;
    
    /**
     * 专业
     */
    private String profession;
    
    /**
     * 培养层次(专科   本科   硕士   博士    其他    预科)
     */
    private String stuTrainingLevel;

    /**
     * 课程层次(专科   本科   硕士   博士    其他    预科)
     */
    private String courseTrainingLevel;
    
    /**
     * 培养类别
     */
    private String trainingCategory;

    /**
     * 学生类别
     */
    private String studentCategory;
	
    /**
     * 学位类型
     */
    private String degreeType;
    
    /**
     * 学习形式
     */
    private String formLearning;
	
    /**
     * 课程编号
     */
    private String courseCode;
    
    /**
     * 课程名称
     */
    private String courseName;
    
    /**
     * 研究生课程性质
     */
    private String nature;
    
    /**
     * 本科生课程性质
     */
    private String isElective;
    
    /**
     * 修读类别(1正常修读,2重修,3免修不免考,4免修)
     */
    private Integer courseTakeType;
    
    /**
     * 关键字（学号或者姓名）
     */
    private String keyWords;
    
    /**
     * 互选类别（1-本科生互选；2-研究生互选；3-本科生跨学科）
     */
    @NotNull
    private Integer mode;
    
    /**
     * 本研互选开关类别：1:本研互选  2：跨学科互选
     */
    private Integer category;
    
    private Integer byType;
    
    private Integer inType;
    
	public Long getCalendarId() {
		return calendarId;
	}

	public void setCalendarId(Long calendarId) {
		this.calendarId = calendarId;
	}

	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	public String getCollege() {
		return college;
	}

	public void setCollege(String college) {
		this.college = college;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public String getStuTrainingLevel() {
		return stuTrainingLevel;
	}

	public void setStuTrainingLevel(String stuTrainingLevel) {
		this.stuTrainingLevel = stuTrainingLevel;
	}

	public String getTrainingCategory() {
		return trainingCategory;
	}

	public void setTrainingCategory(String trainingCategory) {
		this.trainingCategory = trainingCategory;
	}

	public String getDegreeType() {
		return degreeType;
	}

	public void setDegreeType(String degreeType) {
		this.degreeType = degreeType;
	}

	public String getFormLearning() {
		return formLearning;
	}

	public void setFormLearning(String formLearning) {
		this.formLearning = formLearning;
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

	public String getNature() {
		return nature;
	}

	public void setNature(String nature) {
		this.nature = nature;
	}

	public String getIsElective() {
		return isElective;
	}

	public void setIsElective(String isElective) {
		this.isElective = isElective;
	}

	public Integer getCourseTakeType() {
		return courseTakeType;
	}

	public void setCourseTakeType(Integer courseTakeType) {
		this.courseTakeType = courseTakeType;
	}

	public String getKeyWords() {
		return keyWords;
	}

	public void setKeyWords(String keyWords) {
		this.keyWords = keyWords;
	}

	public Integer getMode() {
		return mode;
	}

	public void setMode(Integer mode) {
		this.mode = mode;
	}

	public Integer getCategory() {
		return category;
	}

	public void setCategory(Integer category) {
		this.category = category;
	}

	public Integer getByType() {
		return byType;
	}

	public void setByType(Integer byType) {
		this.byType = byType;
	}

	public Integer getInType() {
		return inType;
	}

	public void setInType(Integer inType) {
		this.inType = inType;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getOpenCollege() {
		return openCollege;
	}

	public void setOpenCollege(String openCollege) {
		this.openCollege = openCollege;
	}

	public List<String> getProjectIds() {
		return projectIds;
	}

	public void setProjectIds(List<String> projectIds) {
		this.projectIds = projectIds;
	}

	public String getStudentCategory() {
		return studentCategory;
	}

	public void setStudentCategory(String studentCategory) {
		this.studentCategory = studentCategory;
	}

	public String getCourseTrainingLevel() {
		return courseTrainingLevel;
	}

	public void setCourseTrainingLevel(String courseTrainingLevel) {
		this.courseTrainingLevel = courseTrainingLevel;
	}
    
	
}
