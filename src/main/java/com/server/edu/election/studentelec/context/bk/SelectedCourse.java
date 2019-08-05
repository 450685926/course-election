package com.server.edu.election.studentelec.context.bk;

import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.election.studentelec.cache.TeachingClassCache;

/**
 * 已选择课程
 */
@CodeI18n
public class SelectedCourse
{
    private TeachingClassCache teachingClass;
    
    /**
     * 选课对象(1学生，2教务员，3管理员)
     */
    private Integer chooseObj;
    
    /**
     * 修读类别(1正常修读,2重修,3免修不免考,4免修)
     */
    @Code2Text(DictTypeEnum.X_XDLX)
    private Integer courseTakeType;
    
    @Code2Text(DictTypeEnum.X_KCFL)
    private String label;
    
    @Code2Text(transformer = " X_KSLX")
    private String assessmentMode;
    
    /**
     * 第几轮
     */
    private Integer turn;
    
    private Integer isApply;
    
    public SelectedCourse()
    {
        
    }
    
    public SelectedCourse(TeachingClassCache teachingClass)
    {
        this.teachingClass = teachingClass;
    }
    
    public TeachingClassCache getTeachingClass()
    {
        if (teachingClass == null)
        {
            teachingClass = new TeachingClassCache();
        }
        return teachingClass;
    }
    
    public void setTeachingClass(TeachingClassCache teachingClass)
    {
        this.teachingClass = teachingClass;
    }
    
    public Integer getChooseObj()
    {
        return chooseObj;
    }
    
    public void setChooseObj(Integer chooseObj)
    {
        this.chooseObj = chooseObj;
    }
    
    public Integer getCourseTakeType()
    {
        return courseTakeType;
    }
    
    public void setCourseTakeType(Integer courseTakeType)
    {
        this.courseTakeType = courseTakeType;
    }
    
    public String getLabel()
    {
        return label;
    }
    
    public void setLabel(String label)
    {
        this.label = label;
    }
    
    public String getAssessmentMode()
    {
        return assessmentMode;
    }
    
    public void setAssessmentMode(String assessmentMode)
    {
        this.assessmentMode = assessmentMode;
    }
    
    public Integer getTurn()
    {
        return turn;
    }
    
    public void setTurn(Integer turn)
    {
        this.turn = turn;
    }
    
    public Integer getIsApply()
    {
        return isApply;
    }
    
    public void setIsApply(Integer isApply)
    {
        this.isApply = isApply;
    }
    
    @Override
    public int hashCode()
    {
        return this.getTeachingClass().hashCode();
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        
        if (obj instanceof SelectedCourse)
        {
            SelectedCourse o = (SelectedCourse)obj;
            if (this.teachingClass == o.getTeachingClass())
            {
                return true;
            }
            return this.teachingClass != null
                && this.teachingClass.equals(o.getTeachingClass());
        }
        return false;
    }
}