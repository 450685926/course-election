package com.server.edu.exam.query;

import java.util.Date;
import java.util.List;

/**
 * @description: 应考学生管理查询
 * @author: bear
 * @create: 2019-09-16 09:55
 */
public class GraduateExamStudentQuery {
    private Long calendarId;
    /**
     * 排考类型 1 期末考试 2 补缓考
     */
    private Integer examType;
    private String campus;
    private String roomId;
    private Date examStartTime;
    private Date examEndTime;
    /**
     *考试情况 1 正常 2 缓考 3 补考 4 重修 5旷考
     */
    private Integer examSituation;
    private String keyword;
    private String projId;

    /**
     * 导出勾选记录，学生表主键ID
     * */
    private List<Long> examStudentIds;

    public List<Long> getExamStudentIds() {
        return examStudentIds;
    }

    public void setExamStudentIds(List<Long> examStudentIds) {
        this.examStudentIds = examStudentIds;
    }

    public String getProjId() {
        return projId;
    }

    public void setProjId(String projId) {
        this.projId = projId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
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

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
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

    public Integer getExamSituation() {
        return examSituation;
    }

    public void setExamSituation(Integer examSituation) {
        this.examSituation = examSituation;
    }
}
