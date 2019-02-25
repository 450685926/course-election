package com.server.edu.election.vo;

import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.election.entity.ElcAffinityCourses;

public class ElcAffinityCoursesVo extends ElcAffinityCourses
{
    private static final long serialVersionUID = 1L;
    
    private String courseName;
    
    private String courseNameEn;
    
    @Code2Text(transformer = "X_PYCC")
    private String trainingLevel;
    
    //学分
    private Double credits;
    
    @NotEmpty
    private List<String> studentIds;
    
    public List<String> getStudentIds()
    {
        return studentIds;
    }
    
    public void setStudentIds(List<String> studentIds)
    {
        this.studentIds = studentIds;
    }
    
    public String getCourseName()
    {
        return courseName;
    }
    
    public void setCourseName(String courseName)
    {
        this.courseName = courseName;
    }
    
    public String getCourseNameEn()
    {
        return courseNameEn;
    }
    
    public void setCourseNameEn(String courseNameEn)
    {
        this.courseNameEn = courseNameEn;
    }
    
    public String getTrainingLevel()
    {
        return trainingLevel;
    }
    
    public void setTrainingLevel(String trainingLevel)
    {
        this.trainingLevel = trainingLevel;
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