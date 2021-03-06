package com.server.edu.election.dto;

import java.io.Serializable;

import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;

/**
 * @description: 预警学生选课
 * @author: bear
 * @create: 2019-05-10 11:28
 */
@CodeI18n
public class LoserStuElcCourse implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long calendarId;
    private String studentId;
    private String studentName;
    private String studentCategory;
    private String classCode;
    private String courseCode;
    private String courseName;
    private Long courseLabel;
    private Double credits;
    @Code2Text(DictTypeEnum.X_XDLX)
    private Integer courseTakeType;
    private Long teachingClassId;
    private Integer chooseObj;
    private Integer mode;
    private Integer turn;

    public Long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getChooseObj() {
        return chooseObj;
    }

    public void setChooseObj(Integer chooseObj) {
        this.chooseObj = chooseObj;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public Integer getTurn() {
        return turn;
    }

    public void setTurn(Integer turn) {
        this.turn = turn;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentCategory() {
        return studentCategory;
    }

    public void setStudentCategory(String studentCategory) {
        this.studentCategory = studentCategory;
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

    public Long getCourseLabel() {
        return courseLabel;
    }

    public void setCourseLabel(Long courseLabel) {
        this.courseLabel = courseLabel;
    }

    public Double getCredits() {
        return credits;
    }

    public void setCredits(Double credits) {
        this.credits = credits;
    }

    public Integer getCourseTakeType() {
        return courseTakeType;
    }

    public void setCourseTakeType(Integer courseTakeType) {
        this.courseTakeType = courseTakeType;
    }

    public Long getTeachingClassId() {
        return teachingClassId;
    }

    public void setTeachingClassId(Long teachingClassId) {
        this.teachingClassId = teachingClassId;
    }
}
