package com.server.edu.exam.entity;

import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.exam.base.UndergExamBaseEntity;

import javax.persistence.Table;
import java.util.Date;

@CodeI18n
@Table(name = "underg_exam_switch_config_t")
public class UndergExamSwitchConfig extends UndergExamBaseEntity {
    private Long id;

    private String termId;

    private Integer type;

    private Integer examType;

    private Date beginTime;

    private Date endTime;

    private Integer deleteStatus;

    private Date createDate;

    private Long createPerson;

    private Date updateDate;

    private Long updatePerson;

    private Long sortTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTermId() {
        return termId;
    }

    public void setTermId(String termId) {
        this.termId = termId == null ? null : termId.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getExamType() {
        return examType;
    }

    public void setExamType(Integer examType) {
        this.examType = examType;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getDeleteStatus() {
        return deleteStatus;
    }

    public void setDeleteStatus(Integer deleteStatus) {
        this.deleteStatus = deleteStatus;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Long getCreatePerson() {
        return createPerson;
    }

    public void setCreatePerson(Long createPerson) {
        this.createPerson = createPerson;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Long getUpdatePerson() {
        return updatePerson;
    }

    public void setUpdatePerson(Long updatePerson) {
        this.updatePerson = updatePerson;
    }

    public Long getSortTime() {
        return sortTime;
    }

    public void setSortTime(Long sortTime) {
        this.sortTime = sortTime;
    }
}