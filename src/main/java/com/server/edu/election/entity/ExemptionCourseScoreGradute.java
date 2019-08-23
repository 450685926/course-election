package com.server.edu.election.entity;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "exemption_score_gradute_t")
public class ExemptionCourseScoreGradute implements Serializable {
    /**
     * 主键
     */
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 学生学号
     */
    @Column(name = "STUDENT_CODE_")
    private Integer studentCode;

    /**
     * 课程代码
     */
    @Column(name = "COURSE_CODE_")
    private String courseCode;
    /**
     * 课程名称
     */
    @Column(name = "COURSE_NAME_")
    private String courseName;

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
     * 获取学生学号
     *
     * @return STUDENT_CODE_ - 学生学号
     */
    public Integer getStudentCode() {
        return studentCode;
    }

    /**
     * 设置学生学号
     *
     * @param studentCode 学生学号
     */
    public void setStudentCode(Integer studentCode) {
        this.studentCode = studentCode;
    }


    /**
     * 获取课程代码
     *
     * @return COURSE_CODE_ - 课程代码
     */
    public String getCourseCode() {
        return courseCode;
    }

    
    public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
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
        sb.append(", studentCode=").append(studentCode);
        sb.append(", courseCode=").append(courseCode);
        sb.append(", courseName=").append(courseName);
        sb.append(", score=").append(score);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}