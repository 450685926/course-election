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

@Table(name = "election_applys_t")
public class ElectionApply implements Serializable {
    /**
     * 主键（自增）
     */
	@NotNull
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
     * 校历ID（学年学期）
     */
    @Column(name = "CALENDAR_ID_")
    private Long calendarId;

    /**
     * 课程代码
     */
    @Column(name = "COURSE_CODE_")
    private String courseCode;

    /**
     * 是否同意(0否,1是)
     */
    @Column(name = "APPLY_")
    private Integer apply;

    /**
     * 同意人
     */
    @Column(name = "APPLY_BY_")
    private String applyBy;

    /**
     * 备注
     */
    @NotBlank
    @Column(name = "REMARK_")
    private String remark;

    /**
     * 创建时间
     */
    @Column(name = "CREATED_AT_")
    private Date createdAt;

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
     * 获取课程代码
     *
     * @return COURSE_CODE_ - 课程代码
     */
    public String getCourseCode() {
        return courseCode;
    }

    /**
     * 设置课程代码
     *
     * @param courseCode 课程代码
     */
    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode == null ? null : courseCode.trim();
    }

    /**
     * 获取是否同意(0否,1是)
     *
     * @return APPLY_ - 是否同意(0否,1是)
     */
    public Integer getApply() {
        return apply;
    }

    /**
     * 设置是否同意(0否,1是)
     *
     * @param apply 是否同意(0否,1是)
     */
    public void setApply(Integer apply) {
        this.apply = apply;
    }

    /**
     * 获取同意人
     *
     * @return APPLY_BY_ - 同意人
     */
    public String getApplyBy() {
        return applyBy;
    }

    /**
     * 设置同意人
     *
     * @param applyBy 同意人
     */
    public void setApplyBy(String applyBy) {
        this.applyBy = applyBy == null ? null : applyBy.trim();
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", studentId=").append(studentId);
        sb.append(", calendarId=").append(calendarId);
        sb.append(", courseCode=").append(courseCode);
        sb.append(", apply=").append(apply);
        sb.append(", applyBy=").append(applyBy);
        sb.append(", remark=").append(remark);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}