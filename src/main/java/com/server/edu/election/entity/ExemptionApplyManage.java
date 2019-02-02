package com.server.edu.election.entity;

import java.io.Serializable;

/**
 * @description: 免修免考申请管理
 * @author: bear
 * @create: 2019-02-02 17:03
 */
public class ExemptionApplyManage implements Serializable{
    private Long id;

    private String courseCode;

    private String courseName;

    private String studentCode;

    private String name;

    private Long calendarId;

    private Integer applyType;

    private String score;

    private Integer examineResult;

    private String auditor;

    private String exemptionType;

    private String materialIp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode == null ? null : studentCode.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }

    public Integer getApplyType() {
        return applyType;
    }

    public void setApplyType(Integer applyType) {
        this.applyType = applyType;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score == null ? null : score.trim();
    }

    public Integer getExamineResult() {
        return examineResult;
    }

    public void setExamineResult(Integer examineResult) {
        this.examineResult = examineResult;
    }

    public String getAuditor() {
        return auditor;
    }

    public void setAuditor(String auditor) {
        this.auditor = auditor == null ? null : auditor.trim();
    }

    public String getExemptionType() {
        return exemptionType;
    }

    public void setExemptionType(String exemptionType) {
        this.exemptionType = exemptionType == null ? null : exemptionType.trim();
    }

    public String getMaterialIp() {
        return materialIp;
    }

    public void setMaterialIp(String materialIp) {
        this.materialIp = materialIp == null ? null : materialIp.trim();
    }
}
