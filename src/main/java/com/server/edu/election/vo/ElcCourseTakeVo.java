package com.server.edu.election.vo;

import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.election.entity.ElcCourseTake;

@CodeI18n
public class ElcCourseTakeVo extends ElcCourseTake
{
    private static final long serialVersionUID = 1L;
    
    private String studentName;
    
    private String courseName;
    
    /**学分*/
    private Double credits;
    
    /**校区*/
    @Code2Text(transformer = "X_XQ")
    private String campus;
    
    /**专业*/
    @Code2Text(DictTypeEnum.G_ZY)
    private String profession;
    
    /**教学班编号*/
    private String teachingClassCode;
    
    public String getStudentName()
    {
        return studentName;
    }
    
    public void setStudentName(String studentName)
    {
        this.studentName = studentName;
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
    
    public String getCampus()
    {
        return campus;
    }
    
    public void setCampus(String campus)
    {
        this.campus = campus;
    }
    
    public String getProfession()
    {
        return profession;
    }
    
    public void setProfession(String profession)
    {
        this.profession = profession;
    }
    
    public String getTeachingClassCode()
    {
        return teachingClassCode;
    }
    
    public void setTeachingClassCode(String teachingClassCode)
    {
        this.teachingClassCode = teachingClassCode;
    }
    
}
