package com.server.edu.election.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Table(name = "elc_number_set_t")
public class ElcNumberSet implements Serializable {
    /**
     * 主键ID
     */
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "CALENDAR_ID_")
    @NotNull
    private Long calendarId;

    /**
     * 状态
     */
    @Column(name = "STATUS_")
    @NotNull
    private Integer status;

    /**
     * 释放时间
     */
    @Column(name = "FIRST_TIME_")
    @NotNull
    private Date firstTime;

    /**
     * 释放时间
     */
    @Column(name = "SECOND_TIME_")
    @NotNull
    private Date secondTime;

    private static final long serialVersionUID = 1L;

    /**
     * 获取主键ID
     *
     * @return ID_ - 主键ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置主键ID
     *
     * @param id 主键ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return CALENDAR_ID_
     */
    public Long getCalendarId() {
        return calendarId;
    }

    /**
     * @param calendarId
     */
    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }

    /**
     * 获取状态
     *
     * @return STATUS_ - 状态
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置状态
     *
     * @param status 状态
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取释放时间
     *
     * @return FIRST_TIME_ - 释放时间
     */
    public Date getFirstTime() {
        return firstTime;
    }

    /**
     * 设置释放时间
     *
     * @param firstTime 释放时间
     */
    public void setFirstTime(Date firstTime) {
        this.firstTime = firstTime;
    }

    /**
     * 获取释放时间
     *
     * @return SECOND_TIME_ - 释放时间
     */
    public Date getSecondTime() {
        return secondTime;
    }

    /**
     * 设置释放时间
     *
     * @param secondTime 释放时间
     */
    public void setSecondTime(Date secondTime) {
        this.secondTime = secondTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", calendarId=").append(calendarId);
        sb.append(", status=").append(status);
        sb.append(", firstTime=").append(firstTime);
        sb.append(", secondTime=").append(secondTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}