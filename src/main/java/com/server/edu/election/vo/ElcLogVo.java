package com.server.edu.election.vo;

import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.election.entity.ElcLog;

@CodeI18n
public class ElcLogVo extends ElcLog
{
    
    private static final long serialVersionUID = 1L;
    /**选课*/
    public static final Integer TYPE_1 = 1;
    /**退课*/
    public static final Integer TYPE_2 = 2;
    /**1自选，2代选*/
    public static final Integer MODE_1 = 1;
    /**1自选，2代选*/
    public static final Integer MODE_2 = 2;
    
    private String studentName;
    
    /**校区*/
    @Code2Text(transformer = "X_XQ")
    private String campus;
    
    /**专业*/
    @Code2Text(DictTypeEnum.G_ZY)
    private String profession;
    
    public String getStudentName()
    {
        return studentName;
    }
    
    public void setStudentName(String studentName)
    {
        this.studentName = studentName;
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
    
}
