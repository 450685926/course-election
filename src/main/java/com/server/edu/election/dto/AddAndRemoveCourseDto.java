package com.server.edu.election.dto;

import java.util.List;

/**
 * 加课退课参数
 */
public class AddAndRemoveCourseDto {
    private Long calendarId;
    private String studentId;
    private List<Long> teachingClassId;
    private Integer chooseObj;
    private String id;
    private String name;

    public Long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getChooseObj() {
        return chooseObj;
    }

    public void setChooseObj(Integer chooseObj) {
        this.chooseObj = chooseObj;
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
