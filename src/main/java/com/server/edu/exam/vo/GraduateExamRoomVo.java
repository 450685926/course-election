package com.server.edu.exam.vo;

import com.server.edu.exam.entity.GraduateExamRoom;

import java.util.List;

/**
 * @description:
 * @author: bear
 * @create: 2019-09-03 15:48
 */
public class GraduateExamRoomVo extends GraduateExamRoom {
    private String teacherCode;
    private String teacherName;
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
