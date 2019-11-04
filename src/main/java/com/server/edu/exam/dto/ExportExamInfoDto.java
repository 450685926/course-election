package com.server.edu.exam.dto;

/**
 * @author daichang
 * @description 导出学生名单传递参数用
 * @date 2019/10/8
 */
public class ExportExamInfoDto {
    private Long calendarId;
    private Long examRoomId;

    private String calendarName;
    private String courseCode;
    private String courseName;
    private String examTime;
    private String examRoom;

    public Long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }

    public Long getExamRoomId() {
        return examRoomId;
    }

    public void setExamRoomId(Long examRoomId) {
        this.examRoomId = examRoomId;
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

    public String getExamTime() {
        return examTime;
    }

    public void setExamTime(String examTime) {
        this.examTime = examTime;
    }

    public String getExamRoom() {
        return examRoom;
    }

    public void setExamRoom(String examRoom) {
        this.examRoom = examRoom;
    }

    public String getCalendarName() {
        return calendarName;
    }

    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }
}
