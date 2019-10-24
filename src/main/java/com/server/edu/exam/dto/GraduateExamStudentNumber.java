package com.server.edu.exam.dto;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 排考人数
 * @author: bear
 * @create: 2019-10-09 17:08
 */
public class GraduateExamStudentNumber implements Serializable {
    private List<Long> examInfoIds;
    private Long examRoomId;
    private Integer actualNumber;
    private Integer totalNumber;
    private Integer roomCapacity;
    private Integer roomNumber;

    public List<Long> getExamInfoIds() {
        return examInfoIds;
    }

    public void setExamInfoIds(List<Long> examInfoIds) {
        this.examInfoIds = examInfoIds;
    }

    public Long getExamRoomId() {
        return examRoomId;
    }

    public void setExamRoomId(Long examRoomId) {
        this.examRoomId = examRoomId;
    }

    public Integer getActualNumber() {
        return actualNumber;
    }

    public void setActualNumber(Integer actualNumber) {
        this.actualNumber = actualNumber;
    }

    public Integer getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(Integer totalNumber) {
        this.totalNumber = totalNumber;
    }

    public Integer getRoomCapacity() {
        return roomCapacity;
    }

    public void setRoomCapacity(Integer roomCapacity) {
        this.roomCapacity = roomCapacity;
    }

    public Integer getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(Integer roomNumber) {
        this.roomNumber = roomNumber;
    }
}
