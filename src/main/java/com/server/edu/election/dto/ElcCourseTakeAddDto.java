package com.server.edu.election.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.server.edu.common.validator.AddGroup;
import com.server.edu.common.validator.DelGroup;

/**
 * 加课退课DTO
 * 
 * 
 * @author  OuYangGuoDong
 * @version  [版本号, 2019年2月16日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class ElcCourseTakeAddDto
{
    @NotNull(groups = {AddGroup.class, DelGroup.class})
    private Long calendarId;
    
    /**
     * 学号
     */
    @NotNull(groups = {AddGroup.class})
    private List<String> studentIds;
    
    /**
     * 教学班ID
     */
    @NotEmpty(groups = {AddGroup.class})
    private List<Long> teachingClassIds;

    /**
     * 模式
     * */
    @NotEmpty(groups = {AddGroup.class})
    private Integer model;
    
    private String studentId;
    
    private String teachingClassCode;
    
    public Long getCalendarId()
    {
        return calendarId;
    }
    
    public void setCalendarId(Long calendarId)
    {
        this.calendarId = calendarId;
    }
    
    public List<String> getStudentIds()
    {
        return studentIds;
    }
    
    public void setStudentIds(List<String> studentIds)
    {
        this.studentIds = studentIds;
    }
    
    public List<Long> getTeachingClassIds()
    {
        return teachingClassIds;
    }
    
    public void setTeachingClassIds(List<Long> teachingClassIds)
    {
        this.teachingClassIds = teachingClassIds;
    }
    
    public String getStudentId()
    {
        return studentId;
    }
    
    public void setStudentId(String studentId)
    {
        this.studentId = studentId;
    }
    
    public String getTeachingClassCode()
    {
        return teachingClassCode;
    }
    
    public void setTeachingClassCode(String teachingClassCode)
    {
        this.teachingClassCode = teachingClassCode;
    }

    public Integer getModel() {
        return model;
    }

    public void setModel(Integer model) {
        this.model = model;
    }
}
