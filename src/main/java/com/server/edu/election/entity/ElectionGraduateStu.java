package com.server.edu.election.entity;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "election_graduate_stu_t")
public class ElectionGraduateStu implements Serializable {
    /**
     * 选课轮次主键
     */
    @Id
    @Column(name = "ROUNDS_ID_")
    private Long roundsId;

    /**
     * 结业生学号
     */
    @Id
    @Column(name = "STUDENT_ID_")
    private String studentId;

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
     * 获取结业生学号
     *
     * @return STUDENT_ID_ - 结业生学号
     */
    public String getStudentId() {
        return studentId;
    }

    /**
     * 设置结业生学号
     *
     * @param studentId 结业生学号
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
        sb.append(", roundsId=").append(roundsId);
        sb.append(", studentId=").append(studentId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}