package com.server.edu.election.entity;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "mark_arrangement_t")
public class MarkArrangement implements Serializable {
    /**
     * 主键（自增）
     */
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 学生代码
     */
    @Column(name = "STUDENT_CODE_")
    private String studentCode;

    /**
     * 姓名
     */
    @Column(name = "NAME_")
    private String name;

    /**
     * 考试成绩
     */
    @Column(name = "SCORE_")
    private Integer score;

    private static final long serialVersionUID = 1L;

    /**
     * 获取主键（自增）
     *
     * @return ID_ - 主键（自增）
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置主键（自增）
     *
     * @param id 主键（自增）
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取学生代码
     *
     * @return STUDENT_CODE_ - 学生代码
     */
    public String getStudentCode() {
        return studentCode;
    }

    /**
     * 设置学生代码
     *
     * @param studentCode 学生代码
     */
    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode == null ? null : studentCode.trim();
    }

    /**
     * 获取姓名
     *
     * @return NAME_ - 姓名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置姓名
     *
     * @param name 姓名
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * 获取考试成绩
     *
     * @return SCORE_ - 考试成绩
     */
    public Integer getScore() {
        return score;
    }

    /**
     * 设置考试成绩
     *
     * @param score 考试成绩
     */
    public void setScore(Integer score) {
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
        sb.append(", name=").append(name);
        sb.append(", score=").append(score);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}