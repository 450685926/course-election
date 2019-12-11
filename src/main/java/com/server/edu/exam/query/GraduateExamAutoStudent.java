package com.server.edu.exam.query;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 自动分配考场
 * @author: bear
 * @create: 2019-10-08 15:34
 */
public class GraduateExamAutoStudent implements Serializable{
    /**期末考试 1 补缓考 2*/
    private Integer examType;
    private List<Long> examInfoIds;
    private List<Long> examRoomIds;
    private Long calendarId;

    private List<Long> allCourseInfoIds;

    public List<Long> getAllCourseInfoIds() {
        return allCourseInfoIds;
    }

    public void setAllCourseInfoIds(List<Long> allCourseInfoIds) {
        this.allCourseInfoIds = allCourseInfoIds;
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

    public List<Long> getExamInfoIds() {
        return examInfoIds;
    }

    public void setExamInfoIds(List<Long> examInfoIds) {
        this.examInfoIds = examInfoIds;
    }

    public List<Long> getExamRoomIds() {
        return examRoomIds;
    }

    public void setExamRoomIds(List<Long> examRoomIds) {
        this.examRoomIds = examRoomIds;
    }
}
