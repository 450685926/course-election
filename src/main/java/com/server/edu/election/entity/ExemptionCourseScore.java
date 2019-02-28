package com.server.edu.election.entity;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "exemption_score_t")
public class ExemptionCourseScore implements Serializable {
    /**
     * 主键
     */
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 校历ID
     */
    @Column(name = "CALENDAR_ID_")
    private Long calendarId;

    /**
     * 学生学号
     */
    @Column(name = "STUDENT_CODE_")
    private String studentCode;

    /**
     * 课程代码
     */
    @Column(name = "COURSE_CODE_")
    private String courseCode;

    /**
     * 成绩
     */
    @Column(name = "SCORE_")
    private Double score;

    private static final long serialVersionUID = 1L;

    /**
     * 获取主键
     *
     * @return ID_ - 主键
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取校历ID
     *
     * @return CALENDAR_ID_ - 校历ID
     */
    public Long getCalendarId() {
        return calendarId;
    }

    /**
     * 设置校历ID
     *
     * @param calendarId 校历ID
     */
    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }

    /**
     * 获取学生学号
     *
     * @return STUDENT_CODE_ - 学生学号
     */
    public String getStudentCode() {
        return studentCode;
    }

    /**
     * 设置学生学号
     *
     * @param studentCode 学生学号
     */
    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode == null ? null : studentCode.trim();
    }


    /**
     * 获取课程代码
     *
     * @return COURSE_CODE_ - 课程代码
     */
    public String getCourseCode() {
        return courseCode;
    }

    /**
     * 设置课程代码
     *
     * @param courseCode 课程代码
     */
    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode == null ? null : courseCode.trim();
    }

    /**
     * 获取成绩
     *
     * @return SCORE_ - 成绩
     */
    public Double getScore() {
        return score;
    }

    /**
     * 设置成绩
     *
     * @param score 成绩
     */
    public void setScore(Double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", calendarId=").append(calendarId);
        sb.append(", studentCode=").append(studentCode);
        sb.append(", courseCode=").append(courseCode);
        sb.append(", score=").append(score);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}