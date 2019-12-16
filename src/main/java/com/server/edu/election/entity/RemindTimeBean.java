package com.server.edu.election.entity;

import org.apache.commons.lang.StringUtils;

import javax.persistence.Column;
import java.io.Serializable;

public class RemindTimeBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3080217197677966960L;

	/** 学年学期 */
	@Column(name = "CALENDAR_ID_")
	private Long calendarId;

	@Column(name = "STUDENT_ID_")
	private String studentId;

	@Column(name = "STUDENT_NAME_")
	private String studentName;

	@Column(name = "EMAIL_")
	private String studentEmail;

	@Column(name = "TEACHER_ID_")
	private String teacherId;

	@Column(name = "TEACHER_NAME_")
	private String teacherName;

	@Column(name = "EMAIL_")
	private String teacherEmail;

	@Column(name = "REMIND_TIME_")
	private String remindTime;

	@Column(name = "ASSESS_START_TIME_")
	private String assessStartTime;

	@Column(name = "APPLY_END_TIME_")
	private String applyEndTime;

	@Column(name = "ASSESS_END_TIME_")
	private String assessEndTime;

	private String courseNameAndCode;

	public Long getCalendarId() {
		return calendarId;
	}

	public void setCalendarId(Long calendarId) {
		this.calendarId = calendarId;
	}

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public String getStudentEmail() {
		return studentEmail;
	}

	public void setStudentEmail(String studentEmail) {
		this.studentEmail = studentEmail;
	}

	public String getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(String teacherId) {
		this.teacherId = teacherId;
	}

	public String getTeacherName() {
		return teacherName;
	}

	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}

	public String getTeacherEmail() {
		return teacherEmail;
	}

	public void setTeacherEmail(String teacherEmail) {
		this.teacherEmail = teacherEmail;
	}

	public String getRemindTime() {
		if (StringUtils.isNotBlank(remindTime)) {
			remindTime = remindTime.replace(".0", "");
		}
		return remindTime;
	}

	public void setRemindTime(String remindTime) {
		this.remindTime = remindTime;
	}

	public String getAssessStartTime() {
		if (StringUtils.isNotBlank(assessStartTime)) {
			assessStartTime = assessStartTime.replace(".0", "");
		}
		return assessStartTime;
	}

	public void setAssessStartTime(String assessStartTime) {
		this.assessStartTime = assessStartTime;
	}

	public String getApplyEndTime() {
		if (StringUtils.isNotBlank(applyEndTime)) {
			applyEndTime = applyEndTime.replace(".0", "");
		}
		return applyEndTime;
	}

	public void setApplyEndTime(String applyEndTime) {
		this.applyEndTime = applyEndTime;
	}

	public String getAssessEndTime() {
		if (StringUtils.isNotBlank(assessEndTime)) {
			assessEndTime = assessEndTime.replace(".0", "");
		}
		return assessEndTime;
	}

	public void setAssessEndTime(String assessEndTime) {
		this.assessEndTime = assessEndTime;
	}

	@Override
	public String toString() {
		return "RemindTimeBean [calendarId=" + calendarId + ", studentId=" + studentId + ", studentName=" + studentName
				+ ", studentEmail=" + studentEmail + ", teacherId=" + teacherId + ", teacherName=" + teacherName
				+ ", teacherEmail=" + teacherEmail + ", remindTime=" + remindTime + ", assessStartTime="
				+ assessStartTime + ", applyEndTime=" + applyEndTime + ", assessEndTime=" + assessEndTime + "]";
	}

	public String getCourseNameAndCode() {
		return courseNameAndCode;
	}

	public void setCourseNameAndCode(String courseNameAndCode) {
		this.courseNameAndCode = courseNameAndCode;
	}
}
