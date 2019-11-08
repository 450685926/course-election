package com.server.edu.election.dto;

import java.util.List;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.Code2Text.DataType;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.dictionary.translator.ClassRoomTranslator;

/**
 * @description: 学生课表
 * @author: bear
 * @create: 2019-04-30 11:40
 */
@CodeI18n
public class StudnetTimeTable{
    private Long teachingClassId;
    private String classCode;
    private String className;
    @Code2Text(transformer = "X_XQ")
    private String campus;
    private String courseCode;
    private String courseName;
    private String courseType;
    private String assessmentMode;
    private String isExemptionCourse;
    private Double credits;
    private String teacherName;
    private String classTime;
    @Code2Text(translator = ClassRoomTranslator.class, dataType = DataType.SPLIT)
    private String classRoom;
    private String remark;
    private List<TimeTableMessage> timeTableList;
    // 是否必修课，1是，0或者null否
    private String compulsory;

    public String getCompulsory() {
        return compulsory;
    }

    public void setCompulsory(String compulsory) {
        this.compulsory = compulsory;
    }

    public Long getTeachingClassId() {
        return teachingClassId;
    }

    public void setTeachingClassId(Long teachingClassId) {
        this.teachingClassId = teachingClassId;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
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

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
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
