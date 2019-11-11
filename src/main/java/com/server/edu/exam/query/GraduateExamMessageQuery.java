package com.server.edu.exam.query;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @description: 排考信息查询
 * @author: bear
 * @create: 2019-10-15 14:47
 */
public class GraduateExamMessageQuery implements Serializable {
    private Long calendarId;
    private Integer examType;
    private String nature;
    private String faculty;
    private String campus;
    private String trainingLevel;
    private Date examStartTime;
    private Date examEndTime;
    private Long roomId;
    private String keyword;
    private String teacherCode;
    private List<String> facultys;
    private String projId;

    private List<Long> examInfoIds;

    public List<Long> getExamInfoIds() {
        return examInfoIds;
    }

    public void setExamInfoIds(List<Long> examInfoIds) {
        this.examInfoIds = examInfoIds;
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

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getTrainingLevel() {
        return trainingLevel;
    }

    public void setTrainingLevel(String trainingLevel) {
        this.trainingLevel = trainingLevel;
    }

    public Date getExamStartTime() {
        return examStartTime;
    }

    public void setExamStartTime(Date examStartTime) {
        this.examStartTime = examStartTime;
    }

    public Date getExamEndTime() {
        return examEndTime;
    }

    public void setExamEndTime(Date examEndTime) {
        this.examEndTime = examEndTime;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getTeacherCode() {
        return teacherCode;
    }

    public void setTeacherCode(String teacherCode) {
        this.teacherCode = teacherCode;
    }

    public List<String> getFacultys() {
        return facultys;
    }

    public void setFacultys(List<String> facultys) {
        this.facultys = facultys;
    }

    public String getProjId() {
        return projId;
    }

    public void setProjId(String projId) {
        this.projId = projId;
    }
}
