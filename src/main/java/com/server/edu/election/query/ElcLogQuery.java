package com.server.edu.election.query;

import java.util.Date;
import java.util.List;

import com.server.edu.election.entity.ElcLog;

public class ElcLogQuery extends ElcLog
{
    private static final long serialVersionUID = 1L;
    
    private List<String> studentIds;
    
    private String keyword;
    
    private String deptId;
    
    /**日志查询时间段*/
    private Date startTime;
    
    private Date endTime;
    
    /** 课程类型 1体育课，2英语课*/
    private Integer courseType;
    
    private List<String> speCourseCodes;

    private List<String> faculties;

    public List<String> getFaculties() {
        return faculties;
    }

    public void setFaculties(List<String> faculties) {
        this.faculties = faculties;
    }
    
    public List<String> getStudentIds()
    {
        return studentIds;
    }
    
    public void setStudentIds(List<String> studentIds)
    {
        this.studentIds = studentIds;
    }
    
    public String getKeyword()
    {
        return keyword;
    }
    
    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }
    
    public String getDeptId()
    {
        return deptId;
    }
    
    public void setDeptId(String deptId)
    {
        this.deptId = deptId;
    }
    
    public Date getStartTime()
    {
        return startTime;
    }
    
    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }
    
    public Date getEndTime()
    {
        return endTime;
    }
    
    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }
    
    public Integer getCourseType()
    {
        return courseType;
    }
    
    public void setCourseType(Integer courseType)
    {
        this.courseType = courseType;
    }
    
    public List<String> getSpeCourseCodes()
    {
        return speCourseCodes;
    }
    
    public void setSpeCourseCodes(List<String> speCourseCodes)
    {
        this.speCourseCodes = speCourseCodes;
    }
    
}
