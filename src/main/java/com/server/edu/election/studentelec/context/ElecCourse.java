package com.server.edu.election.studentelec.context;

import java.util.List;

public class ElecCourse
{
    private String courseCode;
    
    private String courseName;
    
    private Double credits;
    
    private String nameEn;
    
    /**校区*/
    private String campus;
    
    private List<Long> teachClassIds;
    
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
    
    public String getNameEn()
    {
        return nameEn;
    }
    
    public void setNameEn(String nameEn)
    {
        this.nameEn = nameEn;
    }
    
    public String getCampus()
    {
        return campus;
    }
    
    public void setCampus(String campus)
    {
        this.campus = campus;
    }
    
    public List<Long> getTeachClassIds()
    {
        return teachClassIds;
    }
    
    public void setTeachClassIds(List<Long> teachClassIds)
    {
        this.teachClassIds = teachClassIds;
    }
    
}
