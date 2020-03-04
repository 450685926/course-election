package com.server.edu.mutual.testvo;

import java.io.Serializable;
import java.util.List;

/**
 * @Description:课程关系分组表ID LIST
 * @author:lizhwei
 * @time:2018年11月29日 16:05:08
 */
public class CourseLabelRelGroupVo implements
        Serializable {

    private static final long serialVersionUID = 1L;

    private List<Long> courseIdList;

    private List<String> courseCodeList;
    private Long trainingId;

    /**
     * 1：模块信息  还是  2：课程信息
     */
    private String courseOrModeltype;

    public String getCourseOrModeltype() {
        return courseOrModeltype;
    }

    public void setCourseOrModeltype(String courseOrModeltype) {
        this.courseOrModeltype = courseOrModeltype;
    }

    public List<Long> getCourseIdList() {
        return courseIdList;
    }

    public void setCourseIdList(List<Long> courseIdList) {
        this.courseIdList = courseIdList;
    }

    public Long getTrainingId() {
        return trainingId;
    }

    public void setTrainingId(Long trainingId) {
        this.trainingId = trainingId;
    }

    public List<String> getCourseCodeList() {
        return courseCodeList;
    }

    public void setCourseCodeList(List<String> courseCodeList) {
        this.courseCodeList = courseCodeList;
    }
}
