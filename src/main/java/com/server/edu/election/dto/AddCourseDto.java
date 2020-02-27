package com.server.edu.election.dto;

import java.util.List;

/**
 * 加课参数
 */
public class AddCourseDto {
    private Long calendarId;

    private String studentId;

    private List<Long> teachingClassId;

    // 状态码。0或者null第一次进来，1强制添加
    private Integer status;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

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
