package com.server.edu.exam.dto;

import com.server.edu.exam.vo.GraduateExamInfoVo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @description: 编辑排考
 * @author: bear
 * @create: 2019-11-12 16:59
 */
public class EditGraduateExam implements Serializable {
    private List<GraduateExamInfoVo> infoVoList;
    private Date examDate;
    private String examEndTime;
    private String examStartTime;
    private Long actualCalendarId;
    private String classNode;
    private Integer weekDay;
    private Integer weekNumber;
    private Integer startHour;
    private Integer endHour;
    private Integer startMinute;
    private Integer endMinute;
    private  String examInfoIds;

    public String getExamInfoIds() {
        return examInfoIds;
    }

    public void setExamInfoIds(String examInfoIds) {
        this.examInfoIds = examInfoIds;
    }

    public List<GraduateExamInfoVo> getInfoVoList() {
        return infoVoList;
    }

    public void setInfoVoList(List<GraduateExamInfoVo> infoVoList) {
        this.infoVoList = infoVoList;
    }

    public Date getExamDate() {
        return examDate;
    }

    public void setExamDate(Date examDate) {
        this.examDate = examDate;
    }

    public String getExamEndTime() {
        return examEndTime;
    }

    public void setExamEndTime(String examEndTime) {
        this.examEndTime = examEndTime;
    }

    public String getExamStartTime() {
        return examStartTime;
    }

    public void setExamStartTime(String examStartTime) {
        this.examStartTime = examStartTime;
    }

    public Long getActualCalendarId() {
        return actualCalendarId;
    }

    public void setActualCalendarId(Long actualCalendarId) {
        this.actualCalendarId = actualCalendarId;
    }

    public String getClassNode() {
        return classNode;
    }

    public void setClassNode(String classNode) {
        this.classNode = classNode;
    }

    public Integer getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(Integer weekDay) {
        this.weekDay = weekDay;
    }

    public Integer getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(Integer weekNumber) {
        this.weekNumber = weekNumber;
    }

    public Integer getStartHour() {
        return startHour;
    }

    public void setStartHour(Integer startHour) {
        this.startHour = startHour;
    }

    public Integer getEndHour() {
        return endHour;
    }

    public void setEndHour(Integer endHour) {
        this.endHour = endHour;
    }

    public Integer getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(Integer startMinute) {
        this.startMinute = startMinute;
    }

    public Integer getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(Integer endMinute) {
        this.endMinute = endMinute;
    }
}
