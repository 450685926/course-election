package com.server.edu.exam.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * @description: 我的考试
 * @author: bear
 * @create: 2019-09-05 16:59
 */
public class MyGraduateExam implements Serializable {
    private Long calendarId;
    //排考类型 1 期末考试 2 补缓考
    private Integer examType;
    private String courseCode;
    private String courseName;
    private Long roomId;
    private String roomName;
    private String remark;
    private String studentCode;
    //考试情况 1 正常 2 缓考 3 补考 4 重修 5旷考
    private Integer examSituation;
    private String examTime;
    private Long examInfoId;
    private Long teachingClassId;

    public Long getTeachingClassId() {
        return teachingClassId;
    }

    public void setTeachingClassId(Long teachingClassId) {
        this.teachingClassId = teachingClassId;
    }

    public Long getExamInfoId() {
        return examInfoId;
    }

    public void setExamInfoId(Long examInfoId) {
        this.examInfoId = examInfoId;
    }

    public String getExamTime() {
        return examTime;
    }

    public void setExamTime(String examTime) {
        this.examTime = examTime;
    }

    public Integer getExamSituation() {
        return examSituation;
    }

    public void setExamSituation(Integer examSituation) {
        this.examSituation = examSituation;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public Long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }

    public Integer getExamType() {
        return examType;
    }

    public void setExamType(Integer examType) {
        this.examType = examType;
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

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
