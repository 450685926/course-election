package com.server.edu.election.dto;

import javax.validation.constraints.NotNull;

/**
 * 自动剔除超过人数
 * 
 * 
 * @author  OuYangGuoDong
 * @version  [版本号, 2019年2月21日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class AutoRemoveDto
{
    @NotNull
    private Long teachingClassId;
    
    /**删除特殊学生*/
    private Boolean invincibleStu;
    
    /**删除选先学生*/
    private Boolean affinityStu;
    
    /**剔除非指定年级、专业的学生*/
    private Boolean gradAndPre;
    
    /**按教学班选课限制筛选*/
    private Boolean classElcCondition;
    
    /**按教学班对应专业配比人数进行筛选*/
    private Boolean gradAndPrePeople;
    
    public Long getTeachingClassId()
    {
        return teachingClassId;
    }
    
    public void setTeachingClassId(Long teachingClassId)
    {
        this.teachingClassId = teachingClassId;
    }
    
    public Boolean getInvincibleStu()
    {
        return invincibleStu;
    }
    
    public void setInvincibleStu(Boolean invincibleStu)
    {
        this.invincibleStu = invincibleStu;
    }
    
    public Boolean getAffinityStu()
    {
        return affinityStu;
    }
    
    public void setAffinityStu(Boolean affinityStu)
    {
        this.affinityStu = affinityStu;
    }
    
    public Boolean getGradAndPre()
    {
        return gradAndPre;
    }
    
    public void setGradAndPre(Boolean gradAndPre)
    {
        this.gradAndPre = gradAndPre;
    }
    
    public Boolean getClassElcCondition()
    {
        return classElcCondition;
    }
    
    public void setClassElcCondition(Boolean classElcCondition)
    {
        this.classElcCondition = classElcCondition;
    }
    
    public Boolean getGradAndPrePeople()
    {
        return gradAndPrePeople;
    }
    
    public void setGradAndPrePeople(Boolean gradAndPrePeople)
    {
        this.gradAndPrePeople = gradAndPrePeople;
    }
    
}
