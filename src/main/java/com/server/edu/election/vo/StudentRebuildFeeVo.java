package com.server.edu.election.vo;

import java.util.Date;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.dictionary.translator.SchoolCalendarTranslator;
import com.server.edu.election.entity.ElcCourseTake;
@CodeI18n
public class StudentRebuildFeeVo extends ElcCourseTake {

    private static final long serialVersionUID = 1L;
    //流水号
    private String billNum;
    //应缴金额
    private double amount;
    //已缴金额
    private double pay;
    
    private Date payTime;
    
    private String studentName;
    
    @Code2Text(transformer = "X_YX")
    private String faculty;
    
    @Code2Text(transformer = "G_ZY")
    private String profession;
    
    private String courseName;
    
    private String teachingClassCode;
    
    private double credits;
    
    @Code2Text(transformer = "X_KCXZ")
    private String nature;
    
    //是否缴费
    private Integer paId;
    
    //课程学期
    @Code2Text(translator= SchoolCalendarTranslator.class)
    private Long couCalendarId;
    
    
	public Integer getPaId() {
		return paId;
	}

	public void setPaId(Integer paId) {
		this.paId = paId;
	}


	public Long getCouCalendarId() {
		return couCalendarId;
	}

	public void setCouCalendarId(Long couCalendarId) {
		this.couCalendarId = couCalendarId;
	}

	public String getNature() {
		return nature;
	}

	public void setNature(String nature) {
		this.nature = nature;
	}

	public String getBillNum() {
		return billNum;
	}

	public void setBillNum(String billNum) {
		this.billNum = billNum;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getPay() {
		return pay;
	}

	public void setPay(double pay) {
		this.pay = pay;
	}

	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}


	public String getFaculty() {
		return faculty;
	}

	public void setFaculty(String faculty) {
		this.faculty = faculty;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getTeachingClassCode() {
		return teachingClassCode;
	}

	public void setTeachingClassCode(String teachingClassCode) {
		this.teachingClassCode = teachingClassCode;
	}

	public double getCredits() {
		return credits;
	}

	public void setCredits(double credits) {
		this.credits = credits;
	}
    
    

}
