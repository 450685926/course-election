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
