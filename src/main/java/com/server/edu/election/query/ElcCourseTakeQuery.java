package com.server.edu.election.query;

public class ElcCourseTakeQuery
{
    private String studentId;

    /**
     * 校历ID（学年学期）
     */
    private Long calendarId;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 教学班ID
     */
    private Long teachingClassId;

    /**
     * 修读类别(1正常修读,2重修,3免修不免考,4免修)
     */
    private Integer courseTakeType;

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

    public Long getCourseId()
    {
        return courseId;
    }

    public void setCourseId(Long courseId)
    {
        this.courseId = courseId;
    }

    public Long getTeachingClassId()
    {
        return teachingClassId;
    }

    public void setTeachingClassId(Long teachingClassId)
    {
        this.teachingClassId = teachingClassId;
    }

    public Integer getCourseTakeType()
    {
        return courseTakeType;
    }

    public void setCourseTakeType(Integer courseTakeType)
    {
        this.courseTakeType = courseTakeType;
    }


}
