package com.server.edu.mutual.vo;

import java.io.Serializable;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;

@CodeI18n
public class PlanCourseTabVo implements Serializable
{
	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Long parentLabID;
    private String parentLabName;
    private Long labID;
    private String labName;
    private String courseCode;
    private Double score;
    /**
     * 是否选课
     */
    private String selCourse;
    private String courseName;
    /**
     * 学分
     */
    private Double credits;
    /**
     * 学时
     */
    private Double period;
    /**
     * 开课学期
     */
    @Code2Text(transformer="X_KKXQ")
    private String term; 
    /**
     * 开课学院
     */
    @Code2Text(transformer="X_YX")
    private String college;
    /**
     * 备注
     */
    private String remark;
    /**
     * 是否必xiu
     */
    private String compulsory;
    
    /**
     * 完成情况
     */
    private Integer isPass;
    
    private String tainingLevel;
    
    private Integer labGradeTabID;
    

    public Long getParentLabID()
    {
        return parentLabID;
    }

    public void setParentLabID(Long parentLabID)
    {
        this.parentLabID = parentLabID;
    }

    public String getParentLabName()
    {
        return parentLabName;
    }

    public void setParentLabName(String parentLabName)
    {
        this.parentLabName = parentLabName;
    }

    public Long getLabID()
    {
        return labID;
    }

    public void setLabID(Long labID)
    {
        this.labID = labID;
    }

    public String getLabName()
    {
        return labName;
    }

    public void setLabName(String labName)
    {
        this.labName = labName;
    }

    public String getCourseCode()
    {
        return courseCode;
    }

    public void setCourseCode(String courseCode)
    {
        this.courseCode = courseCode;
    }

    public Double getScore()
    {
        return score;
    }

    public void setScore(Double score)
    {
        this.score = score;
    }

    public String getSelCourse()
    {
        return selCourse;
    }

    public void setSelCourse(String selCourse)
    {
        this.selCourse = selCourse;
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

    public Double getPeriod()
    {
        return period;
    }

    public void setPeriod(Double period)
    {
        this.period = period;
    }

    public String getTerm()
    {
        return term;
    }

    public void setTerm(String term)
    {
        this.term = term;
    }

    public String getCollege()
    {
        return college;
    }

    public void setCollege(String college)
    {
        this.college = college;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getCompulsory()
    {
        return compulsory;
    }

    public void setCompulsory(String compulsory)
    {
        this.compulsory = compulsory;
    }

    public Integer getIsPass()
    {
        return isPass;
    }

    public void setIsPass(Integer isPass)
    {
        this.isPass = isPass;
    }

    
    public String getTainingLevel()
    {
        return tainingLevel;
    }

    public void setTainingLevel(String tainingLevel)
    {
        this.tainingLevel = tainingLevel;
    }

    public Integer getLabGradeTabID()
    {
        return labGradeTabID;
    }

    public void setLabGradeTabID(Integer labGradeTabID)
    {
        this.labGradeTabID = labGradeTabID;
    }

    public static long getSerialversionuid()
    {
        return serialVersionUID;
    }

    @Override
    public String toString()
    {
        return "PlanCourseTab [parentLabID=" + parentLabID + ", parentLabName="
            + parentLabName + ", labID=" + labID + ", labName=" + labName
            + ", courseCode=" + courseCode + ", score=" + score + ", selCourse="
            + selCourse + ", courseName=" + courseName + ", credits=" + credits
            + ", period=" + period + ", term=" + term + ", college=" + college
            + ", remark=" + remark + ", compulsory=" + compulsory + ", isPass="
            + isPass + "]";
    }
    
    

}
