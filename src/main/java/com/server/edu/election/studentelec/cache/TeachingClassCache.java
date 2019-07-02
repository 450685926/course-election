package com.server.edu.election.studentelec.cache;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.server.edu.election.studentelec.context.ClassTimeUnit;
import com.server.edu.election.studentelec.context.ElecCourse;

/**
 * 教学班缓存对象
 * 
 */
public class TeachingClassCache extends ElecCourse
{
    private Long teachClassId;
    
    private String teachClassCode;
    
    private String teachClassType;
    
    /** 是否实践课*/
    private Boolean practice = Boolean.FALSE;
    
    /** 是否重修班*/
    private Boolean retraining = Boolean.FALSE;
    
    /** 最大人数 */
    private Integer maxNumber;
    
    /** 当前人数 */
    private Integer currentNumber;
    
    /** 上课时间按教学周拆分集合 */
    private List<ClassTimeUnit> times;
    
    private String teacherCode;
    
    private String teacherName;

    /**开课学院*/
    private String startCollege;

    public TeachingClassCache(){}

    public TeachingClassCache(ElecCourse course)
    {
        this.setCampus(course.getCampus());
        this.setCourseCode(course.getCourseCode());
        this.setCourseName(course.getCourseName());
        this.setCredits(course.getCredits());
        this.setNameEn(course.getNameEn());
        this.setNature(course.getNature());
    }
    
    public String getTeacherCode()
    {
        return teacherCode;
    }

    public void setTeacherCode(String teacherCode)
    {
        this.teacherCode = teacherCode;
    }

    public String getTeacherName()
    {
        return teacherName;
    }

    public void setTeacherName(String teacherName)
    {
        this.teacherName = teacherName;
    }

    public Long getTeachClassId()
    {
        return teachClassId;
    }
    
    public void setTeachClassId(Long teachClassId)
    {
        this.teachClassId = teachClassId;
    }
    
    public String getTeachClassCode()
    {
        return teachClassCode;
    }
    
    public void setTeachClassCode(String teachClassCode)
    {
        this.teachClassCode = teachClassCode;
    }
    
    public String getTeachClassType()
    {
        return teachClassType;
    }
    
    public void setTeachClassType(String teachClassType)
    {
        this.teachClassType = teachClassType;
    }
    
    public Boolean getPractice()
    {
        return practice;
    }
    
    public void setPractice(Boolean practice)
    {
        this.practice = practice;
    }
    
    public Boolean getRetraining()
    {
        return retraining;
    }
    
    public void setRetraining(Boolean retraining)
    {
        this.retraining = retraining;
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
    
    public List<ClassTimeUnit> getTimes()
    {
        return times;
    }
    
    public void setTimes(List<ClassTimeUnit> times)
    {
        this.times = times;
    }

    public String getStartCollege() {
        return startCollege;
    }

    public void setStartCollege(String startCollege) {
        this.startCollege = startCollege;
    }

    @JsonIgnore
    public String getCourseCodeAndClassCode()
    {
        return String
            .format("%s[%s]", this.getCourseCode(), this.getTeachClassCode());
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash(this.teachClassId, this.teachClassCode);
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
            return StringUtils.equals(this.teachClassCode, o.teachClassCode)
                && Objects.equals(this.teachClassId, o.teachClassId);
        }
        return false;
    }
    
}
