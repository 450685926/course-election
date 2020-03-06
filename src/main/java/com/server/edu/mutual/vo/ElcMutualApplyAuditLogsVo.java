package com.server.edu.mutual.vo;

import java.sql.Date;
import java.sql.Timestamp;

import com.server.edu.mutual.entity.ElcMutualApplyAuditLogs;

public class ElcMutualApplyAuditLogsVo extends ElcMutualApplyAuditLogs{
	private static final long serialVersionUID = 1L;
	
	/**
	 * 学生学号
	 */
	private String studentId;
	
	/**
	 * 申请时间
	 */
	private Timestamp applyAt;
	
	/**
	 * 审核状态
	 */
	private Integer status;
	
	private String projectId;

	//课程号
	private String courseCode;

	//学期
	private Long calendarId;

	public Timestamp getApplyAt() {
		return applyAt;
	}

	public void setApplyAt(Timestamp applyAt) {
		this.applyAt = applyAt;
	}

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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

	public Long getCalendarId() {
		return calendarId;
	}

	public void setCalendarId(Long calendarId) {
		this.calendarId = calendarId;
	}

	@Override
	public String toString() {
		return "ElcMutualApplyAuditLogsVo{" +
				"studentId='" + studentId + '\'' +
				", applyAt=" + applyAt +
				", status=" + status +
				", projectId='" + projectId + '\'' +
				", courseCode='" + courseCode + '\'' +
				", calendarId='" + calendarId + '\'' +
				'}';
	}
}
