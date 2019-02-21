package com.server.edu.election.query;

import javax.validation.constraints.NotNull;

public class ElcCourseTakeQuery
{
    /**
     * 校历ID（学年学期）
     */
    @NotNull
    private Long calendarId;
    
    private String studentId;
    /**学生姓名*/
    private String stuName;
    /**学生学院*/
    private String stuFaculty;
    /**学生专业*/
    private String stuProfession;
    /**
     * 课程编号
     */
    private String courseCode;

    /**
     * 教学班编号
     */
    private String teachingClassCode;

    /**
     * 修读类别(1正常修读,2重修,3免修不免考,4免修)
     */
    private Integer courseTakeType;

    /**
     * 选课模式
     * */
    private Integer mode;

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public String getStudentId()
    {
        return studentId;
    }

    public void setStudentId(String studentId)
    {
        this.studentId = studentId;
    }

    public Long getCalendarId()
    {
        return calendarId;
    }

    public void setCalendarId(Long calendarId)
    {
        this.calendarId = calendarId;
    }

    public Integer getCourseTakeType()
    {
        return courseTakeType;
    }

    public void setCourseTakeType(Integer courseTakeType)
    {
        this.courseTakeType = courseTakeType;
    }

    public String getStuName()
    {
        return stuName;
    }

    public void setStuName(String stuName)
    {
        this.stuName = stuName;
    }

    public String getStuFaculty()
    {
        return stuFaculty;
    }

    public void setStuFaculty(String stuFaculty)
    {
        this.stuFaculty = stuFaculty;
    }

    public String getStuProfession()
    {
        return stuProfession;
    }

    public void setStuProfession(String stuProfession)
    {
        this.stuProfession = stuProfession;
    }

    public String getCourseCode()
    {
        return courseCode;
    }

    public void setCourseCode(String courseCode)
    {
        this.courseCode = courseCode;
    }

    public String getTeachingClassCode()
    {
        return teachingClassCode;
    }

    public void setTeachingClassCode(String teachingClassCode)
    {
        this.teachingClassCode = teachingClassCode;
    }
    
}
