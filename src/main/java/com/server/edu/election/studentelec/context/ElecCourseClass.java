package com.server.edu.election.studentelec.context;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

/**
 * 课程教学班
 * 
 * 
 * @author  OuYangGuoDong
 * @version  [版本号, 2019年2月26日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class ElecCourseClass extends ElecCourse
{
    private Long teacherClassId;
    
    private String teacherClassCode;
    
    private String teacherClassType;
    
    public ElecCourseClass()
    {
    }
    
    public ElecCourseClass(ElecCourse course)
    {
        this.setCampus(course.getCampus());
        this.setCourseCode(course.getCourseCode());
        this.setCourseName(course.getCourseName());
        this.setCredits(course.getCredits());
        this.setNameEn(course.getNameEn());
    }
    
    public Long getTeacherClassId()
    {
        return teacherClassId;
    }
    
    public void setTeacherClassId(Long teacherClassId)
    {
        this.teacherClassId = teacherClassId;
    }
    
    public String getTeacherClassCode()
    {
        return teacherClassCode;
    }
    
    public void setTeacherClassCode(String teacherClassCode)
    {
        this.teacherClassCode = teacherClassCode;
    }
    
    public String getTeacherClassType()
    {
        return teacherClassType;
    }
    
    public void setTeacherClassType(String teacherClassType)
    {
        this.teacherClassType = teacherClassType;
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash(this.teacherClassId, this.teacherClassCode);
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        
        if (obj instanceof ElecCourseClass)
        {
            ElecCourseClass o = (ElecCourseClass)obj;
            return StringUtils.equals(this.teacherClassCode, o.teacherClassCode)
                && this.teacherClassId.equals(o.teacherClassId);
        }
        return false;
    }
    
}
