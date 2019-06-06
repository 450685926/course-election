package com.server.edu.election.vo;

import com.server.edu.election.entity.ElcNoSelectReason;

import java.util.List;

/**
 * @description:
 * @author: bear
 * @create: 2019-05-23 10:54
 */
public class ElcNoSelectReasonVo extends ElcNoSelectReason{
    private List<String> studentIds;

    public List<String> getStudentIds() {
        return studentIds;
    }

    public void setStudentIds(List<String> studentIds) {
        this.studentIds = studentIds;
    }
}
