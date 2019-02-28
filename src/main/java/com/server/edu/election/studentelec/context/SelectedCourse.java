package com.server.edu.election.studentelec.context;

import java.util.List;

/**
 * 已选择课程
 */
public class SelectedCourse extends ElecCourse
{
    /** 选择该门课时的轮次 */
    private Integer selectedRound;
    
    /** 是否公选课 */
    private boolean isPublicElec;
    
    /** 上课周 */
    private List<Integer> weeks;
    
    /** 时间*/
    private TimeUnit time;
    
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
    
    public boolean isPublicElec()
    {
        return isPublicElec;
    }
    
    public void setPublicElec(boolean publicElec)
    {
        isPublicElec = publicElec;
    }
    
    public List<Integer> getWeeks()
    {
        return weeks;
    }
    
    public void setWeeks(List<Integer> weeks)
    {
        this.weeks = weeks;
    }
    
    public TimeUnit getTime()
    {
        return time;
    }
    
    public void setTime(TimeUnit time)
    {
        this.time = time;
    }
}