package com.server.edu.election.dto;

/**
 * @description: 教学班关联老师
 * @author: bear
 * @create: 2019-02-14 21:17
 */
public class ClassTeacherDto {
    private String classCode;
    private String teacherCode;
    private String teacherName;

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getTeacherCode() {
        return teacherCode;
    }

    public void setTeacherCode(String teacherCode) {
        this.teacherCode = teacherCode;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
}
