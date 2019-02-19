package com.server.edu.election.vo;

import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;

public class TeachingClassVo
{
    @Code2Text(value=DictTypeEnum.X_XQ)
    private String campus;
    private String courseName;
    public String getCampus()
    {
        return campus;
    }
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
