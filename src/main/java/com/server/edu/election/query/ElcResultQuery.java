package com.server.edu.election.query;

import javax.validation.constraints.NotNull;

public class ElcResultQuery
{
    /**
     * 校历ID（学年学期）
     */
    @NotNull
    private Long calendarId;
    
    private String campus;
    
    /**
     * 课程编号
     */
    private String courseCode;
    
    /**
     * 课程名称
     */
    private String courseName;
    
    /**
     * 教学班编号
     */
    private String teachingClassCode;
    
    /**
     * 是否排课
     */
    private Integer manArrangeFlag;
    
    public Long getCalendarId()
    {
        return calendarId;
    }
    
    public void setCalendarId(Long calendarId)
    {
        this.calendarId = calendarId;
    }
    
    public String getCampus()
    {
        return campus;
    }
    
    public void setCampus(String campus)
    {
        this.campus = campus;
    }
    
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
    
    public String getTeachingClassCode()
    {
        return teachingClassCode;
    }
    
    public void setTeachingClassCode(String teachingClassCode)
    {
        this.teachingClassCode = teachingClassCode;
    }
    
    public Integer getManArrangeFlag()
    {
        return manArrangeFlag;
    }
    
    public void setManArrangeFlag(Integer manArrangeFlag)
    {
        this.manArrangeFlag = manArrangeFlag;
    }
    
}
