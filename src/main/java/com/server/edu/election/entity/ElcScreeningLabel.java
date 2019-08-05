package com.server.edu.election.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

@Table(name = "elc_screening_label_t")
public class ElcScreeningLabel implements Serializable {
    /**
     * 主键
     */
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 学期
     */
    @NotNull
    @Column(name = "CALENDAR_ID_")
    private Long calendarId;

    /**
     * 选课标签名称
     */
    @NotBlank
    @Column(name = "LABEL_NAME_")
    private String labelName;

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
     * 获取学期
     *
     * @return CALENDAR_ID_ - 学期
     */
    public Long getCalendarId() {
        return calendarId;
    }

    /**
     * 设置学期
     *
     * @param calendarId 学期
     */
    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }

    /**
     * 获取选课标签名称
     *
     * @return LABEL_NAME_ - 选课标签名称
     */
    public String getLabelName() {
        return labelName;
    }

    /**
     * 设置选课标签名称
     *
     * @param labelName 选课标签名称
     */
    public void setLabelName(String labelName) {
        this.labelName = labelName == null ? null : labelName.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", calendarId=").append(calendarId);
        sb.append(", labelName=").append(labelName);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}