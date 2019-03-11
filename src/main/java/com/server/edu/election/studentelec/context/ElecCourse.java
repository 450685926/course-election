package com.server.edu.election.studentelec.context;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

public class ElecCourse
{
    private String courseCode;
    
    private String courseName;
    
    private Double credits;
    
    private String nameEn;
    
    /**校区*/
    private String campus;
    
    /** 是否公选课 */
    private boolean publicElec;

    /**实践周*/
    private Integer weekType;

    /**学期周学时*/
    private String semester;

    public Integer getWeekType() {
        return weekType;
    }

    public void setWeekType(Integer weekType) {
        this.weekType = weekType;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getCourseCode()
    {
        return courseCode;
    }
    
    public void setCourseCode(String courseCode)
    {
        this.courseCode = courseCode;
    }
    
    public String getCourseName()
    {
        return courseName;
    }
    
    public void setCourseName(String courseName)
    {
        this.courseName = courseName;
    }
    
    public Double getCredits()
    {
        return credits;
    }
    
    public void setCredits(Double credits)
    {
        this.credits = credits;
    }
    
    public String getNameEn()
    {
        return nameEn;
    }
    
    public void setNameEn(String nameEn)
    {
        this.nameEn = nameEn;
    }
    
    public String getCampus()
    {
        return campus;
    }
    
    public void setCampus(String campus)
    {
        this.campus = campus;
    }
    
    public boolean isPublicElec()
    {
        return publicElec;
    }
    
    public void setPublicElec(boolean publicElec)
    {
        this.publicElec = publicElec;
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hashCode(this.courseCode);
    }
    
    @Override
    public boolean equals(Object obj)
    {
        return super.equals(obj) || (obj instanceof ElecCourse && StringUtils
            .equals(this.courseCode, ((ElecCourse)obj).courseCode));
    }
    
}
