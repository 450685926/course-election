package com.server.edu.election.studentelec.context;

import java.util.List;

public class ClassTimeUnit
{
    private Long teachClassId;
    
    private Long arrangeTimeId;
    
    private int timeStart;
    
    private int timeEnd;
    
    private int dayOfWeek;
    
    private Integer weekNumber;
    
    private List<Integer> weeks;
    
    public Long getTeachClassId()
    {
        return teachClassId;
    }
    
    public void setTeachClassId(Long teachClassId)
    {
        this.teachClassId = teachClassId;
    }
    
    public Long getArrangeTimeId()
    {
        return arrangeTimeId;
    }
    
    public void setArrangeTimeId(Long arrangeTimeId)
    {
        this.arrangeTimeId = arrangeTimeId;
    }
    
    public int getTimeStart()
    {
        return timeStart;
    }
    
    public void setTimeStart(int timeStart)
    {
        this.timeStart = timeStart;
    }
    
    public int getTimeEnd()
    {
        return timeEnd;
    }
    
    public void setTimeEnd(int timeEnd)
    {
        this.timeEnd = timeEnd;
    }
    
    public int getDayOfWeek()
    {
        return dayOfWeek;
    }
    
    public void setDayOfWeek(int dayOfWeek)
    {
        this.dayOfWeek = dayOfWeek;
    }
    
    public Integer getWeekNumber()
    {
        return weekNumber;
    }
    
    public void setWeekNumber(Integer weekNumber)
    {
        this.weekNumber = weekNumber;
    }
    
    public List<Integer> getWeeks()
    {
        return weeks;
    }
    
    public void setWeeks(List<Integer> weeks)
    {
        this.weeks = weeks;
    }
    
}
