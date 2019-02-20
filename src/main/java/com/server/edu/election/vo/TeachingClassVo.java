package com.server.edu.election.vo;

import com.server.edu.election.entity.TeachingClass;

public class TeachingClassVo extends TeachingClass
{
    private String courseCode;
    private String courseName;
    private Double credits;
    
    public String getCourseCode()
    {
        return courseCode;
    }
    public void setCourseCode(String courseCode)
    {
        this.courseCode = courseCode;
    }
    public String getCourseName()
    {
        return courseName;
    }
    public void setCourseName(String courseName)
    {
        this.courseName = courseName;
    }
    public Double getCredits()
    {
        return credits;
    }
    public void setCredits(Double credits)
    {
        this.credits = credits;
    }
    
}
