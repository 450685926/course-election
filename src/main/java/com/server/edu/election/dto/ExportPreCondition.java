package com.server.edu.election.dto;

/**
 * @description:
 * @author: bear
 * @create: 2019-05-08 18:40
 */
public class ExportPreCondition  {
    private Long calendarId;
    private String calendarName;
    private String classCode;
    private String className;
    private String courseCode;
    private String courseName;
    private String faculty;
    private Integer number;
    private String teacherName;
    private Long teachingClassId;
    private String teachingTimeAndRoom;

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

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
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

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public Long getTeachingClassId() {
        return teachingClassId;
    }

    public void setTeachingClassId(Long teachingClassId) {
        this.teachingClassId = teachingClassId;
    }

    public String getTeachingTimeAndRoom() {
        return teachingTimeAndRoom;
    }

    public void setTeachingTimeAndRoom(String teachingTimeAndRoom) {
        this.teachingTimeAndRoom = teachingTimeAndRoom;
    }
}
