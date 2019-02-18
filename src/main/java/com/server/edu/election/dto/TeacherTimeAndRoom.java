package com.server.edu.election.dto;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 学生课表对应教师详细信息
 * @author: bear
 * @create: 2019-02-16 10:03
 */
public class TeacherTimeAndRoom implements Serializable {
    private String courseCode;
    private String courseName;
    private String classCode;
    private String calssName;
    private List<StudentSchoolTimetab> list;

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

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getCalssName() {
        return calssName;
    }

    public void setCalssName(String calssName) {
        this.calssName = calssName;
    }

    public List<StudentSchoolTimetab> getList() {
        return list;
    }

    public void setList(List<StudentSchoolTimetab> list) {
        this.list = list;
    }
}
