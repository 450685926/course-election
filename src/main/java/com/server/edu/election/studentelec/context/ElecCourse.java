package com.server.edu.election.studentelec.context;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

public class ElecCourse
{
	/**课程代码*/
    private String courseCode;
    
    /**课程名称*/
    private String courseName;
    
    /**学分*/
    private Double credits;
    
    /**课程名称（英文）*/
    private String nameEn;
    
    /**课程性质*/
    private String nature;
    
    /**校区*/
    private String campus;
    
    private String faculty;
    
    /** 是否公选课 */
    private boolean publicElec;

    /**学期周学时对应学期*/
    private String calendarName;
    
    /**选课申请ID*/
    private Long electionApplyId;
    /**选课申请状态*/    
    private Integer apply;
    
    public String getFaculty() {
		return faculty;
	}

	public void setFaculty(String faculty) {
		this.faculty = faculty;
	}

	public Long getElectionApplyId() {
		return electionApplyId;
	}

	public void setElectionApplyId(Long electionApplyId) {
		this.electionApplyId = electionApplyId;
	}

	public Integer getApply() {
		return apply;
	}

	public void setApply(Integer apply) {
		this.apply = apply;
	}

    public String getCalendarName() {
        return calendarName;
    }

    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
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
    
    public String getNature() {
		return nature;
	}

	public void setNature(String nature) {
		this.nature = nature;
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
