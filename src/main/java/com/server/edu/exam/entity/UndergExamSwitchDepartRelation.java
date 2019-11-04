package com.server.edu.exam.entity;

import com.server.edu.exam.base.UndergExamBaseEntity;

import java.util.Date;

public class UndergExamSwitchDepartRelation extends UndergExamBaseEntity {
    private Long id;

    private String configId;

    private String collegeCode;

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

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId == null ? null : configId.trim();
    }

    public String getCollegeCode() {
        return collegeCode;
    }

    public void setCollegeCode(String collegeCode) {
        this.collegeCode = collegeCode == null ? null : collegeCode.trim();
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