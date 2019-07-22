package com.server.edu.election.vo;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

public class ElcRetakeSetVo {
    @NotNull
    private Long calendarId;

    @NotNull
    private Integer openFlag;

    /**开始时间*/
    private Date start;

    /**结束时间*/
    private Date end;

    private List<Long> ruleIds;

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
