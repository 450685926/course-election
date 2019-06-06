package com.server.edu.election.dto;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;

import java.io.Serializable;

/**
 * @description: 未选课学生
 * @author: bear
 * @create: 2019-05-22 17:14
 */
@CodeI18n
public class NoSelectCourseStdsDto implements Serializable{
    private Long calendarId;
    private Integer grade;
    @Code2Text(transformer = "X_YX")
    private String faculty;
    @Code2Text(transformer = "G_ZY")
    private String major;
    private String studentCategory;
    private String studentCode;
    private String studentName;
    private String stdStatusChanges;
    private String noSelectReason;
    private String deptId;
    private String keyword;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

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

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getStudentCategory() {
        return studentCategory;
    }

    public void setStudentCategory(String studentCategory) {
        this.studentCategory = studentCategory;
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

    public String getStdStatusChanges() {
        return stdStatusChanges;
    }

    public void setStdStatusChanges(String stdStatusChanges) {
        this.stdStatusChanges = stdStatusChanges;
    }

    public String getNoSelectReason() {
        return noSelectReason;
    }

    public void setNoSelectReason(String noSelectReason) {
        this.noSelectReason = noSelectReason;
    }
}
