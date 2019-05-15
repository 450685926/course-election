package com.server.edu.election.query;

import java.util.List;

import javax.validation.constraints.NotNull;

public class ElcResultQuery
{
    /**
     * 校历ID（学年学期）
     */
    @NotNull
    private Long calendarId;
    
    private String projectId;
    
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
     * 教学班id
     */
    private Long teachingClassId;
    
    /**
     * 教学班ids
     */
    private List<Long> teachingClassIds;
    
    /**
     * 是否排课
     */
    private Integer manArrangeFlag;
    
    /**
     * 学分
     */
    private Double credits;
    
    private String label;
    
    public Long getCalendarId()
    {
        return calendarId;
    }
    
    public void setCalendarId(Long calendarId)
    {
        this.calendarId = calendarId;
    }
    
    public String getProjectId()
    {
        return projectId;
    }
    
    public void setProjectId(String projectId)
    {
        this.projectId = projectId;
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
    
    public Long getTeachingClassId()
    {
        return teachingClassId;
    }
    
    public void setTeachingClassId(Long teachingClassId)
    {
        this.teachingClassId = teachingClassId;
    }
    
    public List<Long> getTeachingClassIds()
    {
        return teachingClassIds;
    }
    
    public void setTeachingClassIds(List<Long> teachingClassIds)
    {
        this.teachingClassIds = teachingClassIds;
    }
    
    public String getLabel()
    {
        return label;
    }
    
    public void setLabel(String label)
    {
        this.label = label;
    }
}
