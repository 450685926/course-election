package com.server.edu.election.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Table(name = "elc_teaching_class_bind_t")
public class ElcTeachingClassBind implements Serializable {
    /**
     * 教学班id
     */
    @Id
    @NotNull
    @Column(name = "TEACHING_CLASS_ID_")
    private Long teachingClassId;

    /**
     * 绑定教学班id
     */
    @Id
    @NotNull
    @Column(name = "BIND_CLASS_ID_")
    private Long bindClassId;

    private static final long serialVersionUID = 1L;

    /**
     * 获取教学班id
     *
     * @return TEACHING_CLASS_ID_ - 教学班id
     */
    public Long getTeachingClassId() {
        return teachingClassId;
    }

    /**
     * 设置教学班id
     *
     * @param teachingClassId 教学班id
     */
    public void setTeachingClassId(Long teachingClassId) {
        this.teachingClassId = teachingClassId;
    }

    /**
     * 获取绑定教学班id
     *
     * @return BIND_CLASS_ID_ - 绑定教学班id
     */
    public Long getBindClassId() {
        return bindClassId;
    }

    /**
     * 设置绑定教学班id
     *
     * @param bindClassId 绑定教学班id
     */
    public void setBindClassId(Long bindClassId) {
        this.bindClassId = bindClassId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", teachingClassId=").append(teachingClassId);
        sb.append(", bindClassId=").append(bindClassId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}