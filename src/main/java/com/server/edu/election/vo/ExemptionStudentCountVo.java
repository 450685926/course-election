package com.server.edu.election.vo;

import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.election.entity.Student;

/**
 * @description: 重修门数
 * @author: bear
 * @create: 2019-02-13 16:14
 */
@CodeI18n
public class ExemptionStudentCountVo extends Student {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**培养层次*/
    @Code2Text(DictTypeEnum.X_PYCC)
	private String trainingLevel;
    
    /**培养类别*/
    @Code2Text(DictTypeEnum.X_PYLB)
    private String trainingCategory;
    
    /**学位类型*/
    @Code2Text(DictTypeEnum.X_XWLX)
    private String degreeType;
    
    /**学习形式*/
    @Code2Text(DictTypeEnum.X_XXXS)
    private String formLearning;
    
    /**课程编码*/
    private String courseCode;
    
    /**课程名称*/
    private String courseName;
    
    /**申请条件*/
    private String applyCondition;
    
    /**申请人数*/
    private Integer applyNum;

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

	public String getApplyCondition() {
		return applyCondition;
	}

	public void setApplyCondition(String applyCondition) {
		this.applyCondition = applyCondition;
	}

	public Integer getApplyNum() {
		return applyNum;
	}

	public void setApplyNum(Integer applyNum) {
		this.applyNum = applyNum;
	}
    
}
