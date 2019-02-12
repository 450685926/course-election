package com.server.edu.election.query;

import javax.validation.constraints.NotNull;

public class ElecRoundCourseQuery
{
    @NotNull
    private Long roundId;
    
    //校历id
    @NotNull
    private Long calendarId;
    
    private String courseCode;
    
    private String courseName;
    
    /** 学院 */
    private String faculty;
    
    /**培养层次*/
    private String trainingLevel;
    /**课程性质*/
    private String nature;
    
    public Long getRoundId()
    {
        return roundId;
    }
    
    public void setRoundId(Long roundId)
    {
        this.roundId = roundId;
    }
    
    public Long getCalendarId()
    {
        return calendarId;
    }
    
    public void setCalendarId(Long calendarId)
    {
        this.calendarId = calendarId;
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
    
    public String getFaculty()
    {
        return faculty;
    }
    
    public void setFaculty(String faculty)
    {
        this.faculty = faculty;
    }

    public String getTrainingLevel()
    {
        return trainingLevel;
    }

    public void setTrainingLevel(String trainingLevel)
    {
        this.trainingLevel = trainingLevel;
    }

    public String getNature()
    {
        return nature;
    }

    public void setNature(String nature)
    {
        this.nature = nature;
    }
    
}
