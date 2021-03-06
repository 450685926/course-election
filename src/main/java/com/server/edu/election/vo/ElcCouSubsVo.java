package com.server.edu.election.vo;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.election.entity.ElcCouSubs;

@CodeI18n
public class ElcCouSubsVo extends ElcCouSubs
{
    private static final long serialVersionUID = 1L;
    
    private String origsCourseCode;
    
    private String origsCourseName;
    
    private Double origsCredits;
    
    private String subCourseName;
    
    private String subCourseCode;
    
    private Double subCredits;
    
    private Integer grade;
    
    @Code2Text(transformer = "X_YX")
    private String faculty;
    
    @Code2Text(transformer = "G_ZY")
    private String profession;
    
    private String studentName;
    
    /**原课程信息*/
    private String origsCourseInfo;
    
    /**新课程信息*/
    private String subCourseInfo;

    @Code2Text(transformer = "X_YX")
    private String origsCollege;

    @Code2Text(transformer = "X_YX")
    private String subCollege;

    public String getOrigsCollege() {
        return origsCollege;
    }

    public void setOrigsCollege(String origsCollege) {
        this.origsCollege = origsCollege;
    }

    public String getSubCollege() {
        return subCollege;
    }

    public void setSubCollege(String subCollege) {
        this.subCollege = subCollege;
    }

    public String getStudentName()
    {
        return studentName;
    }
    
    public void setStudentName(String studentName)
    {
        this.studentName = studentName;
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
    
    public String getOrigsCourseName()
    {
        return origsCourseName;
    }
    
    public void setOrigsCourseName(String origsCourseName)
    {
        this.origsCourseName = origsCourseName;
    }

    public Double getOrigsCredits() {
        return origsCredits;
    }

    public void setOrigsCredits(Double origsCredits) {
        this.origsCredits = origsCredits;
    }

    public String getSubCourseName()
    {
        return subCourseName;
    }
    
    public void setSubCourseName(String subCourseName)
    {
        this.subCourseName = subCourseName;
    }

    public Double getSubCredits() {
        return subCredits;
    }

    public void setSubCredits(Double subCredits) {
        this.subCredits = subCredits;
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
    
    public String getOrigsCourseCode()
    {
        return origsCourseCode;
    }
    
    public void setOrigsCourseCode(String origsCourseCode)
    {
        this.origsCourseCode = origsCourseCode;
    }
    
    public String getSubCourseCode()
    {
        return subCourseCode;
    }
    
    public void setSubCourseCode(String subCourseCode)
    {
        this.subCourseCode = subCourseCode;
    }
    
}
