package com.server.edu.election.entity;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;

import java.io.Serializable;
import java.util.Date;

/**
 * @description: 免修免考入学成绩申请规则
 * @author: bear
 * @create: 2019-02-01 15:39
 */

@CodeI18n
public class ExemptionCourseRule implements Serializable{
    private Long id;

    private Long calendarId;

    @Code2Text(transformer = "X_PYCC")
    private String trainingLevel;

    @Code2Text(transformer = "X_XXXS")
    private String formLearning;

    @Code2Text(transformer="X_YX")
    private String faculty;

    private Date createdAt;

    @Code2Text(transformer="X_PYLB")
    private String trainingCategory;

    private Integer applyType;

    private Integer grade;

    private Integer minimumPassScore;

    private Integer pencent;

    private String studentTypeAuditor;

    private Integer number;

    private String remark;

    private String auditor;

    private String applyDescription;

    private String courseCode;

    private String courseName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }

    public String getTrainingLevel() {
        return trainingLevel;
    }

    public void setTrainingLevel(String trainingLevel) {
        this.trainingLevel = trainingLevel == null ? null : trainingLevel.trim();
    }

    public String getFormLearning() {
        return formLearning;
    }

    public void setFormLearning(String formLearning) {
        this.formLearning = formLearning == null ? null : formLearning.trim();
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty == null ? null : faculty.trim();
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getTrainingCategory() {
        return trainingCategory;
    }

    public void setTrainingCategory(String trainingCategory) {
        this.trainingCategory = trainingCategory == null ? null : trainingCategory.trim();
    }

    public Integer getApplyType() {
        return applyType;
    }

    public void setApplyType(Integer applyType) {
        this.applyType = applyType;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public Integer getMinimumPassScore() {
        return minimumPassScore;
    }

    public void setMinimumPassScore(Integer minimumPassScore) {
        this.minimumPassScore = minimumPassScore;
    }

    public Integer getPencent() {
        return pencent;
    }

    public void setPencent(Integer pencent) {
        this.pencent = pencent;
    }

    public String getStudentTypeAuditor() {
        return studentTypeAuditor;
    }

    public void setStudentTypeAuditor(String studentTypeAuditor) {
        this.studentTypeAuditor = studentTypeAuditor == null ? null : studentTypeAuditor.trim();
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getAuditor() {
        return auditor;
    }

    public void setAuditor(String auditor) {
        this.auditor = auditor == null ? null : auditor.trim();
    }

    public String getApplyDescription() {
        return applyDescription;
    }

    public void setApplyDescription(String applyDescription) {
        this.applyDescription = applyDescription == null ? null : applyDescription.trim();
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode == null ? null : courseCode.trim();
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName == null ? null : courseName.trim();
    }
}
