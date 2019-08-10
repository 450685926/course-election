package com.server.edu.election.dto;

import java.util.List;

import com.server.edu.election.entity.ElcCouSubs;

/**
 * 
 * 替代课程
 * 
 */
public class ElcCouSubsDto extends ElcCouSubs
{
    private static final long serialVersionUID = 1L;
    
    private String origsCourseCode;
    
    private String origsCourseName;
    
    private Integer origsCredits;
    
    private String subCourseCode;
    
    private String subCourseName;
    
    private Integer subCredits;
    
    private Integer grade;
    
    private String faculty;
    
    private String profession;
    
    private List<String> studentIds;
    
    private Integer mode;
    
    //原课程信息
    private String origsCourseInfo;
    
    //新课程信息
    private String subCourseInfo;
    
    public String getOrigsCourseCode()
    {
        return origsCourseCode;
    }
    
    public void setOrigsCourseCode(String origsCourseCode)
    {
        this.origsCourseCode = origsCourseCode;
    }
    
    public String getOrigsCourseName()
    {
        return origsCourseName;
    }
    
    public void setOrigsCourseName(String origsCourseName)
    {
        this.origsCourseName = origsCourseName;
    }
    
    public Integer getOrigsCredits()
    {
        return origsCredits;
    }
    
    public void setOrigsCredits(Integer origsCredits)
    {
        this.origsCredits = origsCredits;
    }
    
    public String getSubCourseCode()
    {
        return subCourseCode;
    }
    
    public void setSubCourseCode(String subCourseCode)
    {
        this.subCourseCode = subCourseCode;
    }
    
    public String getSubCourseName()
    {
        return subCourseName;
    }
    
    public void setSubCourseName(String subCourseName)
    {
        this.subCourseName = subCourseName;
    }
    
    public Integer getSubCredits()
    {
        return subCredits;
    }
    
    public void setSubCredits(Integer subCredits)
    {
        this.subCredits = subCredits;
    }
    
    public Integer getGrade()
    {
        return grade;
    }
    
    public void setGrade(Integer grade)
    {
        this.grade = grade;
    }
    
    public String getFaculty()
    {
        return faculty;
    }
    
    public void setFaculty(String faculty)
    {
        this.faculty = faculty;
    }
    
    public String getProfession()
    {
        return profession;
    }
    
    public void setProfession(String profession)
    {
        this.profession = profession;
    }
    
    public List<String> getStudentIds()
    {
        return studentIds;
    }
    
    public void setStudentIds(List<String> studentIds)
    {
        this.studentIds = studentIds;
    }
    
    public Integer getMode()
    {
        return mode;
    }
    
    public void setMode(Integer mode)
    {
        this.mode = mode;
    }
    
    public String getOrigsCourseInfo()
    {
        return origsCourseInfo;
    }
    
    public void setOrigsCourseInfo(String origsCourseInfo)
    {
        this.origsCourseInfo = origsCourseInfo;
    }
    
    public String getSubCourseInfo()
    {
        return subCourseInfo;
    }
    
    public void setSubCourseInfo(String subCourseInfo)
    {
        this.subCourseInfo = subCourseInfo;
    }
    
}
