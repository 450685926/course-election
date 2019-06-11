package com.server.edu.election.vo;

import com.server.edu.election.entity.RebuildCourseNoChargeType;

/**
 * @description:
 * @author: bear
 * @create: 2019-05-30 12:53
 */
public class RebuildCourseNoChargeTypeVo extends RebuildCourseNoChargeType {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String deptId;

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }
}
