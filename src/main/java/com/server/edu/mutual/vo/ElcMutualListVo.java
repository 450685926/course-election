package com.server.edu.mutual.vo;

import java.util.List;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.election.studentelec.context.TimeAndRoom;
import com.server.edu.mutual.entity.ElcMutualApply;

@CodeI18n
public class ElcMutualListVo extends ElcMutualApply{
	private static final long serialVersionUID = 1L;
	
	/**
	 * 姓名
	 */
	private String studentName;
	
    /**
     * 年级
     */
    private Integer grade;
    
    /**
     * 学生学院（学生所在行政学院）
     */
    private String college;
    
    /**
     * 开课学院
     */
    @Code2Text(transformer = "X_YX")
    private String openCollege;
	
    /**
     * 专业
     */
    @Code2Text(transformer = "G_ZY")
    private String profession;
	
    /**
     * 培养层次(专科   本科   硕士   博士    其他    预科)
     */
    @Code2Text(transformer = "X_PYCC")
    private String stuTrainingLevel;
    
    /**
     * 课程层次(专科   本科   硕士   博士    其他    预科)
     */
    @Code2Text(transformer = "X_PYCC")
    private String courseTrainingLevel;
    
    /**
     * 学生类别
     */
    @Code2Text(transformer = "X_XSLB")
    private String studentCategory;
    
    /**
     * 培养类别
     */
    @Code2Text(transformer = "X_PYLB")
    private String trainingCategory;
	
    /**
     * 学位类型
     */
    @Code2Text(transformer = "X_XWLX")
    private String degreeType;
    
    /**
     * 学习形式
     */
    @Code2Text(transformer = "X_XXXS")
    private String formLearning;
    
    /**
     * 课程编号
     */
    private String courseCode;
    
    /**
     * 课程名称
     */
    private String courseName;
    
    /**
     * 研究生课程性质
     */
    @Code2Text(transformer="X_KCXZ")
    private String nature;
    
    /**
     * 本科生课程性质
     */
    @Code2Text(transformer="K_BKKCXZ")
    private String isElective;
    
    /**
     * 教学安排（上课时间地点）
     */
    private List<TimeAndRoom> timeTableList;
    
    /**
     * 学分
     */
    private Double credits;
    
    /**
     * 修读类别(1正常修读,2重修,3免修不免考,4免修)
     */
    @Code2Text(transformer="X_XDLX")
    private Integer courseTakeType;
    
    /**
     * 申请人数
     */
    private Integer stuNumber;

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public String getStuTrainingLevel() {
		return stuTrainingLevel;
	}

	public void setStuTrainingLevel(String stuTrainingLevel) {
		this.stuTrainingLevel = stuTrainingLevel;
	}

	public String getTrainingCategory() {
		return trainingCategory;
	}

	public void setTrainingCategory(String trainingCategory) {
		this.trainingCategory = trainingCategory;
	}

	public String getDegreeType() {
		return degreeType;
	}

	public void setDegreeType(String degreeType) {
		this.degreeType = degreeType;
	}

	public String getFormLearning() {
		return formLearning;
	}

	public void setFormLearning(String formLearning) {
		this.formLearning = formLearning;
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

	public String getNature() {
		return nature;
	}

	public void setNature(String nature) {
		this.nature = nature;
	}

	public String getIsElective() {
		return isElective;
	}

	public void setIsElective(String isElective) {
		this.isElective = isElective;
	}

	public List<TimeAndRoom> getTimeTableList() {
		return timeTableList;
	}

	public void setTimeTableList(List<TimeAndRoom> timeTableList) {
		this.timeTableList = timeTableList;
	}

	public Double getCredits() {
		return credits;
	}

	public void setCredits(Double credits) {
		this.credits = credits;
	}

	public Integer getCourseTakeType() {
		return courseTakeType;
	}

	public void setCourseTakeType(Integer courseTakeType) {
		this.courseTakeType = courseTakeType;
	}

	public String getCollege() {
		return college;
	}

	public void setCollege(String college) {
		this.college = college;
	}

	public String getStudentCategory() {
		return studentCategory;
	}

	public void setStudentCategory(String studentCategory) {
		this.studentCategory = studentCategory;
	}

	public String getOpenCollege() {
		return openCollege;
	}

	public void setOpenCollege(String openCollege) {
		this.openCollege = openCollege;
	}

	public String getCourseTrainingLevel() {
		return courseTrainingLevel;
	}

	public void setCourseTrainingLevel(String courseTrainingLevel) {
		this.courseTrainingLevel = courseTrainingLevel;
	}

	public Integer getStuNumber() {
		return stuNumber;
	}

	public void setStuNumber(Integer stuNumber) {
		this.stuNumber = stuNumber;
	}
    
}
