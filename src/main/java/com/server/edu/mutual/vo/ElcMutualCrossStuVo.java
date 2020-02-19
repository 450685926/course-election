package com.server.edu.mutual.vo;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
@CodeI18n
public class ElcMutualCrossStuVo {

	/*学位类别*/
	private String degreeType;

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
     * 培养类型
     */
    @Code2Text(transformer = "X_PYLB")
    private String trainingCategory;
    /**
     * 学位类型
     */
    @Code2Text(transformer = "X_XWLX")
    private String degreeCategory;
    /**
     * 学习形式
     */
    @Code2Text(transformer = "X_XXXS")
    private String formLearning;
    /**
     * 年级
     */
    private Integer grade;

	public String getDegreeType() {
		return degreeType;
	}

	public void setDegreeType(String degreeType) {
		this.degreeType = degreeType;
	}

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

	public String getTrainingCategory() {
		return trainingCategory;
	}

	public void setTrainingCategory(String trainingCategory) {
		this.trainingCategory = trainingCategory;
	}

	public String getDegreeCategory() {
		return degreeCategory;
	}

	public void setDegreeCategory(String degreeCategory) {
		this.degreeCategory = degreeCategory;
	}

	public String getFormLearning() {
		return formLearning;
	}

	public void setFormLearning(String formLearning) {
		this.formLearning = formLearning;
	}

	public ElcMutualCrossStuVo() {
	}

	public ElcMutualCrossStuVo(String degreeType, Long id, Long calendarId, String studentId, String studentName, Integer sex, String trainingLevel, String faculty, String profession, String trainingCategory, String degreeCategory, String formLearning, Integer grade) {
		this.degreeType = degreeType;
		this.id = id;
		this.calendarId = calendarId;
		this.studentId = studentId;
		this.studentName = studentName;
		this.sex = sex;
		this.trainingLevel = trainingLevel;
		this.faculty = faculty;
		this.profession = profession;
		this.trainingCategory = trainingCategory;
		this.degreeCategory = degreeCategory;
		this.formLearning = formLearning;
		this.grade = grade;
	}

	@Override
	public String toString() {
		return "ElcMutualCrossStuVo{" +
				"degreeType='" + degreeType + '\'' +
				", id=" + id +
				", calendarId=" + calendarId +
				", studentId='" + studentId + '\'' +
				", studentName='" + studentName + '\'' +
				", sex=" + sex +
				", trainingLevel='" + trainingLevel + '\'' +
				", faculty='" + faculty + '\'' +
				", profession='" + profession + '\'' +
				", trainingCategory='" + trainingCategory + '\'' +
				", degreeCategory='" + degreeCategory + '\'' +
				", formLearning='" + formLearning + '\'' +
				", grade=" + grade +
				'}';
	}
}
