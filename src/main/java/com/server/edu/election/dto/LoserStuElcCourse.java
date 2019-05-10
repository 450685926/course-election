package com.server.edu.election.dto;

import java.io.Serializable;

/**
 * @description: 预警学生选课
 * @author: bear
 * @create: 2019-05-10 11:28
 */
public class LoserStuElcCourse implements Serializable {
    private String studentId;
    private String studentName;
    private String studentCategory;
    private String classCode;
    private String courseCode;
    private String courseName;
    private Long courseLabel;
    private Double credits;
    private Integer courseTakeType;
    private Long teachingClassId;

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
