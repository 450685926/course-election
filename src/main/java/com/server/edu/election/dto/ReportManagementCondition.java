package com.server.edu.election.dto;

/**
 * @description: 报表管理查询条件
 * @author: bear
 * @create: 2019-02-14 15:18
 */
public class ReportManagementCondition extends ExemptionCourseScoreDto {
    private Integer electCourseStatus;//选课状态
    private String nature;
    private Integer isRebuildCourse;//是否重修

    public Integer getElectCourseStatus() {
        return electCourseStatus;
    }

    public void setElectCourseStatus(Integer electCourseStatus) {
        this.electCourseStatus = electCourseStatus;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public Integer getIsRebuildCourse() {
        return isRebuildCourse;
    }

    public void setIsRebuildCourse(Integer isRebuildCourse) {
        this.isRebuildCourse = isRebuildCourse;
    }
}
