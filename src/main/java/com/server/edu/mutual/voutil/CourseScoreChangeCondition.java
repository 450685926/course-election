package com.server.edu.mutual.voutil;

import java.util.List;

public class CourseScoreChangeCondition {
	//学期
	private String calendarId;
	//年级
	private String  yearGrade;
	//校区
	private String schoolZone;
	//培养层次
	private String trainingLevel; 
	//开课学院
	private String openCollege;
	//专业
	private String profession;
	//检索文本
	private String  searchText;
	//学院
	private String college;
	//学习类别
	private String learnType;
	//发布状态
	private Integer releaseType;
	//教学班ID
	private Long teachingClassId;
	//课程代码
	private String  courseCode;
	//课程性质
	private String  courseNature;
    //课程名称
	private String  courseName;
	//教学班名称
	private String  teachingClass;
	//教师名称
	private String  teacherName;
	//管理部门Id
	private String  managerDeptId;
	//教师工号
    private String  teacherUserId;
    //课程序号
    private String  courseNum;
    //审核状态
    private String  auditStatus;
    //工作流待审核列表
    List<String> flowList;
    //数据分权的包含的部门
    private List<String> deptIds;
	//课程代码
	private List<String>  courseCodeList;

	public List<String> getCourseCodeList() {
		return courseCodeList;
	}

	public void setCourseCodeList(List<String> courseCodeList) {
		this.courseCodeList = courseCodeList;
	}

	public String getCourseNature() {
		return courseNature;
	}

	public void setCourseNature(String courseNature) {
		this.courseNature = courseNature;
	}

	public String getCalendarId() {
		return calendarId;
	}
	public void setCalendarId(String calendarId) {
		this.calendarId = calendarId;
	}
	public String getYearGrade() {
		return yearGrade;
	}
	public void setYearGrade(String yearGrade) {
		this.yearGrade = yearGrade;
	}
	public String getSchoolZone() {
		return schoolZone;
	}
	public void setSchoolZone(String schoolZone) {
		this.schoolZone = schoolZone;
	}
	public String getTrainingLevel() {
		return trainingLevel;
	}
	public void setTrainingLevel(String trainingLevel) {
		this.trainingLevel = trainingLevel;
	}
	public String getOpenCollege() {
		return openCollege;
	}
	public void setOpenCollege(String openCollege) {
		this.openCollege = openCollege;
	}
	public String getProfession() {
		return profession;
	}
	public void setProfession(String profession) {
		this.profession = profession;
	}
	public String getSearchText() {
		return searchText;
	}
	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}
	public String getCollege() {
		return college;
	}
	public void setCollege(String college) {
		this.college = college;
	}
	public String getLearnType() {
		return learnType;
	}
	public void setLearnType(String learnType) {
		this.learnType = learnType;
	}
	public Integer getReleaseType() {
		return releaseType;
	}
	public void setReleaseType(Integer releaseType) {
		this.releaseType = releaseType;
	}
	public Long getTeachingClassId() {
		return teachingClassId;
	}
	public void setTeachingClassId(Long teachingClassId) {
		this.teachingClassId = teachingClassId;
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
	public String getTeachingClass() {
		return teachingClass;
	}
	public void setTeachingClass(String teachingClass) {
		this.teachingClass = teachingClass;
	}
	public String getTeacherName() {
		return teacherName;
	}
	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}
	public String getManagerDeptId() {
		return managerDeptId;
	}
	public void setManagerDeptId(String managerDeptId) {
		this.managerDeptId = managerDeptId;
	}
	public String getTeacherUserId() {
		return teacherUserId;
	}
	public void setTeacherUserId(String teacherUserId) {
		this.teacherUserId = teacherUserId;
	}
	public String getCourseNum() {
		return courseNum;
	}
	public void setCourseNum(String courseNum) {
		this.courseNum = courseNum;
	}
	public String getAuditStatus() {
		return auditStatus;
	}
	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}
	public List<String> getFlowList() {
		return flowList;
	}
	public void setFlowList(List<String> flowList) {
		this.flowList = flowList;
	}
	public List<String> getDeptIds() {
		return deptIds;
	}
	public void setDeptIds(List<String> deptIds) {
		this.deptIds = deptIds;
	}

}
