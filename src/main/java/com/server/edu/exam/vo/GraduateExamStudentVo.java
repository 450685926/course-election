package com.server.edu.exam.vo;

import com.server.edu.exam.entity.GraduateExamStudent;

/**
 * @description:
 * @author: bear
 * @create: 2019-10-09 09:23
 */
public class GraduateExamStudentVo extends GraduateExamStudent {
    private Long roomId;
    private String roomName;
    private String ip;
    private String operatorCode;
    private String operatorName;
    private Integer examType;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getOperatorCode() {
        return operatorCode;
    }

    public void setOperatorCode(String operatorCode) {
        this.operatorCode = operatorCode;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public Integer getExamType() {
        return examType;
    }

    public void setExamType(Integer examType) {
        this.examType = examType;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}
