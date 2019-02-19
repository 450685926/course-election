package com.server.edu.election.vo;

import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.election.entity.TeachingClass;

public class TeachingClassVo extends TeachingClass
{
    @Code2Text(value=DictTypeEnum.X_XQ)
    private String campus;
    private String courseName;
    @Override
    public String getCampus()
    {
        return campus;
    }
    @Override
    public void setCampus(String campus)
    {
        this.campus = campus;
    }
    public String getCourseName()
    {
        return courseName;
    }
    public void setCourseName(String courseName)
    {
        this.courseName = courseName;
    }
    
    
}
