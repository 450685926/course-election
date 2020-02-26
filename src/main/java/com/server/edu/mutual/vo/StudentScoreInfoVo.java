package com.server.edu.mutual.vo;

import java.util.List;

import com.server.edu.common.entity.StudentScore;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;

@CodeI18n
public class StudentScoreInfoVo extends StudentScore {

	// 补考成绩
	private String makeupScore;
	// 缓考成绩
	private String slowScore;
	// 校历名称
	private String calendar;
	// 课程类别
	private String courseLabel;
	// 课程类别名称
	private String courseLabelName;
	// 学院
	@Code2Text(transformer = "X_YX")
	private String faculty;
	// 培养层次
	@Code2Text(transformer = "X_PYCC")
	private String trainingLevel;
	// 课程序号
	private String courseNum;
	// 录入人
	private String enterPerson;
	// 成绩构成
	private List<ScoreTypeVo> scoreTypeList;
	// 是否选课
	private String isElcCourse;
	// 是否及格
	private String isPassCn;
	// 录入时间
	private String enterTime;

	private String teachingClassIdNew;

	private String remarkPk;

	private String specialScore;
	// 课程学分
	private Double courseCredit;

	public String getRemarkPk() {
		return remarkPk;
	}

	public void setRemarkPk(String remarkPk) {
		this.remarkPk = remarkPk;
	}

	public String getMakeupScore() {
		return makeupScore;
	}

	public void setMakeupScore(String makeupScore) {
		this.makeupScore = makeupScore;
	}

	public String getSlowScore() {
		return slowScore;
	}

	public void setSlowScore(String slowScore) {
		this.slowScore = slowScore;
	}

	public String getCalendar() {
		return calendar;
	}

	public void setCalendar(String calendar) {
		this.calendar = calendar;
	}

	public String getCourseLabel() {
		return courseLabel;
	}

	public void setCourseLabel(String courseLabel) {
		this.courseLabel = courseLabel;
	}

	public String getCourseLabelName() {
		return courseLabelName;
	}

	public void setCourseLabelName(String courseLabelName) {
		this.courseLabelName = courseLabelName;
	}

	public String getFaculty() {
		return faculty;
	}

	public void setFaculty(String faculty) {
		this.faculty = faculty;
	}

	public String getTrainingLevel() {
		return trainingLevel;
	}

	public void setTrainingLevel(String trainingLevel) {
		this.trainingLevel = trainingLevel;
	}

	public String getCourseNum() {
		return courseNum;
	}

	public void setCourseNum(String courseNum) {
		this.courseNum = courseNum;
	}

	public String getEnterPerson() {
		return enterPerson;
	}

	public void setEnterPerson(String enterPerson) {
		this.enterPerson = enterPerson;
	}

	public List<ScoreTypeVo> getScoreTypeList() {
		return scoreTypeList;
	}

	public void setScoreTypeList(List<ScoreTypeVo> scoreTypeList) {
		this.scoreTypeList = scoreTypeList;
	}

	public String getIsElcCourse() {
		return isElcCourse;
	}

	public void setIsElcCourse(String isElcCourse) {
		this.isElcCourse = isElcCourse;
	}

	public String getIsPassCn() {
		return isPassCn;
	}

	public void setIsPassCn(String isPassCn) {
		this.isPassCn = isPassCn;
	}

	public String getEnterTime() {
		return enterTime;
	}

	public void setEnterTime(String enterTime) {
		this.enterTime = enterTime;
	}

	public String getTeachingClassIdNew() {
		return teachingClassIdNew;
	}

	public void setTeachingClassIdNew(String teachingClassIdNew) {
		this.teachingClassIdNew = teachingClassIdNew;
	}

	public Double getCourseCredit() {
		return courseCredit;
	}

	public void setCourseCredit(Double courseCredit) {
		this.courseCredit = courseCredit;
	}

	@Override
	public String getSpecialScore() {
		return specialScore;
	}

	@Override
	public void setSpecialScore(String specialScore) {
		this.specialScore = specialScore;
	}
}
