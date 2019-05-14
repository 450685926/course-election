package com.server.edu.election.entity;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "elc_loser_stds_t")
public class ElcLoserStds implements Serializable {
    /**
     * 主键（自增）
     */
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 校历ID（学年学期）
     */
    @Column(name = "CALENDAR_ID_")
    private Long calendarId;

    /**
     * 学号
     */
    @Column(name = "STUDENT_ID_")
    private String studentId;

    /**
     * 未通过学分
     */
    @Column(name = "UNPASSED_CREDITS_")
    private Double unpassedCredits;

    private static final long serialVersionUID = 1L;

    /**
     * 获取主键（自增）
     *
     * @return ID_ - 主键（自增）
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置主键（自增）
     *
     * @param id 主键（自增）
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取校历ID（学年学期）
     *
     * @return CALENDAR_ID_ - 校历ID（学年学期）
     */
    public Long getCalendarId() {
        return calendarId;
    }

    /**
     * 设置校历ID（学年学期）
     *
     * @param calendarId 校历ID（学年学期）
     */
    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }

    /**
     * 获取学号
     *
     * @return STUDENT_ID_ - 学号
     */
    public String getStudentId() {
        return studentId;
    }

    /**
     * 设置学号
     *
     * @param studentId 学号
     */
    public void setStudentId(String studentId) {
        this.studentId = studentId == null ? null : studentId.trim();
    }

    /**
     * 获取未通过学分
     *
     * @return UNPASSED_CREDITS_ - 未通过学分
     */
    public Double getUnpassedCredits() {
        return unpassedCredits;
    }

    /**
     * 设置未通过学分
     *
     * @param unpassedCredits 未通过学分
     */
    public void setUnpassedCredits(Double unpassedCredits) {
        this.unpassedCredits = unpassedCredits;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", calendarId=").append(calendarId);
        sb.append(", studentId=").append(studentId);
        sb.append(", unpassedCredits=").append(unpassedCredits);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}