package com.server.edu.election.dto;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 教师课表
 * @author: bear
 * @create: 2019-05-05 13:52
 */
@CodeI18n
public class TeacherTimeTable implements Serializable {
    private Long teachingClassId;
    private String classCode;
    private String courseCdoe;
    private String courseName;
    private Long courseLabel;
    private String courseLabelName;
    private Double weekHour;
    private Double credits;
    @Code2Text(transformer = "X_SKYY")
    private String teachingLanguage;
    private String classTime;
    private String classRoom;
    private String remark;
    private Integer elcNumber;

    public Long getCourseLabel() {
        return courseLabel;
    }

    public void setCourseLabel(Long courseLabel) {
        this.courseLabel = courseLabel;
    }

    public Long getTeachingClassId() {
        return teachingClassId;
    }

    public void setTeachingClassId(Long teachingClassId) {
        this.teachingClassId = teachingClassId;
    }

    private List<TimeTableMessage> timeTableList;

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getCourseCdoe() {
        return courseCdoe;
    }

    public void setCourseCdoe(String courseCdoe) {
        this.courseCdoe = courseCdoe;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseLabelName() {
        return courseLabelName;
    }

    public void setCourseLabelName(String courseLabelName) {
        this.courseLabelName = courseLabelName;
    }

    public Double getWeekHour() {
        return weekHour;
    }

    public void setWeekHour(Double weekHour) {
        this.weekHour = weekHour;
    }

    public Double getCredits() {
        return credits;
    }

    public void setCredits(Double credits) {
        this.credits = credits;
    }

    public String getTeachingLanguage() {
        return teachingLanguage;
    }

    public void setTeachingLanguage(String teachingLanguage) {
        this.teachingLanguage = teachingLanguage;
    }

    public String getClassTime() {
        return classTime;
    }

    public void setClassTime(String classTime) {
        this.classTime = classTime;
    }

    public String getClassRoom() {
        return classRoom;
    }

    public void setClassRoom(String classRoom) {
        this.classRoom = classRoom;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getElcNumber() {
        return elcNumber;
    }

    public void setElcNumber(Integer elcNumber) {
        this.elcNumber = elcNumber;
    }

    public List<TimeTableMessage> getTimeTableList() {
        return timeTableList;
    }

    public void setTimeTableList(List<TimeTableMessage> timeTableList) {
        this.timeTableList = timeTableList;
    }
}
