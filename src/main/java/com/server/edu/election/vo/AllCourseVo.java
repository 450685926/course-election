package com.server.edu.election.vo;

public class AllCourseVo {
	/**轮次ID*/
	private Long roundId;
	
	/**学年学期*/
	private Long calendarId;
	
	/**课程代码*/
    private String courseCode;
    
    /**课程名称*/
    private String courseName;
    
    /** 授课教师编号 */
    private String teacherCode;
    
    /** 授课教师名称 */
    private String teacherName;

    /** 开课学院 */
	private String faculty;
	
	/** 课程类别（课程性质） */
	private String nature;
	
	/** 培养层次 */
	private String trainingLevel;
	
	/** 学生学籍所在校区 */
	private String campu;
	
	/** 学生学号  */
	private String studentCode;
	
	/** 教学班编号（课程序号） */
    private String teachClassCode;  
	
	public Long getRoundId() {
		return roundId;
	}

	public void setRoundId(Long roundId) {
		this.roundId = roundId;
	}

	public String getCourseCode() {
		return courseCode;
	}

	public void setCourseCode(String courseCode) {
		this.courseCode = courseCode;
	}

	public String getTeacherCode() {
		return teacherCode;
	}

	public void setTeacherCode(String teacherCode) {
		this.teacherCode = teacherCode;
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

	public Long getCalendarId() {
		return calendarId;
	}

	public void setCalendarId(Long calendarId) {
		this.calendarId = calendarId;
	}

	public String getCampu() {
		return campu;
	}

	public void setCampu(String campu) {
		this.campu = campu;
	}

	public String getFaculty() {
		return faculty;
	}

	public void setFaculty(String faculty) {
		this.faculty = faculty;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getTeacherName() {
		return teacherName;
	}

	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}

	public String getStudentCode() {
		return studentCode;
	}

	public void setStudentCode(String studentCode) {
		this.studentCode = studentCode;
	}

	public String getTeachClassCode() {
		return teachClassCode;
	}

	public void setTeachClassCode(String teachClassCode) {
		this.teachClassCode = teachClassCode;
	}
	
}
