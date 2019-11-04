package com.server.edu.exam.vo;

import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.exam.entity.GraduateExamInfo;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 排考信息查询
 * @author: bear
 * @create: 2019-09-06 16:29
 */
@CodeI18n
public class GraduateExamMessage implements Serializable {
    private Long calendarId;
    @Code2Text(DictTypeEnum.X_KCXZ )
    private String nature;
    private String courseCode;
    private String courseName;
    @Code2Text(DictTypeEnum.X_YX )
    private String faculty;
    @Code2Text(DictTypeEnum.X_XQ )
    private String campus;
    @Code2Text(DictTypeEnum.X_PYCC )
    private String trainingLevel;
    private String teacherStr;
    private Double credits;
    private String examTime;
    private Long roomId;
    private String roomName;
    private String teacherCode;
    private String teacherName;
    private Long examRoomId;
    private Long examInfoId;
    private Long infoRoomId;
    private Integer roomNumber;

    public Long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }

    public Integer getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(Integer roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Long getInfoRoomId() {
        return infoRoomId;
    }

    public void setInfoRoomId(Long infoRoomId) {
        this.infoRoomId = infoRoomId;
    }

    public Long getExamRoomId() {
        return examRoomId;
    }

    public void setExamRoomId(Long examRoomId) {
        this.examRoomId = examRoomId;
    }

    public Long getExamInfoId() {
        return examInfoId;
    }

    public void setExamInfoId(Long examInfoId) {
        this.examInfoId = examInfoId;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
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

    public String getTrainingLevel() {
        return trainingLevel;
    }

    public void setTrainingLevel(String trainingLevel) {
        this.trainingLevel = trainingLevel;
    }

    public Double getCredits() {
        return credits;
    }

    public void setCredits(Double credits) {
        this.credits = credits;
    }

    public String getExamTime() {
        return examTime;
    }

    public void setExamTime(String examTime) {
        this.examTime = examTime;
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

    public String getTeacherCode() {
        return teacherCode;
    }

    public void setTeacherCode(String teacherCode) {
        this.teacherCode = teacherCode;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getTeacherStr() {
        return teacherStr;
    }

    public void setTeacherStr(String teacherStr) {
        this.teacherStr = teacherStr;
    }
}
