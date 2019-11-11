package com.server.edu.election.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "elc_bills_t")
public class ElcBill implements Serializable {
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
     * 校历ID（学年学期）
     */
    @Column(name = "CALENDAR_ID_")
    private Long calendarId;

    /**
     * 账单流水号码
     */
    @Column(name = "BILL_NUM_")
    private String billNum;

    /**
     * 应付金额
     */
    @Column(name = "AMOUNT_")
    private Double amount;

    /**
     * 已付金额
     */
    @Column(name = "PAY_")
    private Double pay;

    /**
     * 支付说明
     */
    @Column(name = "REMARK_")
    private String remark;

    /**
     * 创建时间
     */
    @Column(name = "CREATED_AT_")
    private Date createdAt;

    /**
     * @Description: 支付标识 true支付，false未支付
     * @author kan yuanfeng
     * @date 2019/11/7 10:06
     */
    @Transient
    private Boolean flag;

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

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
     * 获取账单流水号码
     *
     * @return BILL_NUM_ - 账单流水号码
     */
    public String getBillNum() {
        return billNum;
    }

    /**
     * 设置账单流水号码
     *
     * @param billNum 账单流水号码
     */
    public void setBillNum(String billNum) {
        this.billNum = billNum == null ? null : billNum.trim();
    }

    /**
     * 获取应付金额
     *
     * @return AMOUNT_ - 应付金额
     */
    public Double getAmount() {
        return amount;
    }

    /**
     * 设置应付金额
     *
     * @param amount 应付金额
     */
    public void setAmount(Double amount) {
        this.amount = amount;
    }

    /**
     * 获取已付金额
     *
     * @return PAY_ - 已付金额
     */
    public Double getPay() {
        return pay;
    }

    /**
     * 设置已付金额
     *
     * @param pay 已付金额
     */
    public void setPay(Double pay) {
        this.pay = pay;
    }

    /**
     * 获取支付说明
     *
     * @return REMARK_ - 支付说明
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置支付说明
     *
     * @param remark 支付说明
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
        sb.append(", billNum=").append(billNum);
        sb.append(", amount=").append(amount);
        sb.append(", pay=").append(pay);
        sb.append(", remark=").append(remark);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}