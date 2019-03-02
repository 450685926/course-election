package com.server.edu.election.studentelec.cache;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.server.edu.election.studentelec.context.ElecCourse;

/**
 * 教学班缓存对象
 * 
 */
public class TeachingClassCache extends ElecCourse
{
    private Long teacherClassId;
    
    private String teacherClassCode;
    
    private String teacherClassType;
    
    /** 是否实践课*/
    private Boolean isPractice = false;
    
    /** 是否重修班*/
    private Boolean isRetraining = false;
    
    /** 是否公选课*/
    private Boolean isPublicElective = false;
    
    /** 最大人数 */
    private Integer maxNumber;
    
    /** 当前人数 */
    private Integer currentNumber;
    
    public TeachingClassCache()
    {
    }
    
    public TeachingClassCache(ElecCourse course)
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
    
    public Boolean isPractice()
    {
        return isPractice;
    }
    
    public void setPractice(Boolean practice)
    {
        isPractice = practice;
    }
    
    public boolean isRetraining()
    {
        return isRetraining;
    }
    
    public void setRetraining(Boolean retraining)
    {
        isRetraining = retraining;
    }
    
    public Boolean isPublicElective()
    {
        return isPublicElective;
    }
    
    public void setPublicElective(Boolean publicElective)
    {
        isPublicElective = publicElective;
    }
    
    public Integer getMaxNumber()
    {
        return maxNumber;
    }
    
    public void setMaxNumber(Integer maxNumber)
    {
        this.maxNumber = maxNumber;
    }
    
    public Integer getCurrentNumber()
    {
        return currentNumber;
    }
    
    public void setCurrentNumber(Integer currentNumber)
    {
        this.currentNumber = currentNumber;
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
        
        if (obj instanceof TeachingClassCache)
        {
            TeachingClassCache o = (TeachingClassCache)obj;
            return StringUtils.equals(this.teacherClassCode, o.teacherClassCode)
                && this.teacherClassId.equals(o.teacherClassId);
        }
        return false;
    }
    
}
