package com.server.edu.election.vo;

public class AllCourseVo {
	/**轮次ID*/
	private Long roundId;
	
	/**学年学期*/
	private Long calendarId;
	
	/**课程代码*/
    private String courseCode;
    
    /** 授课教师编号 */
    private String teacherCode;
    
	/** 开课学院 */
	private String faculty;
	
	/** 课程类别（课程性质） */
	private String natrue;
	
	/** 培养层次 */
	private String trainingLevel;
	
	/** 学生学籍所在校区 */
	private String campu;

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

	public String getNatrue() {
		return natrue;
	}

	public void setNatrue(String natrue) {
		this.natrue = natrue;
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
	
	
}
