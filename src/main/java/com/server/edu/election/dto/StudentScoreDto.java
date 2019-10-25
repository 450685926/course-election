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
	private Integer pass;
	
	private Long calendarId;
	
    /**
     * 学年
     */
    private Long academicYear;

    /**
     * 学期
     */
    private Long semester;
    

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

	public Integer getPass() {
		return pass;
	}

	public void setPass(Integer pass) {
		this.pass = pass;
	}

	public Long getCalendarId() {
		return calendarId;
	}

	public void setCalendarId(Long calendarId) {
		this.calendarId = calendarId;
	} 
	
	

}
