package com.server.edu.election.dto;

import com.server.edu.election.entity.Student;

/**
 * @description: 学生选课名单
 * @author: bear
 * @create: 2019-02-19 17:11
 */
public class StudentSelectCourseList extends Student{
    private Long calendarId;
    private String calendarName;
    private String courseCode;
    private String courseName;
    private String classCode;
    private Integer isRebuildCourse;
    private String nature;

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public Long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }

    public String getCalendarName() {
        return calendarName;
    }

    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
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

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public Integer getIsRebuildCourse() {
        return isRebuildCourse;
    }

    public void setIsRebuildCourse(Integer isRebuildCourse) {
        this.isRebuildCourse = isRebuildCourse;
    }
}
