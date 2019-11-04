package com.server.edu.exam.dto;

/**
 * @description: 封装任课教师数据
 * @author: bear
 * @create: 2019-10-16 09:52
 */
public class GraduateTeachingClassDto {
    private Long teachingClassId;
    private Integer studentNumber;
    private String teacherCode;
    private String teacherName;

    public Long getTeachingClassId() {
        return teachingClassId;
    }

    public void setTeachingClassId(Long teachingClassId) {
        this.teachingClassId = teachingClassId;
    }

    public Integer getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(Integer studentNumber) {
        this.studentNumber = studentNumber;
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
