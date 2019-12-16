package com.server.edu.exam.dto;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 返回是否学院通知和主键集合
 * @author: bear
 * @create: 2019-09-27 16:00
 */
public class ExamSaveTimeRebackDto implements Serializable {
    private List<Long> examInfoIds;
    private Integer notice;
    private Integer weekNumber;
    private Integer weekDay;
    private String classNode;
    private Long actualCalendarId;
    private Integer startHour;
    private Integer endHour;
    private Integer startMinute;
    private Integer endMinute;
    private String examStartTime;
    private String examEndTime;

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

    public String getExamStartTime() {
        return examStartTime;
    }

    public void setExamStartTime(String examStartTime) {
        this.examStartTime = examStartTime;
    }

    public String getExamEndTime() {
        return examEndTime;
    }

    public void setExamEndTime(String examEndTime) {
        this.examEndTime = examEndTime;
    }

    public Long getActualCalendarId() {
        return actualCalendarId;
    }

    public void setActualCalendarId(Long actualCalendarId) {
        this.actualCalendarId = actualCalendarId;
    }

    public Integer getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(Integer weekNumber) {
        this.weekNumber = weekNumber;
    }

    public Integer getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(Integer weekDay) {
        this.weekDay = weekDay;
    }

    public String getClassNode() {
        return classNode;
    }

    public void setClassNode(String classNode) {
        this.classNode = classNode;
    }

    public List<Long> getExamInfoIds() {
        return examInfoIds;
    }

    public void setExamInfoIds(List<Long> examInfoIds) {
        this.examInfoIds = examInfoIds;
    }

    public Integer getNotice() {
        return notice;
    }

    public void setNotice(Integer notice) {
        this.notice = notice;
    }
}
