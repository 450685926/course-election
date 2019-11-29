package com.server.edu.exam.dto;

import java.io.Serializable;

/**
 * @description: 补考成绩
 * @author: bear
 * @create: 2019-10-16 16:37
 */
public class GraduateExamScore implements Serializable {
    private Long calendarId;
    private String studentCode;
    private Long teachingClassId;
    private String courseCode;
    private Integer isPass;

    public Integer getIsPass() {
        return isPass;
    }

    public void setIsPass(Integer isPass) {
        this.isPass = isPass;
    }

    public Long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public Long getTeachingClassId() {
        return teachingClassId;
    }

    public void setTeachingClassId(Long teachingClassId) {
        this.teachingClassId = teachingClassId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }
}
