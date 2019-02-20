package com.server.edu.election.entity;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "elc_affinity_courses_stds_t")
public class ElcAffinityCoursesStds implements Serializable {
    /**
     * 优先学生名单-课程
     */
    @Id
    @Column(name = "AFFINITY_COURSE_ID_")
    private Long affinityCourseId;

    /**
     * 学号
     */
    @Id
    @Column(name = "STUDENT_ID_")
    private String studentId;

    private static final long serialVersionUID = 1L;

    /**
     * 获取优先学生名单-课程
     *
     * @return AFFINITY_COURSE_ID_ - 优先学生名单-课程
     */
    public Long getAffinityCourseId() {
        return affinityCourseId;
    }

    /**
     * 设置优先学生名单-课程
     *
     * @param affinityCourseId 优先学生名单-课程
     */
    public void setAffinityCourseId(Long affinityCourseId) {
        this.affinityCourseId = affinityCourseId;
    }

    /**
     * 获取学号
     *
     * @return STUDENT_ID_ - 学号
     */
    public String getStudentId() {
        return studentId;
    }

    /**
     * 设置学号
     *
     * @param studentId 学号
     */
    public void setStudentId(String studentId) {
        this.studentId = studentId == null ? null : studentId.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", affinityCourseId=").append(affinityCourseId);
        sb.append(", studentId=").append(studentId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}