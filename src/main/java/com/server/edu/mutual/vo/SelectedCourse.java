package com.server.edu.mutual.vo;

import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.election.studentelec.cache.TeachingClassCache;

/**
 * 已选择课程
 */
@CodeI18n
public class SelectedCourse {
	private TeachingClassCache course;

	private String courseCode;

	private String courseName;

	private Double credits;

	/** 是否必修 */
	private String compulsory;

	/**
	 * 选课对象(1学生，2教务员，3管理员)
	 */
	private Integer chooseObj;

	/**
	 * 修读类别(1正常修读,2重修,3免修不免考,4免修)
	 */
	@Code2Text(DictTypeEnum.X_XDLX)
	private Integer courseTakeType;

	@Code2Text(DictTypeEnum.X_KCFL)
	private String label;

	@Code2Text(transformer = " X_KSLX")
	private String assessmentMode;
	
	/**
	 * 第几轮
	 */
	private Integer turn;

	private Integer isApply;

	/**
	 * 选课状态(1-已选；0-未选)
	 */
	private Integer status;

	public SelectedCourse() {

	}

	public SelectedCourse(TeachingClassCache teachingClass) {
		this.course = teachingClass;
	}

	public TeachingClassCache getCourse() {
		if (course == null) {
			course = new TeachingClassCache();
		}
		return course;
	}

	public void setCourse(TeachingClassCache course) {
		this.course = course;
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

	public String getCompulsory() {
		return compulsory;
	}

	public void setCompulsory(String compulsory) {
		this.compulsory = compulsory;
	}

	public Integer getChooseObj() {
		return chooseObj;
	}

	public void setChooseObj(Integer chooseObj) {
		this.chooseObj = chooseObj;
	}

	public Integer getCourseTakeType() {
		return courseTakeType;
	}

	public void setCourseTakeType(Integer courseTakeType) {
		this.courseTakeType = courseTakeType;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getAssessmentMode() {
		return assessmentMode;
	}

	public void setAssessmentMode(String assessmentMode) {
		this.assessmentMode = assessmentMode;
	}

	public Integer getTurn() {
		return turn;
	}

	public void setTurn(Integer turn) {
		this.turn = turn;
	}

	public Integer getIsApply() {
		return isApply;
	}

	public void setIsApply(Integer isApply) {
		this.isApply = isApply;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		return this.getCourse().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof SelectedCourse) {
			SelectedCourse o = (SelectedCourse) obj;
			if (this.course == o.getCourse()) {
				return true;
			}
			return this.course != null && this.course.getTeachClassId() != null
					&& this.getCourse().equals(o.getCourse());
		}
		return false;
	}
}
