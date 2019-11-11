package com.server.edu.election.vo;

import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.dictionary.translator.TeacherTranslator;
import com.server.edu.election.entity.ElcMedWithdrawRuleRefCour;
@CodeI18n
public class ElcMedWithdrawRuleRefCourVo extends ElcMedWithdrawRuleRefCour {
    private static final long serialVersionUID = 1L;
    
    /**
     * 教学班编号（课程代码+2位）
     */
    private String code;
    
    /**
     * 教学班名称
     */
    private String name;
    /**
     * 课程代码
     */
    private String courseCode;
    
    /**
     * 课程名称
     */
    private String courseName;
    
    private String courseNameEn;
    
    @Code2Text(transformer = "X_PYCC")
    private String trainingLevel;

    @Code2Text(transformer = "X_XXXS")
    private String formLearning;
    
    @Code2Text(transformer = "X_KCXZ")
    private String nature;
    
    @Code2Text(transformer="X_YX")
    private String faculty;
    
    /**
     * 校区（取字典X_XQ）
     */
    @Code2Text(DictTypeEnum.X_XQ)
    private String campus;
    
    /**
     * 学分
     */
    private Double credits;
    
    @Code2Text(translator=TeacherTranslator.class,dataType = Code2Text.DataType.SPLIT)
    private String teacherName;
    

	public Double getCredits() {
		return credits;
	}

	public void setCredits(Double credits) {
		this.credits = credits;
	}

	public String getTeacherName() {
		return teacherName;
	}

	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getCourseNameEn() {
		return courseNameEn;
	}

	public void setCourseNameEn(String courseNameEn) {
		this.courseNameEn = courseNameEn;
	}

	public String getTrainingLevel() {
		return trainingLevel;
	}

	public void setTrainingLevel(String trainingLevel) {
		this.trainingLevel = trainingLevel;
	}

	public String getFormLearning() {
		return formLearning;
	}

	public void setFormLearning(String formLearning) {
		this.formLearning = formLearning;
	}

	public String getNature() {
		return nature;
	}

	public void setNature(String nature) {
		this.nature = nature;
	}

	public String getFaculty() {
		return faculty;
	}

	public void setFaculty(String faculty) {
		this.faculty = faculty;
	}

	public String getCampus() {
		return campus;
	}

	public void setCampus(String campus) {
		this.campus = campus;
	}
    
    
    
    
    
    
    
}
