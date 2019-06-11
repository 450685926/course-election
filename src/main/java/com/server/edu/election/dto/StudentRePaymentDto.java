package com.server.edu.election.dto;

/**
 * @description: 学生缴费详情
 * @author: bear
 * @create: 2019-05-27 10:52
 */
public class StudentRePaymentDto{
    private Long calendarId;
    private String courseCode;
    private String courseName;
    private Double credits;
    private Integer unitPrice;
    private Double Payable;
    private Integer paid;
    private String studentCode;
    private Long billId;

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public Long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Double getCredits() {
        return credits;
    }

    public void setCredits(Double credits) {
        this.credits = credits;
    }

    public Integer getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Integer unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Double getPayable() {
        return Payable;
    }

    public void setPayable(Double payable) {
        Payable = payable;
    }

    public Integer getPaid() {
        return paid;
    }

    public void setPaid(Integer paid) {
        this.paid = paid;
    }
}
