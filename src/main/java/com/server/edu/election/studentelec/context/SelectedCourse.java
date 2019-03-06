package com.server.edu.election.studentelec.context;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;

/**
 * 已选择课程
 */
public class SelectedCourse extends ElecCourse
{
    /**是否重修课*/
    private boolean isRebuildElec;
    
    /** 上课时间 */
    private List<TimeUnit> times;
    
    /** 教学班Id */
    private Long teachingclassId;
    
    /**
     * 选课对象(1学生，2教务员，3管理员)
     */
    private Integer chooseObj;
    
    /**
     * 修读类别(1正常修读,2重修,3免修不免考,4免修)
     */
    @Code2Text(DictTypeEnum.X_XDLX)
    private Integer courseTakeType;
    
    /**
     * 第几轮
     */
    private Integer turn;
    
    public SelectedCourse()
    {
        
    }
    
    public SelectedCourse(ElecCourse course)
    {
        this.setCampus(course.getCampus());
        this.setCourseCode(course.getCourseCode());
        this.setCourseName(course.getCourseName());
        this.setCredits(course.getCredits());
        this.setNameEn(course.getNameEn());
    }
    
    public List<TimeUnit> getTimes()
    {
        return times;
    }
    
    public void setTimes(List<TimeUnit> times)
    {
        this.times = times;
    }
    
    public Integer getChooseObj()
    {
        return chooseObj;
    }
    
    public void setChooseObj(Integer chooseObj)
    {
        this.chooseObj = chooseObj;
    }
    
    public Long getTeachingclassId()
    {
        return teachingclassId;
    }
    
    public void setTeachingclassId(Long teachingclassId)
    {
        this.teachingclassId = teachingclassId;
    }
    
    public boolean isRebuildElec()
    {
        return isRebuildElec;
    }
    
    public void setRebuildElec(boolean rebuildElec)
    {
        isRebuildElec = rebuildElec;
    }
    
    public Integer getCourseTakeType()
    {
        return courseTakeType;
    }
    
    public void setCourseTakeType(Integer courseTakeType)
    {
        this.courseTakeType = courseTakeType;
    }
    
    public Integer getTurn()
    {
        return turn;
    }
    
    public void setTurn(Integer turn)
    {
        this.turn = turn;
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash(this.teachingclassId, this.getCourseCode());
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
            return Objects.equals(this.teachingclassId, o.teachingclassId)
                && StringUtils.equals(this.getCourseCode(), o.getCourseCode());
        }
        return false;
    }
    
}