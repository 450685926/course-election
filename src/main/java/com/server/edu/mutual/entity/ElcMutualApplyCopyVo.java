package com.server.edu.mutual.entity;

import java.io.Serializable;
import java.sql.Timestamp;

public class ElcMutualApplyCopyVo implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 校历ID（学年学期）
     */
    private Long calendarId;

    /**
     * 学号
     */
    private String studentId;

    /**
     * 申请时间
     */
    private Timestamp applyAt;

    /**
     * 申请人ID
     */
    private String userId;

    /**
     * 审核状态(0 未审核 1行政院系审核通过 2行政院系审核不过 3审核通过 4审核不通过)
     */
    private Integer status;

    /**
     * 申请课程ID
     */
    private Long mutualCourseId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 开课院系审核理由
     */
    private String auditReason;

    /**
     * 申请类型
     */
    private Integer mode;
    /**
     * 修读类型
     */
    private Integer courseTakeType;

    /*系列化*/
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

    public Timestamp getApplyAt() {
        return applyAt;
    }

    public void setApplyAt(Timestamp applyAt) {
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

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }
    
    public Integer getCourseTakeType() {
		return courseTakeType;
	}

	public void setCourseTakeType(Integer courseTakeType) {
		this.courseTakeType = courseTakeType;
	}

    @Override
    public String toString() {
        return "ElcMutualApplyVo{" +
                "id=" + id +
                ", calendarId=" + calendarId +
                ", studentId='" + studentId + '\'' +
                ", applyAt=" + applyAt +
                ", userId='" + userId + '\'' +
                ", status=" + status +
                ", mutualCourseId=" + mutualCourseId +
                ", remark='" + remark + '\'' +
                ", auditReason='" + auditReason + '\'' +
                ", mode=" + mode +
                ", courseTakeType=" + courseTakeType +
                '}';
    }
}