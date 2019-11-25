package com.server.edu.election.dto;

import javax.validation.constraints.NotNull;

public class BatchAutoRemoveDto {
    @NotNull
    private Long calendarId;
    
    @NotNull
    private Long roundId;
    
    /**删除特殊学生*/
    private Boolean invincibleStu;
    
    /**删除选先学生*/
    private Boolean affinityStu;
    
    /**剔除非指定年级、专业的学生*/
    private Boolean gradAndPre;
    
    /**按教学班选课限制筛选*/
    private Boolean classElcCondition;
    
    /**按教学班对应专业配比人数进行筛选*/
    private Boolean gradAndPrePeople;
    
    /**按建议课表进行筛选*/
    private Boolean suggestSwitchCourse;
    
    public Long getCalendarId()
    {
        return calendarId;
    }
    
    public void setCalendarId(Long calendarId)
    {
        this.calendarId = calendarId;
    }
    
    public Long getRoundId() {
		return roundId;
	}

	public void setRoundId(Long roundId) {
		this.roundId = roundId;
	}

	public Boolean getInvincibleStu()
    {
        return invincibleStu;
    }
    
    public void setInvincibleStu(Boolean invincibleStu)
    {
        this.invincibleStu = invincibleStu;
    }
    
    public Boolean getAffinityStu()
    {
        return affinityStu;
    }
    
    public void setAffinityStu(Boolean affinityStu)
    {
        this.affinityStu = affinityStu;
    }
    
    public Boolean getGradAndPre()
    {
        return gradAndPre;
    }
    
    public void setGradAndPre(Boolean gradAndPre)
    {
        this.gradAndPre = gradAndPre;
    }
    
    public Boolean getClassElcCondition()
    {
        return classElcCondition;
    }
    
    public void setClassElcCondition(Boolean classElcCondition)
    {
        this.classElcCondition = classElcCondition;
    }
    
    public Boolean getGradAndPrePeople()
    {
        return gradAndPrePeople;
    }
    
    public void setGradAndPrePeople(Boolean gradAndPrePeople)
    {
        this.gradAndPrePeople = gradAndPrePeople;
    }

	public Boolean getSuggestSwitchCourse() {
		return suggestSwitchCourse;
	}

	public void setSuggestSwitchCourse(Boolean suggestSwitchCourse) {
		this.suggestSwitchCourse = suggestSwitchCourse;
	}	
}
