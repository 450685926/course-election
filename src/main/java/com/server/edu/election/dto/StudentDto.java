package com.server.edu.election.dto;

import javax.validation.constraints.NotNull;

import com.server.edu.election.entity.Student;

public class StudentDto extends Student
{
    private static final long serialVersionUID = 1L;
    
    private String courseCode;
    
    @NotNull
    private Long courseId;
    
    private Long calendarId;
    
    private String studentId;
    
    private String projectId;
    
    public String getStudentId()
    {
        return studentId;
    }
    
    public void setStudentId(String studentId)
    {
        this.studentId = studentId;
    }
    
    public Long getCalendarId()
    {
        return calendarId;
    }
    
    public void setCalendarId(Long calendarId)
    {
        this.calendarId = calendarId;
    }
    
    public Long getCourseId()
    {
        return courseId;
    }
    
    public void setCourseId(Long courseId)
    {
        this.courseId = courseId;
    }
    
    public String getCourseCode()
    {
        return courseCode;
    }
    
    public void setCourseCode(String courseCode)
    {
        this.courseCode = courseCode;
    }
    
    public String getProjectId()
    {
        return projectId;
    }
    
    public void setProjectId(String projectId)
    {
        this.projectId = projectId;
    }
    
}
