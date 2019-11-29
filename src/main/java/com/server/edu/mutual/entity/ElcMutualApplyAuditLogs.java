package com.server.edu.mutual.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "elc_mutual_apply_audit_logs_t")
public class ElcMutualApplyAuditLogs implements Serializable {
    /**
     * 主键
     */
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 审核类型(1 行政学院审核 2 开课学院审核)
     */
    @Column(name = "AUDIT_TYPE_")
    private Integer auditType;

    /**
     * 是否审核通过(0 审核不通过 1审核通过)
     */
    @Column(name = "APPROVED_")
    private Integer approved;

    /**
     * 审核人ID
     */
    @Column(name = "AUDIT_USER_ID_")
    private String auditUserId;

    /**
     * 审核时间
     */
    @Column(name = "AUDIT_AT_")
    private Date auditAt;

    /**
     * 互选申请ID
     */
    @Column(name = "MU_APPLY_ID_")
    private Long muApplyId;

    /**
     * 审核原因
     */
    @Column(name = "REASON_")
    private String reason;

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
     * 获取审核类型(1 行政学院审核 2 开课学院审核)
     *
     * @return AUDIT_TYPE_ - 审核类型(1 行政学院审核 2 开课学院审核)
     */
    public Integer getAuditType() {
        return auditType;
    }

    /**
     * 设置审核类型(1 行政学院审核 2 开课学院审核)
     *
     * @param auditType 审核类型(1 行政学院审核 2 开课学院审核)
     */
    public void setAuditType(Integer auditType) {
        this.auditType = auditType;
    }

    /**
     * 获取是否审核通过(0 审核不通过 1审核通过)
     *
     * @return APPROVED_ - 是否审核通过(0 审核不通过 1审核通过)
     */
    public Integer getApproved() {
        return approved;
    }

    /**
     * 设置是否审核通过(0 审核不通过 1审核通过)
     *
     * @param approved 是否审核通过(0 审核不通过 1审核通过)
     */
    public void setApproved(Integer approved) {
        this.approved = approved;
    }

    /**
     * 获取审核人ID
     *
     * @return AUDIT_USER_ID_ - 审核人ID
     */
    public String getAuditUserId() {
        return auditUserId;
    }

    /**
     * 设置审核人ID
     *
     * @param auditUserId 审核人ID
     */
    public void setAuditUserId(String auditUserId) {
        this.auditUserId = auditUserId == null ? null : auditUserId.trim();
    }

    /**
     * 获取审核时间
     *
     * @return AUDIT_AT_ - 审核时间
     */
    public Date getAuditAt() {
        return auditAt;
    }

    /**
     * 设置审核时间
     *
     * @param auditAt 审核时间
     */
    public void setAuditAt(Date auditAt) {
        this.auditAt = auditAt;
    }

    /**
     * 获取互选申请ID
     *
     * @return MU_APPLY_ID_ - 互选申请ID
     */
    public Long getMuApplyId() {
        return muApplyId;
    }

    /**
     * 设置互选申请ID
     *
     * @param muApplyId 互选申请ID
     */
    public void setMuApplyId(Long muApplyId) {
        this.muApplyId = muApplyId;
    }

    /**
     * 获取审核原因
     *
     * @return REASON_ - 审核原因
     */
    public String getReason() {
        return reason;
    }

    /**
     * 设置审核原因
     *
     * @param reason 审核原因
     */
    public void setReason(String reason) {
        this.reason = reason == null ? null : reason.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", auditType=").append(auditType);
        sb.append(", approved=").append(approved);
        sb.append(", auditUserId=").append(auditUserId);
        sb.append(", auditAt=").append(auditAt);
        sb.append(", muApplyId=").append(muApplyId);
        sb.append(", reason=").append(reason);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}