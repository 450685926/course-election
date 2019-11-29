package com.server.edu.mutual.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

@Table(name = "elc_mutual_apply_turns_t")
public class ElcMutualApplyTurns implements Serializable {
    /**
     * 主键
     */
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 部门Id
     */
    @Column(name = "PROJECT_ID_")
    @NotBlank
    private String projectId;

    /**
     * 校历ID（学年学期）
     */
    @NotNull
    @Column(name = "CALENDAR_ID_")
    private Long calendarId;

    /**
     * 开始时间
     */
    @NotNull
    @Column(name = "BEGIN_AT_")
    private Date beginAt;

    /**
     * 结束时间
     */
    @NotNull
    @Column(name = "END_AT_")
    private Date endAt;

    /**
     * 开启状态(1-开启；0-关闭)
     */
    @NotNull
    @Column(name = "OPEN_")
    private Integer open;

    /**
     * 开关类别：1:本研互选  2：跨学科互选
     */
    @NotNull
    @Column(name = "CATEGORY_")
    private Integer category;

    /**
     *  绩点
     */
    @Column(name = "GPA")
    private Float gpa;

    /**
     * 是否检测学生是否存在未及格的课程(1是，0否)
     */
    @NotNull
    @Column(name = "FAIL")
    private Integer fail;

    /**
     * 申请上限
     */
    @Column(name = "APP_LIMIT")
    private Integer appLimit;

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
     * 获取部门Id
     *
     * @return PROJECT_ID_ - 部门Id
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * 设置部门Id
     *
     * @param projectId 部门Id
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId == null ? null : projectId.trim();
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
     * 获取开始时间
     *
     * @return BEGIN_AT_ - 开始时间
     */
    public Date getBeginAt() {
        return beginAt;
    }

    /**
     * 设置开始时间
     *
     * @param beginAt 开始时间
     */
    public void setBeginAt(Date beginAt) {
        this.beginAt = beginAt;
    }

    /**
     * 获取结束时间
     *
     * @return END_AT_ - 结束时间
     */
    public Date getEndAt() {
        return endAt;
    }

    /**
     * 设置结束时间
     *
     * @param endAt 结束时间
     */
    public void setEndAt(Date endAt) {
        this.endAt = endAt;
    }

    /**
     * 获取开启状态
     *
     * @return OPEN_ - 开启状态
     */
    public Integer getOpen() {
        return open;
    }

    /**
     * 设置开启状态
     *
     * @param open 开启状态
     */
    public void setOpen(Integer open) {
        this.open = open;
    }

    /**
     * 获取开关类别：1:本研互选  2：跨学科互选
     *
     * @return CATEGORY_ - 开关类别：1:本研互选  2：跨学科互选
     */
    public Integer getCategory() {
        return category;
    }

    /**
     * 设置开关类别：1:本研互选  2：跨学科互选
     *
     * @param category 开关类别：1:本研互选  2：跨学科互选
     */
    public void setCategory(Integer category) {
        this.category = category;
    }

    /**
     * 获取 绩点
     *
     * @return GPA -  绩点
     */
    public Float getGpa() {
        return gpa;
    }

    /**
     * 设置 绩点
     *
     * @param gpa  绩点
     */
    public void setGpa(Float gpa) {
        this.gpa = gpa;
    }

    /**
     * 获取是否检测学生是否存在未及格的课程(1是，0否)
     *
     * @return FAIL - 是否检测学生是否存在未及格的课程(1是，0否)
     */
    public Integer getFail() {
        return fail;
    }

    /**
     * 设置是否检测学生是否存在未及格的课程(1是，0否)
     *
     * @param fail 是否检测学生是否存在未及格的课程(1是，0否)
     */
    public void setFail(Integer fail) {
        this.fail = fail;
    }

    /**
     * 获取申请上限
     *
     * @return APP_LIMIT - 申请上限
     */
    public Integer getAppLimit() {
        return appLimit;
    }

    /**
     * 设置申请上限
     *
     * @param appLimit 申请上限
     */
    public void setAppLimit(Integer appLimit) {
        this.appLimit = appLimit;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", projectId=").append(projectId);
        sb.append(", calendarId=").append(calendarId);
        sb.append(", beginAt=").append(beginAt);
        sb.append(", endAt=").append(endAt);
        sb.append(", open=").append(open);
        sb.append(", category=").append(category);
        sb.append(", gpa=").append(gpa);
        sb.append(", fail=").append(fail);
        sb.append(", appLimit=").append(appLimit);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}