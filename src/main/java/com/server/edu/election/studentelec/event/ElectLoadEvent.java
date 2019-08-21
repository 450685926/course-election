package com.server.edu.election.studentelec.event;

import org.springframework.context.ApplicationEvent;

/**
 * 选课事件
 * 
 * 
 * @author  OuYangGuoDong
 * @version  [版本号, 2019年3月17日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class ElectLoadEvent extends ApplicationEvent
{
    
    private static final long serialVersionUID = 1L;
    
    private Long calendarId;
    
    private String studentId;
    
    public ElectLoadEvent(Long calendarId, String studentId)
    {
        super(calendarId);
        this.calendarId = calendarId;
        this.studentId = studentId;
    }
    
    @Override
    public Long getSource()
    {
        return (Long)super.getSource();
    }
    
    public Long getCalendarId()
    {
        return calendarId;
    }
    
    public String getStudentId()
    {
        return studentId;
    }
    
}
