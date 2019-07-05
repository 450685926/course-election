package com.server.edu.election.vo;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;

/**
 * @description: 点名册
 * @author: bear
 * @create: 2019-02-14 17:54
 */
@CodeI18n
public class RollBookList {
    private Long id;
    private Long calendarId;
    private Long teachingClassId;
    private String classCode;
    private String className;
    private String courseCode;
    private String courseName;
    private String courseLabel;
    private Integer selectCourseNumber;
    private Integer numberLimit;
    private String calendarName;
    private String classCodeAndcourseName;
    private String courseNature;

    public String getCourseNature() {
        return courseNature;
    }

    public void setCourseNature(String courseNature) {
        this.courseNature = courseNature;
    }

    public String getClassCodeAndcourseName() {
        return classCodeAndcourseName;
    }

    public void setClassCodeAndcourseName(String classCodeAndcourseName) {
        this.classCodeAndcourseName = classCodeAndcourseName;
    }

    public String getCalendarName() {
        return calendarName;
    }

    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }

    @Code2Text(transformer="X_YX")
    private String faculty;
    private String teacherName;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTeachingClassId() {
        return teachingClassId;
    }

    public void setTeachingClassId(Long teachingClassId) {
        this.teachingClassId = teachingClassId;
    }

    public Long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
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

    public String getCourseLabel() {
        return courseLabel;
    }

    public void setCourseLabel(String courseLabel) {
        this.courseLabel = courseLabel;
    }

    public Integer getSelectCourseNumber() {
        return selectCourseNumber;
    }

    public void setSelectCourseNumber(Integer selectCourseNumber) {
        this.selectCourseNumber = selectCourseNumber;
    }

    public Integer getNumberLimit() {
        return numberLimit;
    }

    public void setNumberLimit(Integer numberLimit) {
        this.numberLimit = numberLimit;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
}
