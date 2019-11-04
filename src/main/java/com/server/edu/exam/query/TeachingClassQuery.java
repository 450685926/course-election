package com.server.edu.exam.query;

/**
 * @description: 通过课程查询教学班
 * @author: bear
 * @create: 2019-09-09 10:20
 */
public class TeachingClassQuery {
    private Long calendarId;
    private String courseCode;
    private Long roomId;

    public Long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }
}
