package com.server.edu.exam.dto;

import java.io.Serializable;

/**
 * @description: 课程表教室中间表
 * @author: bear
 * @create: 2019-09-24 15:07
 */
public class ExamInfoRoomDto implements Serializable {
    private Long examInfoId;
    private Integer examRooms;
    private Integer actualNumber;

    public Integer getActualNumber() {
        return actualNumber;
    }

    public void setActualNumber(Integer actualNumber) {
        this.actualNumber = actualNumber;
    }

    public Long getExamInfoId() {
        return examInfoId;
    }

    public void setExamInfoId(Long examInfoId) {
        this.examInfoId = examInfoId;
    }

    public Integer getExamRooms() {
        return examRooms;
    }

    public void setExamRooms(Integer examRooms) {
        this.examRooms = examRooms;
    }
}
