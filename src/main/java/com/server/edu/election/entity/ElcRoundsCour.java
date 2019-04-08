package com.server.edu.election.entity;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "election_rounds_cour_t")
public class ElcRoundsCour implements Serializable {
    /**
     * 选课轮次主键
     */
    @Id
    @Column(name = "ROUNDS_ID_")
    private Long roundsId;

    /**
     * 课程代码
     */
    @Id
    @Column(name = "COURSE_CODE_")
    private String courseCode;

    private static final long serialVersionUID = 1L;

    /**
     * 获取选课轮次主键
     *
     * @return ROUNDS_ID_ - 选课轮次主键
     */
    public Long getRoundsId() {
        return roundsId;
    }

    /**
     * 设置选课轮次主键
     *
     * @param roundsId 选课轮次主键
     */
    public void setRoundsId(Long roundsId) {
        this.roundsId = roundsId;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", roundsId=").append(roundsId);
        sb.append(", courseCode=").append(courseCode);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}