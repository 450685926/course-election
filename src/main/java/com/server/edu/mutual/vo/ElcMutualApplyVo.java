package com.server.edu.mutual.vo;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.mutual.entity.ElcMutualApply;

@CodeI18n
public class ElcMutualApplyVo extends ElcMutualApply {
    private static final long serialVersionUID = 1L;
    
    private String courseId;

    private String courseCode;
    
    private String courseName;
    
    private Double credits;
    
    /**
     * 开课学院（课程所在学院）
     */
    @Code2Text(transformer="X_YX")
    private String openCollege;
    
    /**
     * 学生学院（学生所在行政学院）
     */
    @Code2Text(transformer="X_YX")
    private String college;
    
    /** 研究生课程性质 */
    @Code2Text(transformer="X_KCXZ")
    private String nature;
    
    /** 本科生课程性质 */
    @Code2Text(transformer="K_BKKCXZ")
    private String isElective;
    
    @Code2Text(transformer="X_KSLX")
    private String assessmentMode; 
    /**
     * 课程层次
     */
    @Code2Text(transformer = "X_PYCC")
    private String courseTrainingLevel;
    
    /**
     * 学生类别
     */
    private String studentCategory;
    
    /**
     * 姓名
     */
    private String studentName;
    
    /**
     * 培养层次(专科   本科   硕士   博士    其他    预科)
     */
    @Code2Text(transformer = "X_PYCC")
    private String stuTrainingLevel;
    
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
    
    /**
     * 申请课程人数
     */
    private Integer stuNumber;
    
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public String getStuTrainingLevel() {
		return stuTrainingLevel;
	}
	public void setStuTrainingLevel(String stuTrainingLevel) {
		this.stuTrainingLevel = stuTrainingLevel;
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
	
	public String getOpenCollege() {
		return openCollege;
	}
	public void setOpenCollege(String openCollege) {
		this.openCollege = openCollege;
	}
	public String getNature() {
		return nature;
	}
	public void setNature(String nature) {
		this.nature = nature;
	}
	public String getAssessmentMode() {
		return assessmentMode;
	}
	public void setAssessmentMode(String assessmentMode) {
		this.assessmentMode = assessmentMode;
	}
	public String getCourseTrainingLevel() {
		return courseTrainingLevel;
	}
	public void setCourseTrainingLevel(String courseTrainingLevel) {
		this.courseTrainingLevel = courseTrainingLevel;
	}
	public String getCourseId() {
		return courseId;
	}
	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}
	public String getIsElective() {
		return isElective;
	}
	public void setIsElective(String isElective) {
		this.isElective = isElective;
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
    public void setStudentCategory(String studentCategory)
    {
        this.studentCategory =
            studentCategory == null ? null : studentCategory.trim();
    }
	public Integer getStuNumber() {
		return stuNumber;
	}
	public void setStuNumber(Integer stuNumber) {
		this.stuNumber = stuNumber;
	}
	
}
