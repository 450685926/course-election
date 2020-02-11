package com.server.edu.election.vo;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.election.studentelec.context.ClassTimeUnit;

import java.util.List;

@CodeI18n
public class PublicCourseVo {
    private Long teachClassId;

    private String teachClassCode;

    /**课程代码*/
    private String courseCode;

    /**课程名称*/
    private String courseName;

    /**学分*/
    private Double credits;

    @Code2Text(transformer = "X_XQ")
    private String campus;

    private List<ClassTimeUnit> times;

    public Long getTeachClassId() {
        return teachClassId;
    }

    public void setTeachClassId(Long teachClassId) {
        this.teachClassId = teachClassId;
    }

    public String getTeachClassCode() {
        return teachClassCode;
    }

    public void setTeachClassCode(String teachClassCode) {
        this.teachClassCode = teachClassCode;
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

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public List<ClassTimeUnit> getTimes() {
        return times;
    }

    public void setTimes(List<ClassTimeUnit> times) {
        this.times = times;
    }
}
