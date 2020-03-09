package com.server.edu.mutual.voutil;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;

@CodeI18n
public class CourseScoreChangeVo {
	//教学班ID
	private String id;
	//课程代码
	private String courseCode;
	//课程名称
	private String courseName;
	//课程序号
	private String courseNum;
	//教学班
	private String teachingClass;
	//校区
	@Code2Text(transformer="X_XQ")
	private String schoolZone;
	//其他录入人
	private String otherEnter;
	//人数
	private Integer number;
	//学分
	private String credit;
	//教师
	private String teacher;
	//学院
	@Code2Text(transformer="X_YX")
	private String college;
	//学时
	private String  period;
	//变更类型（学生/课程）
    private Integer changeType;
    //OA文号
    private String oaCode;
    //附件路径
    private String attachmentPath;
    //申请原因
    private String applyReason;
    //审核状态
    private Integer auditStatus;
    //电子流审批类型
    private String type;
    //变更申请ID
    private String changeId;
    //发布状态
    @Code2Text(transformer="X_CJFBZT")
    private String releaseType;
    //学院代码
    private String faculty;
    //申请编号
    private String appNo;
    //申请日期
    private String applyDate;
    //录入时间
    private String enterTime;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public String getCourseNum() {
		return courseNum;
	}
	public void setCourseNum(String courseNum) {
		this.courseNum = courseNum;
	}
	public String getTeachingClass() {
		return teachingClass;
	}
	public void setTeachingClass(String teachingClass) {
		this.teachingClass = teachingClass;
	}
	public String getSchoolZone() {
		return schoolZone;
	}
	public void setSchoolZone(String schoolZone) {
		this.schoolZone = schoolZone;
	}
	public String getOtherEnter() {
		return otherEnter;
	}
	public void setOtherEnter(String otherEnter) {
		this.otherEnter = otherEnter;
	}
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	public String getCredit() {
		return credit;
	}
	public void setCredit(String credit) {
		this.credit = credit;
	}
	public String getTeacher() {
		return teacher;
	}
	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}
	public String getCollege() {
		return college;
	}
	public void setCollege(String college) {
		this.college = college;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public Integer getChangeType() {
		return changeType;
	}
	public void setChangeType(Integer changeType) {
		this.changeType = changeType;
	}
	public String getOaCode() {
		return oaCode;
	}
	public void setOaCode(String oaCode) {
		this.oaCode = oaCode;
	}
	public String getAttachmentPath() {
		return attachmentPath;
	}
	public void setAttachmentPath(String attachmentPath) {
		this.attachmentPath = attachmentPath;
	}
	public String getApplyReason() {
		return applyReason;
	}
	public void setApplyReason(String applyReason) {
		this.applyReason = applyReason;
	}
	public Integer getAuditStatus() {
		return auditStatus;
	}
	public void setAuditStatus(Integer auditStatus) {
		this.auditStatus = auditStatus;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getChangeId() {
		return changeId;
	}
	public void setChangeId(String changeId) {
		this.changeId = changeId;
	}
	public String getReleaseType() {
		return releaseType;
	}
	public void setReleaseType(String releaseType) {
		this.releaseType = releaseType;
	}
	public String getFaculty() {
		return faculty;
	}
	public void setFaculty(String faculty) {
		this.faculty = faculty;
	}
	public String getAppNo() {
		return appNo;
	}
	public void setAppNo(String appNo) {
		this.appNo = appNo;
	}
	public String getApplyDate() {
		return applyDate;
	}
	public void setApplyDate(String applyDate) {
		this.applyDate = applyDate;
	}
	public String getEnterTime() {
		return enterTime;
	}
	public void setEnterTime(String enterTime) {
		this.enterTime = enterTime;
	}
}
