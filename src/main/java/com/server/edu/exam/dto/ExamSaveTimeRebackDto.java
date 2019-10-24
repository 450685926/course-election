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
