package com.server.edu.mutual.vo;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.mutual.entity.ElcMutualCourses;

@CodeI18n
public class ElcMutualCoursesVo extends ElcMutualCourses {

    private static final long serialVersionUID = 1L;
    
    private String courseCode;
    
    private String courseName;
    
    private Double credits;
    
    @Code2Text(transformer="X_YX")
    private String college;
    
    /**
     * 开课学院
     */
    @Code2Text(transformer="X_YX")
    private String faculty;
    
    /** 研究生课程性质取值 */
    @Code2Text(transformer="X_KCXZ")
    private String nature;
    
    /** 本科生课程性质取值 */
    @Code2Text(transformer="K_BKKCXZ")
    private String isElective;
    
    /**
     * 课程层次
     */
    @Code2Text(transformer = "X_PYCC")
    private String trainingLevel;
    
    /**
     * 考核方式（从字典取值 X_KSLX）
     */
    @Code2Text(transformer = "X_KSLX")
    private String assessmentMode;
    
    
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
	public String getCollege() {
		return college;
	}
	public void setCollege(String college) {
		this.college = college;
	}
	public String getNature() {
		return nature;
	}
	public void setNature(String nature) {
		this.nature = nature;
	}
	public String getTrainingLevel() {
		return trainingLevel;
	}
	public void setTrainingLevel(String trainingLevel) {
		this.trainingLevel = trainingLevel;
	}
	public String getIsElective() {
		return isElective;
	}
	public void setIsElective(String isElective) {
		this.isElective = isElective;
	}
	public String getFaculty() {
		return faculty;
	}
	public void setFaculty(String faculty) {
		this.faculty = faculty;
	}
	public String getAssessmentMode() {
		return assessmentMode;
	}
	public void setAssessmentMode(String assessmentMode) {
		this.assessmentMode = assessmentMode;
	}
	
}
