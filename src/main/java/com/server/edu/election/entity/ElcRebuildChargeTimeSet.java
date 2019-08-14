package com.server.edu.election.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

@Table(name = "elc_rebuild_charge_time_set")
public class ElcRebuildChargeTimeSet implements Serializable {
    /**
     * 主键
     */
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 校历ID（学年学期）
     */
    @NotNull
    @Column(name = "CALENDAR_ID_")
    private Long calendarId;

    /**
     * 状态(0:关闭、1:开启)
     */
    @NotNull
    @Column(name = "STATUS")
    private Integer status;

    /**
     * 开始时间
     */
    @NotNull
    @Column(name = "STRATTIME")
    private Date strattime;

    /**
     * 结束时间
     */
    @NotNull
    @Column(name = "ENDTIME")
    private Date endtime;

    /**
     * 部门id
     */
    @NotBlank
    @Column(name = "PROJ_ID_")
    private String projId;

    private static final long serialVersionUID = 1L;

    /**
     * 获取主键
     *
     * @return ID_ - 主键
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
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
     * 获取状态(0:关闭、1:开启)
     *
     * @return STATUS - 状态(0:关闭、1:开启)
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置状态(0:关闭、1:开启)
     *
     * @param status 状态(0:关闭、1:开启)
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取开始时间
     *
     * @return STRATTIME - 开始时间
     */
    public Date getStrattime() {
        return strattime;
    }

    /**
     * 设置开始时间
     *
     * @param strattime 开始时间
     */
    public void setStrattime(Date strattime) {
        this.strattime = strattime;
    }

    /**
     * 获取结束时间
     *
     * @return ENDTIME - 结束时间
     */
    public Date getEndtime() {
        return endtime;
    }

    /**
     * 设置结束时间
     *
     * @param endtime 结束时间
     */
    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }

    /**
     * 获取部门id
     *
     * @return PROJ_ID_ - 部门id
     */
    public String getProjId() {
        return projId;
    }

    /**
     * 设置部门id
     *
     * @param projId 部门id
     */
    public void setProjId(String projId) {
        this.projId = projId == null ? null : projId.trim();
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
        sb.append(", strattime=").append(strattime);
        sb.append(", endtime=").append(endtime);
        sb.append(", projId=").append(projId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}