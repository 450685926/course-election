package com.server.edu.election.dto;

import java.io.Serializable;

/**
 * @description: 时间表
 * @author: bear
 * @create: 2019-04-29 17:51
 */
public class TimeTableMessage implements Serializable {
    private Integer dayOfWeek;
    private Integer timeStart;
    private Integer timeEnd;
    private String roomId;
    private String teacherCode;
    private String weekNum;//周次
    private String weekstr;//星期
    private String teacherName;//
    private String timeAndRoom;

    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Integer getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Integer timeStart) {
        this.timeStart = timeStart;
    }

    public Integer getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Integer timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getTeacherCode() {
        return teacherCode;
    }

    public void setTeacherCode(String teacherCode) {
        this.teacherCode = teacherCode;
    }

    public String getWeekNum() {
        return weekNum;
    }

    public void setWeekNum(String weekNum) {
        this.weekNum = weekNum;
    }

    public String getWeekstr() {
        return weekstr;
    }

    public void setWeekstr(String weekstr) {
        this.weekstr = weekstr;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTimeAndRoom() {
        return timeAndRoom;
    }

    public void setTimeAndRoom(String timeAndRoom) {
        this.timeAndRoom = timeAndRoom;
    }
}
