package com.server.edu.election.dto;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

public class ElectionApplyRejectDto {
	
    /**
     * 学号
     */
	@NotBlank
    private String studentId;
    
	@NotNull
    private Long roundId;
    
    /**
     * 课程代码
     */
	@NotBlank
    private String courseCode;
    
    private String remark;
    

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

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
    
    

}
