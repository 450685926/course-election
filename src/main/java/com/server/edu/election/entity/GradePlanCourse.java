package com.server.edu.election.entity;

import com.server.edu.election.studentelec.context.ElecCourse;

public class GradePlanCourse{
    private ElecCourse course;
    
    /**课程分类*/
    private Long label;
    
    private String labelName;
    
    /**开课学院*/
    private String faculty;
    
    /**实践周*/
    private Integer weekType;
    
    /**学期周学时*/
    private String semester;
    
    /**个人替代课程*/
    private String subCourseCode;
    
    private Integer chosen;
    
    private Integer isQhClass;

    private Integer isPE;
    
    private Integer isHaveScore;
    
    
    public Integer getIsHaveScore() {
		return isHaveScore;
	}

	public void setIsHaveScore(Integer isHaveScore) {
		this.isHaveScore = isHaveScore;
	}

	public Integer getChosen() {
		return chosen;
	}

	public void setChosen(Integer chosen) {
		this.chosen = chosen;
	}

	public Integer getIsQhClass() {
		return isQhClass;
	}

	public void setIsQhClass(Integer isQhClass) {
		this.isQhClass = isQhClass;
	}

	public String getLabelName() {
		return labelName;
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}

	public String getCourseCode()
    {
        return getCourse().getCourseCode();
    }
    
    public void setCourseCode(String courseCode)
    {
        this.getCourse().setCourseCode(courseCode);
    }
    
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

    public Integer getIsPE() {
        return isPE;
    }

    public void setIsPE(Integer isPE) {
        this.isPE = isPE;
    }	

}
