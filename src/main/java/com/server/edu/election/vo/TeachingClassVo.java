package com.server.edu.election.vo;

import com.server.edu.election.entity.TeachingClass;

public class TeachingClassVo extends TeachingClass
{
    private String courseName;
    public String getCourseName()
    {
        return courseName;
    }
    public void setCourseName(String courseName)
    {
        this.courseName = courseName;
    }
    
    
}
