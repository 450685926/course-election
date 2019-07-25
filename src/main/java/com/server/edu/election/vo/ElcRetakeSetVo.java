package com.server.edu.election.vo;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

public class ElcRetakeSetVo {
    @NotNull
    private Long calendarId;

    private Long retakeSetId;

    @NotNull
    private Integer openFlag;

    /**开始时间*/
    private Date start;

    /**结束时间*/
    private Date end;

    @NotBlank
    private String projectId;

    private Date createAt;

    private List<Long> ruleIds;

    public Long getRetakeSetId() {
        return retakeSetId;
    }

    public void setRetakeSetId(Long retakeSetId) {
        this.retakeSetId = retakeSetId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public List<Long> getRuleIds() {
        return ruleIds;
    }

    public void setRuleIds(List<Long> ruleIds) {
        this.ruleIds = ruleIds;
    }

    public Long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }

    public Integer getOpenFlag() {
        return openFlag;
    }

    public void setOpenFlag(Integer openFlag) {
        this.openFlag = openFlag;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }
}
