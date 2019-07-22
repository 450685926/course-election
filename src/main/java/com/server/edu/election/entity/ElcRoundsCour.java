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
     * 教学班ID
     */
    @Id
    @Column(name = "TEACHING_CLASS_ID_")
    private Long teachingClassId;

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
     * 获取教学班ID
     *
     * @return TEACHING_CLASS_ID_ - 教学班ID
     */
    public Long getTeachingClassId() {
        return teachingClassId;
    }

    /**
     * 设置教学班ID
     *
     * @param teachingClassId 教学班ID
     */
    public void setTeachingClassId(Long teachingClassId) {
        this.teachingClassId = teachingClassId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", roundsId=").append(roundsId);
        sb.append(", teachingClassId=").append(teachingClassId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}