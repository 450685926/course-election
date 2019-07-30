package com.server.edu.election.dto;

import java.util.List;

/**
 * 加课参数
 */
public class AddCourseDto {
    private Long calendarId;

    private String studentId;

    private List<Long> teachingClassId;

    public Long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }

    public List<Long> getTeachingClassId() {
        return teachingClassId;
    }

    public void setTeachingClassId(List<Long> teachingClassId) {
        this.teachingClassId = teachingClassId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
}
