package com.server.edu.election.vo;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;

import java.util.List;

@CodeI18n
public class RebuildCourseVo {
    private Long calendarId;

    private Long teachingClassId;

    private String courseCode;

    private String classCode;

    private String courseName;

    private Double credits;

    private String teacherName;

    @Code2Text(transformer="X_YX")
    private String faculty;

    /**校区*/
    @Code2Text(transformer = "X_XQ")
    private String campus;

    private List<String> courseArrange;

    private int selectNumber;

    private int number;

    private String remark;

    /**null为不能操作，0选课，1退课*/
    private Integer status;

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public Long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
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

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public List<String> getCourseArrange() {
        return courseArrange;
    }

    public void setCourseArrange(List<String> courseArrange) {
        this.courseArrange = courseArrange;
    }

    public int getSelectNumber() {
        return selectNumber;
    }

    public void setSelectNumber(int selectNumber) {
        this.selectNumber = selectNumber;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
