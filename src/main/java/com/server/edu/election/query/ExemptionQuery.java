package com.server.edu.election.query;

/**
 * 免修免考查询
 * @author qiangliz
 *
 */
public class ExemptionQuery
{
	/**
	 * 学生id
	 */
	private String studentId;
	
	/**入学季节*/
	private String enrolSeason;
	
	/**年级*/
	private String grade;
	
	/**培养层次*/
	private String trainingLevel;
    
    /**培养类别*/
    private String trainingCategory;
    
    /**学位类型*/
    private String degreeType;
    
    /**学习形式*/
    private String formLearning;
    
    /**课程编码*/
    private String courseCode;
    
    /**课程名称*/
    private String courseName;
    
    
    private String projectId;
    
    /** 姓名 */
    private String studentName;
    
    /** 审核结果 */
    private Integer examineResult;
    
    /** 审核结果 */
    private String faculty;
    
	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public String getEnrolSeason() {
		return enrolSeason;
	}

	public void setEnrolSeason(String enrolSeason) {
		this.enrolSeason = enrolSeason;
	}

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

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
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

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public Integer getExamineResult() {
		return examineResult;
	}

	public void setExamineResult(Integer examineResult) {
		this.examineResult = examineResult;
	}

	public String getFaculty() {
		return faculty;
	}

	public void setFaculty(String faculty) {
		this.faculty = faculty;
	}
	
}
