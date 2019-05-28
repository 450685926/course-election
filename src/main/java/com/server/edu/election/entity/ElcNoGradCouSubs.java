package com.server.edu.election.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotBlank;

@Table(name = "elc_no_grad_cou_subs_t")
public class ElcNoGradCouSubs implements Serializable {
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 管理部门ID
     */
    @Column(name = "PROJECT_ID_")
    private String projectId;

    @Column(name = "CALENDAR_ID_")
    @NotBlank
    private Long calendarId;

    /**
     * 原课程代码
     */
    @Column(name = "ORIGS_COURSE_ID_")
    @NotBlank
    private String origsCourseId;

    /**
     * 替代课程代码
     */
    @Column(name = "SUB_COURSE_ID_")
    @NotBlank
    private String subCourseId;

    /**
     * 备注
     */
    @Column(name = "REMARK_")
    private String remark;

    /**
     * 创建时间
     */
    @Column(name = "CREATED_AT_")
    private Date createdAt;

    @Column(name = "UPDATED_AT")
    private Date updatedAt;

    private static final long serialVersionUID = 1L;

    /**
     * @return ID_
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取管理部门ID
     *
     * @return PROJECT_ID_ - 管理部门ID
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * 设置管理部门ID
     *
     * @param projectId 管理部门ID
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId == null ? null : projectId.trim();
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
     * 获取原课程代码
     *
     * @return ORIGS_COURSE_ID_ - 原课程代码
     */
    public String getOrigsCourseId() {
        return origsCourseId;
    }

    /**
     * 设置原课程代码
     *
     * @param origsCourseId 原课程代码
     */
    public void setOrigsCourseId(String origsCourseId) {
        this.origsCourseId = origsCourseId == null ? null : origsCourseId.trim();
    }

    /**
     * 获取替代课程代码
     *
     * @return SUB_COURSE_ID_ - 替代课程代码
     */
    public String getSubCourseId() {
        return subCourseId;
    }

    /**
     * 设置替代课程代码
     *
     * @param subCourseId 替代课程代码
     */
    public void setSubCourseId(String subCourseId) {
        this.subCourseId = subCourseId == null ? null : subCourseId.trim();
    }

    /**
     * 获取备注
     *
     * @return REMARK_ - 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注
     *
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
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
     * @return UPDATED_AT
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * @param updatedAt
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
        sb.append(", projectId=").append(projectId);
        sb.append(", calendarId=").append(calendarId);
        sb.append(", origsCourseId=").append(origsCourseId);
        sb.append(", subCourseId=").append(subCourseId);
        sb.append(", remark=").append(remark);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}