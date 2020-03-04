package com.server.edu.mutual.testvo;

/**
 * MinorPlanFlowContent
 *
 * @author 李阳
 * @description：制定的计划中选中的课
 * @date 2019/7/2 23:04
 */
public class CourseOfMinorPlan {
    private Long courseId;

    private String courseName;//课程名称

    private String courseCollege;//开设学院

    private String courseLabel;//课程类别

    private Double coursePeriod;//课时

    private Double courseCredit;//学分

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseCollege() {
        return courseCollege;
    }

    public void setCourseCollege(String courseCollege) {
        this.courseCollege = courseCollege;
    }

    public String getCourseLabel() {
        return courseLabel;
    }

    public void setCourseLabel(String courseLabel) {
        this.courseLabel = courseLabel;
    }

    public Double getCoursePeriod() {
        return coursePeriod;
    }

    public void setCoursePeriod(Double coursePeriod) {
        this.coursePeriod = coursePeriod;
    }

    public Double getCourseCredit() {
        return courseCredit;
    }

    public void setCourseCredit(Double courseCredit) {
        this.courseCredit = courseCredit;
    }
}
