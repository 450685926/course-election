package com.server.edu.election.studentelec.context;

import java.util.List;

public class ClassTimeUnit
{
    private Long teachClassId;
    
    private Long arrangeTimeId;
    
    private int timeStart;
    
    private int timeEnd;
    
    private int dayOfWeek;
    
    private List<Integer> weeks;
    
    private String value;
    
    private String teacherCode;

    private String roomId;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

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
    
    public String getValue()
    {
        return value;
    }
    
    public void setValue(String value)
    {
        this.value = value;
    }
    
    public List<Integer> getWeeks()
    {
        return weeks;
    }
    
    public void setWeeks(List<Integer> weeks)
    {
        this.weeks = weeks;
    }
    
    public String getTeacherCode()
    {
        return teacherCode;
    }
    
    public void setTeacherCode(String teacherCode)
    {
        this.teacherCode = teacherCode;
    }

	@Override
	public String toString() {
		return "ClassTimeUnit [teachClassId=" + teachClassId + ", arrangeTimeId=" + arrangeTimeId + ", timeStart="
				+ timeStart + ", timeEnd=" + timeEnd + ", dayOfWeek=" + dayOfWeek + ", weeks=" + weeks + ", value="
				+ value + ", teacherCode=" + teacherCode + "]";
	}
    
}
