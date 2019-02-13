package com.server.edu.election.vo;

import com.server.edu.election.entity.Student;

/**
 * @description: 重修门数
 * @author: bear
 * @create: 2019-02-13 16:14
 */
public class StudentVo extends Student {
    private Integer rebuildNumber;

    public Integer getRebuildNumber() {
        return rebuildNumber;
    }

    public void setRebuildNumber(Integer rebuildNumber) {
        this.rebuildNumber = rebuildNumber;
    }
}
