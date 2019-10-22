package com.server.edu.election.dto;

import com.server.edu.dictionary.annotation.Code2Text;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description:
 * @author: bear
 * @create: 2019-05-23 17:23
 */
public class RebuildCourseDto
{
    @NotNull
    private Long calendarId;
    
    private String courseCode;
    
    private String courseName;
    
    private String label;
    
    private Integer isCharge;
    
    private Integer grade;
    
    @Code2Text(transformer = "X_YX")
    private String faculty;
    
    @Code2Text(transformer = "G_ZY")
    private String major;
    
    private String studentCategory;
    
    private String keyWord;
    
    private Integer mode;
    
    private String deptId;
    /**学号*/
    private String studentId;
    /**姓名*/
    private String studentName;
    /**性别*/
    private String sex;

    /**培养计划对应课程Id集合*/
    private List<Long> planCourseIds;

    public List<Long> getPlanCourseIds() {
        return planCourseIds;
    }

    public void setPlanCourseIds(List<Long> planCourseIds) {
        this.planCourseIds = planCourseIds;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getDeptId()
    {
        return deptId;
    }
    
    public void setDeptId(String deptId)
    {
        this.deptId = deptId;
    }
    
    public Long getCalendarId()
    {
        return calendarId;
    }
    
    public void setCalendarId(Long calendarId)
    {
        this.calendarId = calendarId;
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
    
    public String getLabel()
    {
        return label;
    }
    
    public void setLabel(String label)
    {
        this.label = label;
    }
    
    public Integer getIsCharge()
    {
        return isCharge;
    }
    
    public void setIsCharge(Integer isCharge)
    {
        this.isCharge = isCharge;
    }
    
    public Integer getGrade()
    {
        return grade;
    }
    
    public void setGrade(Integer grade)
    {
        this.grade = grade;
    }
    
    public String getFaculty()
    {
        return faculty;
    }
    
    public void setFaculty(String faculty)
    {
        this.faculty = faculty;
    }
    
    public String getMajor()
    {
        return major;
    }
    
    public void setMajor(String major)
    {
        this.major = major;
    }
    
    public String getStudentCategory()
    {
        return studentCategory;
    }
    
    public void setStudentCategory(String studentCategory)
    {
        this.studentCategory = studentCategory;
    }
    
    public String getKeyWord()
    {
        return keyWord;
    }
    
    public void setKeyWord(String keyWord)
    {
        this.keyWord = keyWord;
    }
    
    public Integer getMode()
    {
        return mode;
    }
    
    public void setMode(Integer mode)
    {
        this.mode = mode;
    }
}
