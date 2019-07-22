package com.server.edu.election.query;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;

import com.server.edu.election.validate.RoundGroup;

public class ElecRoundCourseQuery
{
    @NotNull(groups = {RoundGroup.class})
    private Long roundId;
    
    //校历id
    @NotNull(groups = {RoundGroup.class, Default.class})
    private Long calendarId;
    
    //选课模式
    @NotNull(groups = {RoundGroup.class})
    private Integer mode;
    
    private String projectId;
    
    private String courseCode;
    
    private String courseName;

    /** 课程类型 1体育课，2英语课*/
    private Integer courseType;

    private List<String> includeCourseCodes;
    
    /** 学院 */
    private String faculty;
    
    /**培养层次*/
    private String trainingLevel;
    
    /**课程性质*/
    private String nature;
    
    /**校区*/
    private String campus; 
    
    /**课程类别*/
    private String label;
    
    private String keyword;
    
    private String campus;

    public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Integer getCourseType() {
        return courseType;
    }

    public void setCourseType(Integer courseType) {
        this.courseType = courseType;
    }

    public List<String> getIncludeCourseCodes() {
        return includeCourseCodes;
    }

    public void setIncludeCourseCodes(List<String> includeCourseCodes) {
        this.includeCourseCodes = includeCourseCodes;
    }

    public Integer getMode()
    {
        return mode;
    }
    
    public void setMode(Integer mode)
    {
        this.mode = mode;
    }
    
    public String getProjectId()
    {
        return projectId;
    }
    
    public void setProjectId(String projectId)
    {
        this.projectId = projectId;
    }
    
    public Long getRoundId()
    {
        return roundId;
    }
    
    public void setRoundId(Long roundId)
    {
        this.roundId = roundId;
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
    
    public String getFaculty()
    {
        return faculty;
    }
    
    public void setFaculty(String faculty)
    {
        this.faculty = faculty;
    }
    
    public String getTrainingLevel()
    {
        return trainingLevel;
    }
    
    public void setTrainingLevel(String trainingLevel)
    {
        this.trainingLevel = trainingLevel;
    }
    
    public String getNature()
    {
        return nature;
    }
    
    public void setNature(String nature)
    {
        this.nature = nature;
    }
    
    public String getKeyword()
    {
        return keyword;
    }
    
    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }

	public String getCampus() {
		return campus;
	}

	public void setCampus(String campus) {
		this.campus = campus;
	}

    
}
