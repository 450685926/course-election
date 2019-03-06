package com.server.edu.election.studentelec.context;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

/**
 * 已选择课程
 */
public class SelectedCourse extends ElecCourse
{
    /** 选择该门课时的轮次 */
    private Integer selectedRound;
    
    /** 上课时间 */
    private List<TimeUnit> times;
    
    /** 教学班Id */
    private Long teachingclassId;
    
    /**
     * 选课对象(1学生，2教务员，3管理员)
     */
    private Integer chooseObj;
    
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
    
    public Integer getSelectedRound()
    {
        return selectedRound;
    }
    
    public void setSelectedRound(Integer selectedRound)
    {
        this.selectedRound = selectedRound;
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