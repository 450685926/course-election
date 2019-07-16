package com.server.edu.election.studentelec.context;

import java.util.List;
import com.server.edu.dictionary.annotation.CodeI18n;

/**
 * 选课返回结果信息
 * @author xlluoc
 */
@CodeI18n
public class ElcCourseResult{
	/** 课程类别（课程性质） */
	private String natrue;
	
	/** 教学班主键ID */
	private Long teachClassId;

	/** 教学班编号（课程序号） */
	private String  teachingClassCode;
	
	/**课程代码*/
    private String courseCode;
    
    /**课程名称*/
    private String courseName;

    /**学分*/
    private Double credits;

    /** 授课教师编号 */
    private String teacherCode;
    
    /** 授课教师名称 */
    private String teacherName;
    
	/** 开课学院 */
	private String faculty;
	
	/** 实际选课人数 */
	private Integer elcNumber;
	
	/** 开班人数 */
	private Integer number;

	/** 教学班备注信息 */
	private String remark;
	
	/** 上课时间地点 */
	//private List<TimeTableMessage> timeTableList;
	private List<TimeAndRoom> timeTableList;
	
    /** 上课时间按教学周拆分集合 */
    private List<ClassTimeUnit> times;
	
	/** 是否冲突（0-不冲突；1-冲突） */
	private Integer isConflict;

	public String getNatrue() {
		return natrue;
	}

	public void setNatrue(String natrue) {
		this.natrue = natrue;
	}

	public String getTeacherCode() {
		return teacherCode;
	}

	public void setTeacherCode(String teacherCode) {
		this.teacherCode = teacherCode;
	}

	public String getTeacherName() {
		return teacherName;
	}

	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}

	public String getTeachingClassCode() {
		return teachingClassCode;
	}

	public void setTeachingClassCode(String teachingClassCode) {
		this.teachingClassCode = teachingClassCode;
	}

	public Long getTeachClassId() {
		return teachClassId;
	}

	public void setTeachClassId(Long teachClassId) {
		this.teachClassId = teachClassId;
	}

	public Integer getElcNumber() {
		return elcNumber;
	}

	public void setElcNumber(Integer elcNumber) {
		this.elcNumber = elcNumber;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getIsConflict() {
		return isConflict;
	}

	public void setIsConflict(Integer isConflict) {
		this.isConflict = isConflict;
	}

	public List<TimeAndRoom> getTimeTableList() {
		return timeTableList;
	}

	public void setTimeTableList(List<TimeAndRoom> timeTableList) {
		this.timeTableList = timeTableList;
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

	public Double getCredits() {
		return credits;
	}

	public void setCredits(Double credits) {
		this.credits = credits;
	}

	public String getFaculty() {
		return faculty;
	}

	public void setFaculty(String faculty) {
		this.faculty = faculty;
	}

	public List<ClassTimeUnit> getTimes() {
		return times;
	}

	public void setTimes(List<ClassTimeUnit> times) {
		this.times = times;
	}
	
	
}
