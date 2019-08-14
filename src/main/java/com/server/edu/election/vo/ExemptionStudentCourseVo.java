package com.server.edu.election.vo;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;

/**
 * @description: 免修免考返回结果
 * @author: bear
 * @create: 2019-02-02 17:32
 */
@CodeI18n
public class ExemptionStudentCourseVo{

    private String courseNameAndCode;
    
    private String firstForeignLanguageCode;
    
    private String firstForeignLanguageName;
    
    private Double firstForeignLanguageScore;
    
    private String courseCode;
    
    private String courseName;
    
    private String applyCourse;
    
    @Code2Text(transformer = "X_MXSQLX")
    private Integer applyType;
    
    @Code2Text(transformer = "X_SHZT")
    private Integer examineResult;

	public String getCourseNameAndCode() {
		return courseNameAndCode;
	}

	public void setCourseNameAndCode(String courseNameAndCode) {
		this.courseNameAndCode = courseNameAndCode;
	}

	public String getFirstForeignLanguageCode() {
		return firstForeignLanguageCode;
	}

	public void setFirstForeignLanguageCode(String firstForeignLanguageCode) {
		this.firstForeignLanguageCode = firstForeignLanguageCode;
	}

	public Double getFirstForeignLanguageScore() {
		return firstForeignLanguageScore;
	}

	public void setFirstForeignLanguageScore(Double firstForeignLanguageScore) {
		this.firstForeignLanguageScore = firstForeignLanguageScore;
	}

	public String getApplyCourse() {
		return applyCourse;
	}

	public void setApplyCourse(String applyCourse) {
		this.applyCourse = applyCourse;
	}

	public Integer getApplyType() {
		return applyType;
	}

	public void setApplyType(Integer applyType) {
		this.applyType = applyType;
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

	public String getFirstForeignLanguageName() {
		return firstForeignLanguageName;
	}

	public void setFirstForeignLanguageName(String firstForeignLanguageName) {
		this.firstForeignLanguageName = firstForeignLanguageName;
	}

	public Integer getExamineResult() {
		return examineResult;
	}

	public void setExamineResult(Integer examineResult) {
		this.examineResult = examineResult;
	}
	
}
