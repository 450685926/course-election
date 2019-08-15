package com.server.edu.election.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "elc_med_withdraw_t")
public class ElcMedWithdrawApply implements Serializable {
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
     * 已缴标识
     */
    @Column(name = "PAID_FLAG_")
    private Boolean paidFlag;

    /**
     * 已缴金额
     */
    @Column(name = "PAID_PRICE_")
    private Double paidPrice;

    /**
     * 支付时间
     */
    @Column(name = "PAY_TIME_")
    private Date payTime;

    /**
     * 是否需要缴费(0否,1是)
     */
    @Column(name = "REQUIRE_PAID_")
    private Boolean requirePaid;

    /**
     * 应缴金额
     */
    @Column(name = "REQUIRE_PRICE_")
    private Double requirePrice;

    /**
     * 退课标识（0未退,1已退）
     */
    @Column(name = "WITHDRAW_FLAG_")
    private Boolean withdrawFlag;

    /**
     * 教学班ID
     */
    @Column(name = "TEACHING_CLASS_ID_")
    private Long teachingClassId;

    /**
     * 邮件发送标识
     */
    @Column(name = "MAIL_FLAG_")
    private Boolean mailFlag;

    /**
     * 交易流水号
     */
    @Column(name = "TRADE_NO_")
    private String tradeNo;

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
     * 获取已缴标识
     *
     * @return PAID_FLAG_ - 已缴标识
     */
    public Boolean getPaidFlag() {
        return paidFlag;
    }

    /**
     * 设置已缴标识
     *
     * @param paidFlag 已缴标识
     */
    public void setPaidFlag(Boolean paidFlag) {
        this.paidFlag = paidFlag;
    }

    /**
     * 获取已缴金额
     *
     * @return PAID_PRICE_ - 已缴金额
     */
    public Double getPaidPrice() {
        return paidPrice;
    }

    /**
     * 设置已缴金额
     *
     * @param paidPrice 已缴金额
     */
    public void setPaidPrice(Double paidPrice) {
        this.paidPrice = paidPrice;
    }

    /**
     * 获取支付时间
     *
     * @return PAY_TIME_ - 支付时间
     */
    public Date getPayTime() {
        return payTime;
    }

    /**
     * 设置支付时间
     *
     * @param payTime 支付时间
     */
    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    /**
     * 获取是否需要缴费(0否,1是)
     *
     * @return REQUIRE_PAID_ - 是否需要缴费(0否,1是)
     */
    public Boolean getRequirePaid() {
        return requirePaid;
    }

    /**
     * 设置是否需要缴费(0否,1是)
     *
     * @param requirePaid 是否需要缴费(0否,1是)
     */
    public void setRequirePaid(Boolean requirePaid) {
        this.requirePaid = requirePaid;
    }

    /**
     * 获取应缴金额
     *
     * @return REQUIRE_PRICE_ - 应缴金额
     */
    public Double getRequirePrice() {
        return requirePrice;
    }

    /**
     * 设置应缴金额
     *
     * @param requirePrice 应缴金额
     */
    public void setRequirePrice(Double requirePrice) {
        this.requirePrice = requirePrice;
    }

    /**
     * 获取退课标识（0未退,1已退）
     *
     * @return WITHDRAW_FLAG_ - 退课标识（0未退,1已退）
     */
    public Boolean getWithdrawFlag() {
        return withdrawFlag;
    }

    /**
     * 设置退课标识（0未退,1已退）
     *
     * @param withdrawFlag 退课标识（0未退,1已退）
     */
    public void setWithdrawFlag(Boolean withdrawFlag) {
        this.withdrawFlag = withdrawFlag;
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
     * 获取邮件发送标识
     *
     * @return MAIL_FLAG_ - 邮件发送标识
     */
    public Boolean getMailFlag() {
        return mailFlag;
    }

    /**
     * 设置邮件发送标识
     *
     * @param mailFlag 邮件发送标识
     */
    public void setMailFlag(Boolean mailFlag) {
        this.mailFlag = mailFlag;
    }

    /**
     * 获取交易流水号
     *
     * @return TRADE_NO_ - 交易流水号
     */
    public String getTradeNo() {
        return tradeNo;
    }

    /**
     * 设置交易流水号
     *
     * @param tradeNo 交易流水号
     */
    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo == null ? null : tradeNo.trim();
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
        sb.append(", paidFlag=").append(paidFlag);
        sb.append(", paidPrice=").append(paidPrice);
        sb.append(", payTime=").append(payTime);
        sb.append(", requirePaid=").append(requirePaid);
        sb.append(", requirePrice=").append(requirePrice);
        sb.append(", withdrawFlag=").append(withdrawFlag);
        sb.append(", teachingClassId=").append(teachingClassId);
        sb.append(", mailFlag=").append(mailFlag);
        sb.append(", tradeNo=").append(tradeNo);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}