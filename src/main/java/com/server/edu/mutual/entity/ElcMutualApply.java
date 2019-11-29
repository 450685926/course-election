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

@Table(name = "elc_mutual_apply_t")
public class ElcMutualApply implements Serializable {
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
     * 学号
     */
    @NotBlank
    @Column(name = "STUDENT_ID_")
    private String studentId;

    /**
     * 申请时间
     */
    @Column(name = "APPLY_AT_")
    private Date applyAt;

    /**
     * 申请人ID
     */
    @Column(name = "USER_ID_")
    private String userId;

    /**
     * 审核状态(0 未审核 1行政院系审核通过 2行政院系审核不过 3审核通过 4审核不通过)
     */
    @Column(name = "STATUS_")
    private Integer status;

    /**
     * 申请课程ID
     */
    @Column(name = "MUTUAL_COURSE_ID_")
    private Long mutualCourseId;

    /**
     * 备注
     */
    @Column(name = "REMARK_")
    private String remark;

    /**
     * 开课院系审核理由
     */
    @Column(name = "AUDIT_REASON_")
    private String auditReason;

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
     * 获取申请时间
     *
     * @return APPLY_AT_ - 申请时间
     */
    public Date getApplyAt() {
        return applyAt;
    }

    /**
     * 设置申请时间
     *
     * @param applyAt 申请时间
     */
    public void setApplyAt(Date applyAt) {
        this.applyAt = applyAt;
    }

    /**
     * 获取申请人ID
     *
     * @return USER_ID_ - 申请人ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 设置申请人ID
     *
     * @param userId 申请人ID
     */
    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    /**
     * 获取审核状态(0 未审核 1行政院系审核通过 2行政院系审核不过 3审核通过 4审核不通过)
     *
     * @return STATUS_ - 审核状态(0 未审核 1行政院系审核通过 2行政院系审核不过 3审核通过 4审核不通过)
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置审核状态(0 未审核 1行政院系审核通过 2行政院系审核不过 3审核通过 4审核不通过)
     *
     * @param status 审核状态(0 未审核 1行政院系审核通过 2行政院系审核不过 3审核通过 4审核不通过)
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取申请课程ID
     *
     * @return MUTUAL_COURSE_ID_ - 申请课程ID
     */
    public Long getMutualCourseId() {
        return mutualCourseId;
    }

    /**
     * 设置申请课程ID
     *
     * @param mutualCourseId 申请课程ID
     */
    public void setMutualCourseId(Long mutualCourseId) {
        this.mutualCourseId = mutualCourseId;
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
     * 获取开课院系审核理由
     *
     * @return AUDIT_REASON_ - 开课院系审核理由
     */
    public String getAuditReason() {
        return auditReason;
    }

    /**
     * 设置开课院系审核理由
     *
     * @param auditReason 开课院系审核理由
     */
    public void setAuditReason(String auditReason) {
        this.auditReason = auditReason == null ? null : auditReason.trim();
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
        sb.append(", applyAt=").append(applyAt);
        sb.append(", userId=").append(userId);
        sb.append(", status=").append(status);
        sb.append(", mutualCourseId=").append(mutualCourseId);
        sb.append(", remark=").append(remark);
        sb.append(", auditReason=").append(auditReason);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}