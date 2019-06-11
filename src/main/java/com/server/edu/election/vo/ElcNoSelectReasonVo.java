package com.server.edu.election.vo;

import java.util.List;

import com.server.edu.election.entity.ElcNoSelectReason;

/**
 * @description:
 * @author: bear
 * @create: 2019-05-23 10:54
 */
public class ElcNoSelectReasonVo extends ElcNoSelectReason{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private List<String> studentIds;

    public List<String> getStudentIds() {
        return studentIds;
    }

    public void setStudentIds(List<String> studentIds) {
        this.studentIds = studentIds;
    }
}
