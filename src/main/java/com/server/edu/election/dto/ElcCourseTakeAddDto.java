package com.server.edu.election.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.bind.annotation.RequestPart;

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
     * 教学班ID
     */
    private String courseCode;

    /**
     * 模式
     * */
    @NotNull(groups = {AddGroup.class,DelGroup.class})
    private Integer mode;
    
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

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

	public String getCourseCode() {
		return courseCode;
	}

	public void setCourseCode(String courseCode) {
		this.courseCode = courseCode;
	}

}
