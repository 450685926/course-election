package com.server.edu.election.dto;

import com.server.edu.election.vo.StudentVo;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 预览点名册
 * @author: bear
 * @create: 2019-02-15 10:16
 */
public class PreviewRollBookList implements Serializable {
    private List<StudentVo> list;
    private String classCode;
    private String courseName;
    private String teacherName;
    private String time;
    private String room;
    private String calendarName;

    public List<StudentVo> getList() {
        return list;
    }

    public void setList(List<StudentVo> list) {
        this.list = list;
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

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getCalendarName() {
        return calendarName;
    }

    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }
}
