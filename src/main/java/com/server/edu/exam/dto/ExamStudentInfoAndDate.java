package com.server.edu.exam.dto;

import java.util.Date;

/**
 * @description: 排考学生课程时间
 * @author: bear
 * @create: 2019-10-18 17:08
 */
public class ExamStudentInfoAndDate {
    private Long examInfoId;
    private String campus;
    private Date examDate;
    private String examStartTime;
    private String examEndTime;
    private String studentCode;
    private String courseCode;
    private String courseName;
    private String examTime;

    public String getExamTime() {
        return examTime;
    }

    public void setExamTime(String examTime) {
        this.examTime = examTime;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Long getExamInfoId() {
        return examInfoId;
    }

    public void setExamInfoId(Long examInfoId) {
        this.examInfoId = examInfoId;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public Date getExamDate() {
        return examDate;
    }

    public void setExamDate(Date examDate) {
        this.examDate = examDate;
    }

    public String getExamStartTime() {
        return examStartTime;
    }

    public void setExamStartTime(String examStartTime) {
        this.examStartTime = examStartTime;
    }

    public String getExamEndTime() {
        return examEndTime;
    }

    public void setExamEndTime(String examEndTime) {
        this.examEndTime = examEndTime;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }
}
