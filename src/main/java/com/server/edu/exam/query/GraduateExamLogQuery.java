package com.server.edu.exam.query;

import java.util.Date;
import java.util.List;

/**
 * @description: 排考日志查询
 * @author: bear
 * @create: 2019-09-25 18:53
 */
public class GraduateExamLogQuery {
    private Long calendarId;
    private String campus;
    private Long roomId;
    private Date examStartTime;
    private Date examEndTime;
    private Integer examType;
    private String operator;
    private Date operatorStratTime;
    private Date operatorEndTime;
    private String keyword;
    private String projId;

    private List<Long> ids;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public String getProjId() {
        return projId;
    }

    public void setProjId(String projId) {
        this.projId = projId;
    }

    public Long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
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

    public Integer getExamType() {
        return examType;
    }

    public void setExamType(Integer examType) {
        this.examType = examType;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Date getOperatorStratTime() {
        return operatorStratTime;
    }

    public void setOperatorStratTime(Date operatorStratTime) {
        this.operatorStratTime = operatorStratTime;
    }

    public Date getOperatorEndTime() {
        return operatorEndTime;
    }

    public void setOperatorEndTime(Date operatorEndTime) {
        this.operatorEndTime = operatorEndTime;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
