package com.server.edu.mutual.vo;

import com.server.edu.common.rest.PageRequest;

import java.io.Serializable;

public class CulturePlanVo  extends PageRequest implements Serializable {
    private Long id;
    private String studentId;
    private Long courseLabelRelationId;
    private String courseCode;
    private String score;
    private Integer isPass = 0;
    private String tag;
    private String selCourse;
    private String isScorePass;
    private Long labelId;
    private String changeStatus;
    private Long cultureId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public Long getCourseLabelRelationId() {
        return courseLabelRelationId;
    }

    public void setCourseLabelRelationId(Long courseLabelRelationId) {
        this.courseLabelRelationId = courseLabelRelationId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public Integer getIsPass() {
        return isPass;
    }

    public void setIsPass(Integer isPass) {
        this.isPass = isPass;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getSelCourse() {
        return selCourse;
    }

    public void setSelCourse(String selCourse) {
        this.selCourse = selCourse;
    }

    public String getIsScorePass() {
        return isScorePass;
    }

    public void setIsScorePass(String isScorePass) {
        this.isScorePass = isScorePass;
    }

    public Long getLabelId() {
        return labelId;
    }

    public void setLabelId(Long labelId) {
        this.labelId = labelId;
    }

    public String getChangeStatus() {
        return changeStatus;
    }

    public void setChangeStatus(String changeStatus) {
        this.changeStatus = changeStatus;
    }

    public Long getCultureId() {
        return cultureId;
    }

    public void setCultureId(Long cultureId) {
        this.cultureId = cultureId;
    }
}
