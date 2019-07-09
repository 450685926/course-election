package com.server.edu.election.dto;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.dictionary.DictTypeEnum;

/**
 * 选课结果统计（学生统计）
 * @author qiangliz
 *
 */
@CodeI18n
public class ElcResultDto
{
    /**
     * 年级
     */
    private String grade;
    
    /**
     * 培养层次
     */
    @Code2Text(DictTypeEnum.X_PYCC)
    private String trainingLevel;
    
    /**
     * 培养类别
     */
    @Code2Text(DictTypeEnum.X_PYLB)
    private String trainingCategory;
    
    /**
     * 学习形式
     */
    @Code2Text(DictTypeEnum.X_XXXS)
    private String formLearning;
    
    /**
     * 学位类型
     */
    @Code2Text(DictTypeEnum.X_XWLX)
    private String degreeType;
    
    /**
     * 学生人数
     */
    private Integer studentNum;
    
    /**
     * 专业
     */
    @Code2Text(DictTypeEnum.G_ZY)
    private String profession;
    
    /**
     * 学院
     */
    @Code2Text(DictTypeEnum.X_YX)
    private String faculty;
    
    /**
     * 已选人数
     */
    private Integer numberOfelectedPersons;
  
    /**
     * 未选人数
     */
    private Integer numberOfNonCandidates;
    
    /**
     * 已选人数百分比
     */
    private Double numberOfelectedPersonsPoint;

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getTrainingLevel() {
		return trainingLevel;
	}

	public void setTrainingLevel(String trainingLevel) {
		this.trainingLevel = trainingLevel;
	}

	public String getTrainingCategory() {
		return trainingCategory;
	}

	public void setTrainingCategory(String trainingCategory) {
		this.trainingCategory = trainingCategory;
	}

	public String getFormLearning() {
		return formLearning;
	}

	public void setFormLearning(String formLearning) {
		this.formLearning = formLearning;
	}

	public String getDegreeType() {
		return degreeType;
	}

	public void setDegreeType(String degreeType) {
		this.degreeType = degreeType;
	}

	public Integer getStudentNum() {
		return studentNum;
	}

	public void setStudentNum(Integer studentNum) {
		this.studentNum = studentNum;
	}

	public Integer getNumberOfelectedPersons() {
		return numberOfelectedPersons;
	}

	public void setNumberOfelectedPersons(Integer numberOfelectedPersons) {
		this.numberOfelectedPersons = numberOfelectedPersons;
	}

	public Integer getNumberOfNonCandidates() {
		return numberOfNonCandidates;
	}

	public void setNumberOfNonCandidates(Integer numberOfNonCandidates) {
		this.numberOfNonCandidates = numberOfNonCandidates;
	}

	public Double getNumberOfelectedPersonsPoint() {
		return numberOfelectedPersonsPoint;
	}

	public void setNumberOfelectedPersonsPoint(Double numberOfelectedPersonsPoint) {
		this.numberOfelectedPersonsPoint = numberOfelectedPersonsPoint;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public String getFaculty() {
		return faculty;
	}

	public void setFaculty(String faculty) {
		this.faculty = faculty;
	}
    
}
