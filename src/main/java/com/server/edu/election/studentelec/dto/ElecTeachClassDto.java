package com.server.edu.election.studentelec.dto;

/**
 * 选课\退课教学班
 * 
 * 
 * @author  OuYangGuoDong
 * @version  [版本号, 2019年3月13日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class ElecTeachClassDto
{
    /**教学班ID*/
    private Long teachClassId;
    
    /**课程代码*/
    private String courseCode;
    
    /**课程分类*/
    private Long label;
    
    public Long getTeachClassId()
    {
        return teachClassId;
    }
    
    public void setTeachClassId(Long teachClassId)
    {
        this.teachClassId = teachClassId;
    }
    
    public String getCourseCode()
    {
        return courseCode;
    }
    
    public void setCourseCode(String courseCode)
    {
        this.courseCode = courseCode;
    }
    
    public Long getLabel()
    {
        return label;
    }
    
    public void setLabel(Long label)
    {
        this.label = label;
    }
    
}
