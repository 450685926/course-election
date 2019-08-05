package com.server.edu.election.studentelec.context.bk;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.server.edu.election.studentelec.cache.TeachingClassCache;

/**
 * 已完成课程
 */
public class CompletedCourse
{
    
    private TeachingClassCache course;
    
    /**
     * 成绩
     */
    private String score;
    
    /**
     * 等级
     */
    private String rank;
    
    /**
     * 是否作弊
     */
    private boolean cheat;
    
    /**
     * 是否为优级
     */
    private boolean excellent;
    
    /**
     * 课程分类
     */
    private Long courseLabelId;
    
    private Integer isPass;
    
    public TeachingClassCache getCourse()
    {
        if (course == null)
        {
            course = new TeachingClassCache();
        }
        return course;
    }
    
    public void setCourse(TeachingClassCache course)
    {
        this.course = course;
    }
    
    public String getRank()
    {
        return rank;
    }
    
    public void setRank(String rank)
    {
        this.rank = rank;
    }
    
    public String getScore()
    {
        return score;
    }
    
    public void setScore(String score)
    {
        this.score = score;
    }
    
    public boolean isCheat()
    {
        return cheat;
    }
    
    public void setCheat(boolean cheat)
    {
        this.cheat = cheat;
    }
    
    public boolean isExcellent()
    {
        return excellent;
    }
    
    public void setExcellent(boolean excellent)
    {
        this.excellent = excellent;
    }
    
    public Integer getIsPass()
    {
        return isPass;
    }
    
    public void setIsPass(Integer isPass)
    {
        this.isPass = isPass;
    }
    
    public Long getCourseLabelId()
    {
        return courseLabelId;
    }
    
    public void setCourseLabelId(Long courseLabelId)
    {
        this.courseLabelId = courseLabelId;
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash(this.score,
            this.getCourse().getCourseCode(),
            this.getCourse().getCalendarId());
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        
        if (obj instanceof CompletedCourse)
        {
            CompletedCourse o = (CompletedCourse)obj;
            return StringUtils.equals(this.score, o.score)
                && StringUtils.equals(this.getCourse().getCourseCode(),
                    o.getCourse().getCourseCode())
                && Objects.equals(this.getCourse().getCalendarId(),
                    o.getCourse().getCalendarId());
        }
        return false;
    }
    
}