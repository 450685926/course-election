package com.server.edu.election.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.server.edu.dictionary.annotation.CodeI18n;
@CodeI18n
@Table(name = "elc_student_limit_t")
public class ElcStudentLimit implements Serializable {
    /**
     * 主键
     */
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 学号
     */
    @Column(name = "STUDENT_ID_")
    private String studentId;

    /**
     * 学期
     */
    @Column(name = "CALENDAR_ID_")
    private Long calendarId;

    /**
     * 部门ID
     */
    @Column(name = "PROJECT_ID_")
    private String projectId;

    /**
     * 新选学分上限
     */
    @Column(name = "NEW_LIMIT_CREDITS_")
    private Double newLimitCredits;

    /**
     * 总学分上限
     */
    @Column(name = "TOTAL_LIMIT_CREDITS_")
    private Double totalLimitCredits;

    /**
     * 重修门数上限
     */
    @Column(name = "REBUILD_LIMIT_NUMBER_")
    private Integer rebuildLimitNumber;

    /**
     * 创建时间
     */
    @Column(name = "CREATED_AT_")
    private Date createdAt;

    /**
     * 修改时间
     */
    @Column(name = "UPDATED_AT")
    private Date updatedAt;

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
     * 获取学期
     *
     * @return CALENDAR_ID_ - 学期
     */
    public Long getCalendarId() {
        return calendarId;
    }

    /**
     * 设置学期
     *
     * @param calendarId 学期
     */
    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }

    /**
     * 获取部门ID
     *
     * @return PROJECT_ID_ - 部门ID
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * 设置部门ID
     *
     * @param projectId 部门ID
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId == null ? null : projectId.trim();
    }

    /**
     * 获取新选学分上限
     *
     * @return NEW_LIMIT_CREDITS_ - 新选学分上限
     */
    public Double getNewLimitCredits() {
        return newLimitCredits;
    }

    /**
     * 设置新选学分上限
     *
     * @param newLimitCredits 新选学分上限
     */
    public void setNewLimitCredits(Double newLimitCredits) {
        this.newLimitCredits = newLimitCredits;
    }

    /**
     * 获取总学分上限
     *
     * @return TOTAL_LIMIT_CREDITS_ - 总学分上限
     */
    public Double getTotalLimitCredits() {
        return totalLimitCredits;
    }

    /**
     * 设置总学分上限
     *
     * @param totalLimitCredits 总学分上限
     */
    public void setTotalLimitCredits(Double totalLimitCredits) {
        this.totalLimitCredits = totalLimitCredits;
    }

    /**
     * 获取重修门数上限
     *
     * @return REBUILD_LIMIT_NUMBER_ - 重修门数上限
     */
    public Integer getRebuildLimitNumber() {
        return rebuildLimitNumber;
    }

    /**
     * 设置重修门数上限
     *
     * @param rebuildLimitNumber 重修门数上限
     */
    public void setRebuildLimitNumber(Integer rebuildLimitNumber) {
        this.rebuildLimitNumber = rebuildLimitNumber;
    }

    /**
     * 获取创建时间
     *
     * @return CREATED_AT_ - 创建时间
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * 设置创建时间
     *
     * @param createdAt 创建时间
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 获取修改时间
     *
     * @return UPDATED_AT - 修改时间
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 设置修改时间
     *
     * @param updatedAt 修改时间
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", studentId=").append(studentId);
        sb.append(", calendarId=").append(calendarId);
        sb.append(", projectId=").append(projectId);
        sb.append(", newLimitCredits=").append(newLimitCredits);
        sb.append(", totalLimitCredits=").append(totalLimitCredits);
        sb.append(", rebuildLimitNumber=").append(rebuildLimitNumber);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}