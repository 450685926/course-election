package com.server.edu.exam.base;

import com.server.edu.common.rest.PageRequest;

import java.util.Date;

/**
 * 功能描述：本科生排考通用基础实体类
 *
 * @ClassName UndergExamBaseEntity
 * @Author zhaoerhu
 * @Date 2019/8/19 20:14
 */
public class UndergExamBaseEntity extends PageRequest {
    private Date createDate;

    private Long createPerson;

    private Date updateDate;

    private Long updatePerson;

    private Long sortTime;

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
