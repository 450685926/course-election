package com.server.edu.election.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "elc_med_withdraw_apply_log_t")
public class ElcMedWithdrawApplyLog implements Serializable {
    /**
     * 主键（自增）
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
     * 教学班ID
     */
    @Column(name = "TEACHING_CLASS_ID_")
    private Long teachingClassId;

    /**
     * 操作对象工号
     */
    @Column(name = "OPRATION_OBJ_CODE")
    private String oprationObjCode;

    /**
     * 操作对象姓名
     */
    @Column(name = "OPRATION_OBJ_NAME")
    private String oprationObjName;

    /**
     * 操作客户端IP
     */
    @Column(name = "OPRATION_CLIENT_IP")
    private String oprationClientIp;

    /**
     * CREATE新增,PAYING支付,PAYED支付完成,EN_AUDIT生效申请,DIS_AUDIT失效申请
     */
    @Column(name = "OPRATION_TYPE")
    private String oprationType;

    /**
     * 目标对象学号
     */
    @Column(name = "TARGET_OBJ_CODE")
    private String targetObjCode;

    /**
     * 目标对象姓名
     */
    @Column(name = "TARGET_OBJ_NAME")
    private String targetObjName;

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
     * 获取教学班ID
     *
     * @return TEACHING_CLASS_ID_ - 教学班ID
     */
    public Long getTeachingClassId() {
        return teachingClassId;
    }

    /**
     * 设置教学班ID
     *
     * @param teachingClassId 教学班ID
     */
    public void setTeachingClassId(Long teachingClassId) {
        this.teachingClassId = teachingClassId;
    }

    /**
     * 获取操作对象工号
     *
     * @return OPRATION_OBJ_CODE - 操作对象工号
     */
    public String getOprationObjCode() {
        return oprationObjCode;
    }

    /**
     * 设置操作对象工号
     *
     * @param oprationObjCode 操作对象工号
     */
    public void setOprationObjCode(String oprationObjCode) {
        this.oprationObjCode = oprationObjCode == null ? null : oprationObjCode.trim();
    }

    /**
     * 获取操作对象姓名
     *
     * @return OPRATION_OBJ_NAME - 操作对象姓名
     */
    public String getOprationObjName() {
        return oprationObjName;
    }

    /**
     * 设置操作对象姓名
     *
     * @param oprationObjName 操作对象姓名
     */
    public void setOprationObjName(String oprationObjName) {
        this.oprationObjName = oprationObjName == null ? null : oprationObjName.trim();
    }

    /**
     * 获取操作客户端IP
     *
     * @return OPRATION_CLIENT_IP - 操作客户端IP
     */
    public String getOprationClientIp() {
        return oprationClientIp;
    }

    /**
     * 设置操作客户端IP
     *
     * @param oprationClientIp 操作客户端IP
     */
    public void setOprationClientIp(String oprationClientIp) {
        this.oprationClientIp = oprationClientIp == null ? null : oprationClientIp.trim();
    }

    /**
     * 获取CREATE新增,PAYING支付,PAYED支付完成,EN_AUDIT生效申请,DIS_AUDIT失效申请
     *
     * @return OPRATION_TYPE - CREATE新增,PAYING支付,PAYED支付完成,EN_AUDIT生效申请,DIS_AUDIT失效申请
     */
    public String getOprationType() {
        return oprationType;
    }

    /**
     * 设置CREATE新增,PAYING支付,PAYED支付完成,EN_AUDIT生效申请,DIS_AUDIT失效申请
     *
     * @param oprationType CREATE新增,PAYING支付,PAYED支付完成,EN_AUDIT生效申请,DIS_AUDIT失效申请
     */
    public void setOprationType(String oprationType) {
        this.oprationType = oprationType == null ? null : oprationType.trim();
    }

    /**
     * 获取目标对象学号
     *
     * @return TARGET_OBJ_CODE - 目标对象学号
     */
    public String getTargetObjCode() {
        return targetObjCode;
    }

    /**
     * 设置目标对象学号
     *
     * @param targetObjCode 目标对象学号
     */
    public void setTargetObjCode(String targetObjCode) {
        this.targetObjCode = targetObjCode == null ? null : targetObjCode.trim();
    }

    /**
     * 获取目标对象姓名
     *
     * @return TARGET_OBJ_NAME - 目标对象姓名
     */
    public String getTargetObjName() {
        return targetObjName;
    }

    /**
     * 设置目标对象姓名
     *
     * @param targetObjName 目标对象姓名
     */
    public void setTargetObjName(String targetObjName) {
        this.targetObjName = targetObjName == null ? null : targetObjName.trim();
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
        sb.append(", teachingClassId=").append(teachingClassId);
        sb.append(", oprationObjCode=").append(oprationObjCode);
        sb.append(", oprationObjName=").append(oprationObjName);
        sb.append(", oprationClientIp=").append(oprationClientIp);
        sb.append(", oprationType=").append(oprationType);
        sb.append(", targetObjCode=").append(targetObjCode);
        sb.append(", targetObjName=").append(targetObjName);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}