package com.server.edu.election.entity;

import java.io.Serializable;

/**
 * @description: 点名册
 * @author: bear
 * @create: 2019-02-14 17:54
 */
public class RollBookList implements Serializable {
    private Long calendarId;
    private String calssCode;
    private String courseCode;
    private String courseName;
    private String label;
    private Integer selectCourseNumber;
    private Integer numberLimit;
    private String faculty;
    private String teacherName;

    public Long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }

    public String getCalssCode() {
        return calssCode;
    }

    public void setCalssCode(String calssCode) {
        this.calssCode = calssCode;
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getSelectCourseNumber() {
        return selectCourseNumber;
    }

    public void setSelectCourseNumber(Integer selectCourseNumber) {
        this.selectCourseNumber = selectCourseNumber;
    }

    public Integer getNumberLimit() {
        return numberLimit;
    }

    public void setNumberLimit(Integer numberLimit) {
        this.numberLimit = numberLimit;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
}
