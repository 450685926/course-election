package com.server.edu.election.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * 支付订单
 * @author xllicy
 *
 */
public class PayOrderDto
{
    /**
     * 订单ID
     */
    private Long id;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 部门id（区分本研）
     */
    private String projId;
    
    /**
     * 学生id
     */
    private String studentId;
    
    /**
     * 费用项代码(对外代码)
     */
    private String feeItemCode;
    
    /**
     * 年级
     */
    private String grade;
    
    /**
     * 学期
     */
    private String term;
    
    /**
     * 学院
     */
    private String faculty;
    
    /**
     * 专业
     */
    private String major;
    
    /**
     * 支付状态
     */
    private Integer payStatus;
    
    /**
     * 金额
     */
    private Double amount;

    /**
     * 缴费数据的业务id集合
     */
    private String busIds;

    public String getBusIds() {
        return busIds;
    }

    public void setBusIds(String busIds) {
        this.busIds = busIds;
    }

    
    /**
     * 创建时间
     */
    private Long createTime;
    
    /**
     * 更新时间
     */
    private Long updateTime;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getOrderNo()
    {
        return orderNo;
    }

    public void setOrderNo(String orderNo)
    {
        this.orderNo = orderNo;
    }

    public String getStudentId()
    {
        return studentId;
    }

    public void setStudentId(String studentId)
    {
        this.studentId = studentId;
    }

    public String getFeeItemCode()
    {
        return feeItemCode;
    }

    public void setFeeItemCode(String feeItemCode)
    {
        this.feeItemCode = feeItemCode;
    }

    public String getGrade()
    {
        return grade;
    }

    public void setGrade(String grade)
    {
        this.grade = grade;
    }

    public String getTerm()
    {
        return term;
    }

    public void setTerm(String term)
    {
        this.term = term;
    }

    public String getFaculty()
    {
        return faculty;
    }

    public void setFaculty(String faculty)
    {
        this.faculty = faculty;
    }

    public String getMajor()
    {
        return major;
    }

    public void setMajor(String major)
    {
        this.major = major;
    }

    public Integer getPayStatus()
    {
        return payStatus;
    }

    public void setPayStatus(Integer payStatus)
    {
        this.payStatus = payStatus;
    }

    public Double getAmount()
    {
        return amount;
    }

    public void setAmount(Double amount)
    {
        this.amount = amount;
    }

    public Long getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Long createTime)
    {
        this.createTime = createTime;
    }

    public Long getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime)
    {
        this.updateTime = updateTime;
    }

    public String getProjId()
    {
        return projId;
    }

    public void setProjId(String projId)
    {
        this.projId = projId;
    }

    @Override
    public String toString()
    {
        return "PayOrder [id=" + id + ", orderNo=" + orderNo + ", projId="
            + projId + ", studentId=" + studentId + ", feeItemCode="
            + feeItemCode + ", grade=" + grade + ", term=" + term + ", faculty="
            + faculty + ", major=" + major + ", payStatus=" + payStatus
            + ", amount=" + amount + ", createTime=" + createTime
            + ", updateTime=" + updateTime + "]";
    }
}
