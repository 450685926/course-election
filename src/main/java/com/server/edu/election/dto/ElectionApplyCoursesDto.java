package com.server.edu.election.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.server.edu.election.entity.Course;

public class ElectionApplyCoursesDto extends Course
{
    private static final long serialVersionUID = 1L;
    
    @NotNull
    private Long calendarId;
    
    @NotEmpty
    private List<String> courses;
    
    private String keyword;
    
    private Integer weekNum;
    
    private Integer mode;
    
    public Integer getMode()
    {
        return mode;
    }
    
    public void setMode(Integer mode)
    {
        this.mode = mode;
    }
    
    public Integer getWeekNum()
    {
        return weekNum;
    }
    
    public void setWeekNum(Integer weekNum)
    {
        this.weekNum = weekNum;
    }
    
    public String getKeyword()
    {
        return keyword;
    }
    
    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }
    
    public Long getCalendarId()
    {
        return calendarId;
    }
    
    public void setCalendarId(Long calendarId)
    {
        this.calendarId = calendarId;
    }
    
    public List<String> getCourses()
    {
        return courses;
    }
    
    public void setCourses(List<String> courses)
    {
        this.courses = courses;
    }
    
}
