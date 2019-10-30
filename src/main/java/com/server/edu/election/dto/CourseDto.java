package com.server.edu.election.dto;

import com.server.edu.election.entity.Course;

public class CourseDto extends Course
{
    /**
     * 模式：1正常，2英语 3体育
     */
    private Integer mode;
    
    private String projectId;
    
    private String keyword;
    
    private Long calendarId;
    
    public String getProjectId()
    {
        return projectId;
    }
    
    public void setProjectId(String projectId)
    {
        this.projectId = projectId;
    }
    
    public Integer getMode()
    {
        return mode;
    }
    
    public void setMode(Integer mode)
    {
        this.mode = mode;
    }
    
    public String getKeyword()
    {
        return keyword;
    }
    
    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }
    
    public Long getCalendarId() {
		return calendarId;
	}

	public void setCalendarId(Long calendarId) {
		this.calendarId = calendarId;
	}

	private static final long serialVersionUID = 1L;
}
