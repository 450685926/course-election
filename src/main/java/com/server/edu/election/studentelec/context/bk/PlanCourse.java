package com.server.edu.election.studentelec.context.bk;

import com.server.edu.election.studentelec.context.ElecCourse;

public class PlanCourse
{
    
    private ElecCourse course;
    
    /**课程分类*/
    private Long label;
    
    /**开课学院*/
    private String faculty;
    
    /**实践周*/
    private Integer weekType;
    
    /**学期周学时*/
    private String semester;
    
    /**个人替代课程*/
    private String subCourseCode;
    
    public ElecCourse getCourse()
    {
        if (course == null)
        {
            course = new ElecCourse();
        }
        return course;
    }
    
    public void setCourse(ElecCourse course)
    {
        this.course = course;
    }
    
    public String getSubCourseCode()
    {
        return subCourseCode;
    }
    
    public void setSubCourseCode(String subCourseCode)
    {
        this.subCourseCode = subCourseCode;
    }
    
    public Integer getWeekType()
    {
        return weekType;
    }
    
    public void setWeekType(Integer weekType)
    {
        this.weekType = weekType;
    }
    
    public String getSemester()
    {
        return semester;
    }
    
    public void setSemester(String semester)
    {
        this.semester = semester;
    }
    
    public Long getLabel()
    {
        return label;
    }
    
    public void setLabel(Long label)
    {
        this.label = label;
    }
    
    public String getFaculty()
    {
        return faculty;
    }
    
    public void setFaculty(String faculty)
    {
        this.faculty = faculty;
    }
    
}
