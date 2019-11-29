package com.server.edu.mutual.vo;

import java.sql.Date;

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
	private Date applyAt;
	
	/**
	 * 审核状态
	 */
	private Integer status;
	
	private String projectId;

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public Date getApplyAt() {
		return applyAt;
	}

	public void setApplyAt(Date applyAt) {
		this.applyAt = applyAt;
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
	
}
