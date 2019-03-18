package com.server.edu.election.studentelec.context;

import java.io.Serializable;

/**
 * @description: 计划课程组
 * @author: bear
 * @create: 2019-03-12 15:44
 */
public class CourseGroup implements Serializable {
    private Long label;
    private Double crrdits;
    /**通识标识 1 为通识 0 非通识*/
    private String limitType;

    public String getLimitType() {
        return limitType;
    }

    public void setLimitType(String limitType) {
        this.limitType = limitType;
    }

    public Long getLabel() {
        return label;
    }

    public void setLabel(Long label) {
        this.label = label;
    }

    public Double getCrrdits() {
        return crrdits;
    }

    public void setCrrdits(Double crrdits) {
        this.crrdits = crrdits;
    }
}
