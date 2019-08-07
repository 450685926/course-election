package com.server.edu.election.dto;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.Code2Text.DataType;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.dictionary.translator.ClassRoomTranslator;
import com.server.edu.dictionary.translator.TeacherTranslator;

/**
 * 教学班的上课时间和地点类
 * 
 * 
 * @author  OuYangGuoDong
 * @version  [版本号, 2019年6月5日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@CodeI18n
public class TeacherClassTimeRoom
{
    private Long teachClassId;
    
    private Long arrangeTimeId;
    
    private int timeStart;
    
    private int timeEnd;
    
    private int dayOfWeek;
    
    private Integer weekNumber;
    
    @Code2Text(translator = ClassRoomTranslator.class, dataType = DataType.SPLIT)
    private String roomId;
    
    @Code2Text(translator = TeacherTranslator.class, dataType = DataType.SPLIT)
    private String teacherCode;
    
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
    
    public String getRoomId()
    {
        return roomId;
    }
    
    public void setRoomId(String roomId)
    {
        this.roomId = roomId;
    }
    
    public String getTeacherCode()
    {
        return teacherCode;
    }
    
    public void setTeacherCode(String teacherCode)
    {
        this.teacherCode = teacherCode;
    }
    
}
