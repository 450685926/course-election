package com.server.edu.election.dto;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 学生课表
 * @author: bear
 * @create: 2019-04-30 11:40
 */
public class StudnetTimeTable implements Serializable{
    private Long teachingClassId;
    private Long classCode;
    private String className;
    private String campus;
    private String courseCode;
    private String courseName;
    private String isRebulidCourse;
    private String assessmentMode;
    private String isExemptionCourse;
    private Double credits;
    private String teacherName;
    private String classTime;
    private String classRoom;
    private String remark;
    private List<TimeTableMessage> timeTableList;

    public Long getTeachingClassId() {
        return teachingClassId;
    }

    public void setTeachingClassId(Long teachingClassId) {
        this.teachingClassId = teachingClassId;
    }

    public Long getClassCode() {
        return classCode;
    }

    public void setClassCode(Long classCode) {
        this.classCode = classCode;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
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

    public String getIsRebulidCourse() {
        return isRebulidCourse;
    }

    public void setIsRebulidCourse(String isRebulidCourse) {
        this.isRebulidCourse = isRebulidCourse;
    }

    public String getAssessmentMode() {
        return assessmentMode;
    }

    public void setAssessmentMode(String assessmentMode) {
        this.assessmentMode = assessmentMode;
    }

    public String getIsExemptionCourse() {
        return isExemptionCourse;
    }

    public void setIsExemptionCourse(String isExemptionCourse) {
        this.isExemptionCourse = isExemptionCourse;
    }

    public Double getCredits() {
        return credits;
    }

    public void setCredits(Double credits) {
        this.credits = credits;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
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

    public List<TimeTableMessage> getTimeTableList() {
        return timeTableList;
    }

    public void setTimeTableList(List<TimeTableMessage> timeTableList) {
        this.timeTableList = timeTableList;
    }
}
