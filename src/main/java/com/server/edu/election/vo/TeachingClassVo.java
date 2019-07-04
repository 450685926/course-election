package com.server.edu.election.vo;

import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.election.entity.TeachingClass;

@CodeI18n
public class TeachingClassVo extends TeachingClass
{
    private String courseCode;
    private String courseName;
    private Double credits;
    
    private String teacherName;
    
    private int withdrawNum;
    /**
     * 课程性质
     */
    @Code2Text(DictTypeEnum.X_KCXZ)
    private String nature;
    
    /**
     * 开课学院
     */
    @Code2Text(DictTypeEnum.X_YX)
    private String faculty;
    
    /**
     * 校区
     */
    @Code2Text(DictTypeEnum.X_XQ)
    private String campus; 
    
	public int getWithdrawNum() {
		return withdrawNum;
	}
	public void setWithdrawNum(int withdrawNum) {
		this.withdrawNum = withdrawNum;
	}
	public String getTeacherName() {
		return teacherName;
	}
	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
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
	public String getNature() {
		return nature;
	}
	public void setNature(String nature) {
		this.nature = nature;
	}
	public String getFaculty() {
		return faculty;
	}
	public void setFaculty(String faculty) {
		this.faculty = faculty;
	}
	public String getCampus() {
		return campus;
	}
	public void setCampus(String campus) {
		this.campus = campus;
	}
    
}
