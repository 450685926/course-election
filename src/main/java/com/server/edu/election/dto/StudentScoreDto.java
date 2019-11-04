package com.server.edu.election.dto;

public class StudentScoreDto {
	private String studentId;
    /**
     * 得优(1.是 0否)
     */
	private Integer isBest;
	
    /**
     * 通过(1.是 0否)
     */
	private Integer isPass;
	
	private Long calendarId;
	
    /**
     * 学年
     */
    private Long academicYear;

    /**
     * 学期
     */
    private Long semester;
    
    /**
     * 成绩
     */
    private String score;
    
    
    

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public Long getAcademicYear() {
		return academicYear;
	}

	public void setAcademicYear(Long academicYear) {
		this.academicYear = academicYear;
	}

	public Long getSemester() {
		return semester;
	}

	public void setSemester(Long semester) {
		this.semester = semester;
	}

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public Integer getIsBest() {
		return isBest;
	}

	public void setIsBest(Integer isBest) {
		this.isBest = isBest;
	}


	public Integer getIsPass() {
		return isPass;
	}

	public void setIsPass(Integer isPass) {
		this.isPass = isPass;
	}

	public Long getCalendarId() {
		return calendarId;
	}

	public void setCalendarId(Long calendarId) {
		this.calendarId = calendarId;
	} 
	
	

}
