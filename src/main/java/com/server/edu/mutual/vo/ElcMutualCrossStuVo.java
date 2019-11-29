package com.server.edu.mutual.vo;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
@CodeI18n
public class ElcMutualCrossStuVo {

	private Long id;
   
    private Long calendarId;
    
    private String studentId;
    
    /**
     * 姓名
     */
    private String studentName;
    
    /**
     * 性别(根据数据字典统一使用)
     */
    @Code2Text(transformer = "G_XBIE")
    private Integer sex;
    
    /**
     * 培养层次(专科   本科   硕士   博士    其他    预科)
     */
    @Code2Text(transformer = "X_PYCC")
    private String trainingLevel;
    
    /**
     * 学院
     */
    @Code2Text(transformer = "X_YX")
    private String faculty;
    
    /**
     * 专业
     */
    @Code2Text(transformer = "G_ZY")
    private String profession;
    
    /**
     * 年级
     */
    private Integer grade;
    

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCalendarId() {
		return calendarId;
	}

	public void setCalendarId(Long calendarId) {
		this.calendarId = calendarId;
	}

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public String getTrainingLevel() {
		return trainingLevel;
	}

	public void setTrainingLevel(String trainingLevel) {
		this.trainingLevel = trainingLevel;
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

	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}
    
    
}
