package com.server.edu.election.dto;

/**
 * @description: 重修缴费查询条件
 * @author: bear
 * @create: 2019-02-13 17:10
 */
public class RebuildCoursePaymentCondition {
    private Long calendarId;
    private String studentCode;
    private String classCode;//教学班Code
    private String studentName;
    private Integer grade;
    private String trainingLevel;
    private String  studentCategory;
    private String  faculty;
    private String profession;
    private String  researchDirection;
    private String openFaculty;//开课院系
    private String label;//课程类别
    private Long courseId;
    private String code;
    private String name;
    private Integer mode;
    private String deptId;

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

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

    public String getTrainingLevel() {
        return trainingLevel;
    }

    public void setTrainingLevel(String trainingLevel) {
        this.trainingLevel = trainingLevel;
    }

    public String getStudentCategory() {
        return studentCategory;
    }

    public void setStudentCategory(String studentCategory) {
        this.studentCategory = studentCategory;
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

    public String getResearchDirection() {
        return researchDirection;
    }

    public void setResearchDirection(String researchDirection) {
        this.researchDirection = researchDirection;
    }

    public String getOpenFaculty() {
        return openFaculty;
    }

    public void setOpenFaculty(String openFaculty) {
        this.openFaculty = openFaculty;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }
}
