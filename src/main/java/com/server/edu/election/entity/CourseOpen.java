package com.server.edu.election.entity;

import java.util.Date;
import java.util.Objects;

import com.server.edu.common.BaseEntity;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;

@CodeI18n
public class CourseOpen extends BaseEntity
{
    private Long id;
    
    //校历id
    private Long calendarId;
    
    private String courseCode;
    
    private String courseName;
    
    private String courseNameEn;
    
    private Integer isElective;
    
    @Code2Text(transformer = "X_YX")
    private String faculty;
    
    @Code2Text(transformer = "X_KKXQ")
    private String term;
    
    private Integer crossTerm;
    
    @Code2Text(transformer = "X_PYCC")
    private String trainingLevel;
    
    @Code2Text(transformer = "X_XXXS")
    private String formLearning;
    
    @Code2Text(transformer = "X_KCXZ")
    private String nature;
    
    private Double period;
    
    private Double credits;
    
    private Integer number;
    
    private Integer isOpen;
    
    private String remark;
    
    private Date createdAt;
    
    private Date updatedAt;
    
    public Long getId()
    {
        return id;
    }
    
    public void setId(Long id)
    {
        this.id = id;
    }
    
    public String getCourseCode()
    {
        return courseCode;
    }
    
    public void setCourseCode(String courseCode)
    {
        this.courseCode = courseCode == null ? null : courseCode.trim();
    }
    
    public Integer getIsElective()
    {
        return isElective;
    }
    
    public void setIsElective(Integer isElective)
    {
        this.isElective = isElective;
    }
    
    public String getFaculty()
    {
        return faculty;
    }
    
    public void setFaculty(String faculty)
    {
        this.faculty = faculty == null ? null : faculty.trim();
    }
    
    public String getTrainingLevel()
    {
        return trainingLevel;
    }
    
    public void setTrainingLevel(String trainingLevel)
    {
        this.trainingLevel =
            trainingLevel == null ? null : trainingLevel.trim();
    }
    
    public String getFormLearning()
    {
        return formLearning;
    }
    
    public void setFormLearning(String formLearning)
    {
        this.formLearning = formLearning == null ? null : formLearning.trim();
    }
    
    public Integer getNumber()
    {
        return number;
    }
    
    public void setNumber(Integer number)
    {
        this.number = number;
    }
    
    public Integer getIsOpen()
    {
        return isOpen;
    }
    
    public void setIsOpen(Integer isOpen)
    {
        this.isOpen = isOpen;
    }
    
    public String getRemark()
    {
        return remark;
    }
    
    public void setRemark(String remark)
    {
        this.remark = remark == null ? null : remark.trim();
    }
    
    public Date getCreatedAt()
    {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = createdAt;
    }
    
    public Date getUpdatedAt()
    {
        return updatedAt;
    }
    
    public void setUpdatedAt(Date updatedAt)
    {
        this.updatedAt = updatedAt;
    }
    
    public Long getCalendarId()
    {
        return calendarId;
    }
    
    public void setCalendarId(Long calendarId)
    {
        this.calendarId = calendarId;
    }
    
    public String getCourseName()
    {
        return courseName;
    }
    
    public void setCourseName(String courseName)
    {
        this.courseName = courseName;
    }
    
    public String getCourseNameEn()
    {
        return courseNameEn;
    }
    
    public void setCourseNameEn(String courseNameEn)
    {
        this.courseNameEn = courseNameEn;
    }
    
    public String getNature()
    {
        return nature;
    }
    
    public void setNature(String nature)
    {
        this.nature = nature;
    }
    
    public Double getPeriod()
    {
        return period;
    }
    
    public void setPeriod(Double period)
    {
        this.period = period;
    }
    
    public Double getCredits()
    {
        return credits;
    }
    
    public void setCredits(Double credits)
    {
        this.credits = credits;
    }
    
    public String getTerm()
    {
        return term;
    }
    
    public void setTerm(String term)
    {
        this.term = term;
    }
    
    public Integer getCrossTerm()
    {
        return crossTerm;
    }
    
    public void setCrossTerm(Integer crossTerm)
    {
        this.crossTerm = crossTerm;
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CourseOpen that = (CourseOpen)o;
        return Objects.equals(calendarId, that.calendarId)
            && Objects.equals(courseCode, that.courseCode);
    }
    
    @Override
    public int hashCode()
    {
        
        return Objects.hash(calendarId, courseCode);
    }
}