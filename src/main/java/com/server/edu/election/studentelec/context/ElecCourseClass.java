package com.server.edu.election.studentelec.context;

/**
 * 课程教学班
 * 
 * 
 * @author  OuYangGuoDong
 * @version  [版本号, 2019年2月26日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class ElecCourseClass extends ElecCourse
{
    private Long teacherClassId;
    
    private String teacherClassCode;
    
    public ElecCourseClass()
    {
    }
    
    public ElecCourseClass(ElecCourse course)
    {
        this.setCampus(course.getCampus());
        this.setCourseCode(course.getCourseCode());
        this.setCourseName(course.getCourseName());
        this.setCredits(course.getCredits());
        this.setNameEn(course.getNameEn());
    }
    
    public Long getTeacherClassId()
    {
        return teacherClassId;
    }
    
    public void setTeacherClassId(Long teacherClassId)
    {
        this.teacherClassId = teacherClassId;
    }
    
    public String getTeacherClassCode()
    {
        return teacherClassCode;
    }
    
    public void setTeacherClassCode(String teacherClassCode)
    {
        this.teacherClassCode = teacherClassCode;
    }
    
}
