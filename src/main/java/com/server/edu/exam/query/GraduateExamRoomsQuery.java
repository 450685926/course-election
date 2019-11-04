package com.server.edu.exam.query;

import java.util.List;

/**
 * @description: 查询考场列表
 * @author: bear
 * @create: 2019-09-23 11:37
 */
public class GraduateExamRoomsQuery {
    private Integer mode;
    private List<Long> examInfoIds;
    private Long examRoomId;
    private List<Long> teachingClassIds;

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public List<Long> getTeachingClassIds() {
        return teachingClassIds;
    }

    public void setTeachingClassIds(List<Long> teachingClassIds) {
        this.teachingClassIds = teachingClassIds;
    }

    public Long getExamRoomId() {
        return examRoomId;
    }

    public void setExamRoomId(Long examRoomId) {
        this.examRoomId = examRoomId;
    }

    public List<Long> getExamInfoIds() {
        return examInfoIds;
    }

    public void setExamInfoIds(List<Long> examInfoIds) {
        this.examInfoIds = examInfoIds;
    }
}
