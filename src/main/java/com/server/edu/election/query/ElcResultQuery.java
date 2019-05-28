package com.server.edu.election.query;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;

public class ElcResultQuery
{
    /**
     * 校历ID（学年学期）
     */
    @NotNull
    private Long calendarId;
    
    private String projectId;
    
    private String campus;
    
    /**
     * 课程编号
     */
    private String courseCode;
    
    /**
     * 课程名称
     */
    private String courseName;
    
    /**
     * 教学班编号
     */
    private String teachingClassCode;
    
    /**
     * 教学班id
     */
    private Long teachingClassId;
    
    /**
     * 教学班ids
     */
    private List<Long> teachingClassIds;
    
    /**
     * 是否排课
     */
    private Integer manArrangeFlag;
    
    /**
     * 学分
     */
    private Double credits;
    
    private String label;
    
    private String keyword;
    
    private String faculty;
    
    /**
     * 课程类别ID
     */
    @Code2Text(DictTypeEnum.X_KCFL)
    private Long courseLabelId;
    
    private String teacherCode;
    
    private Integer actualAndUpper;
    
    private Integer minActualNumber;
    
    private Integer maxActualNumber;
    
    private Integer minUpperNumber;
    
    private Integer maxUpperNumber;
    
    
    public Integer getActualAndUpper() {
		return actualAndUpper;
	}

	public void setActualAndUpper(Integer actualAndUpper) {
		this.actualAndUpper = actualAndUpper;
	}

	public Integer getMinActualNumber() {
		return minActualNumber;
	}

	public void setMinActualNumber(Integer minActualNumber) {
		this.minActualNumber = minActualNumber;
	}

	public Integer getMaxActualNumber() {
		return maxActualNumber;
	}

	public void setMaxActualNumber(Integer maxActualNumber) {
		this.maxActualNumber = maxActualNumber;
	}

	public Integer getMinUpperNumber() {
		return minUpperNumber;
	}

	public void setMinUpperNumber(Integer minUpperNumber) {
		this.minUpperNumber = minUpperNumber;
	}

	public Integer getMaxUpperNumber() {
		return maxUpperNumber;
	}

	public void setMaxUpperNumber(Integer maxUpperNumber) {
		this.maxUpperNumber = maxUpperNumber;
	}

	public String getTeacherCode() {
		return teacherCode;
	}

	public void setTeacherCode(String teacherCode) {
		this.teacherCode = teacherCode;
	}

	public Long getCourseLabelId() {
		return courseLabelId;
	}

	public void setCourseLabelId(Long courseLabelId) {
		this.courseLabelId = courseLabelId;
	}

	public Long getCalendarId()
    {
        return calendarId;
    }
    
    public void setCalendarId(Long calendarId)
    {
        this.calendarId = calendarId;
    }
    
    public String getProjectId()
    {
        return projectId;
    }
    
    public void setProjectId(String projectId)
    {
        this.projectId = projectId;
    }
    
    public String getCampus()
    {
        return campus;
    }
    
    public void setCampus(String campus)
    {
        this.campus = campus;
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
    
    public String getTeachingClassCode()
    {
        return teachingClassCode;
    }
    
    public void setTeachingClassCode(String teachingClassCode)
    {
        this.teachingClassCode = teachingClassCode;
    }
    
    public Integer getManArrangeFlag()
    {
        return manArrangeFlag;
    }
    
    public void setManArrangeFlag(Integer manArrangeFlag)
    {
        this.manArrangeFlag = manArrangeFlag;
    }
    
    public Long getTeachingClassId()
    {
        return teachingClassId;
    }
    
    public void setTeachingClassId(Long teachingClassId)
    {
        this.teachingClassId = teachingClassId;
    }
    
    public List<Long> getTeachingClassIds()
    {
        return teachingClassIds;
    }
    
    public void setTeachingClassIds(List<Long> teachingClassIds)
    {
        this.teachingClassIds = teachingClassIds;
    }
    
    public String getLabel()
    {
        return label;
    }
    
    public void setLabel(String label)
    {
        this.label = label;
    }
    
    public String getKeyword()
    {
        return keyword;
    }
    
    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }

	public Double getCredits() {
		return credits;
	}

	public void setCredits(Double credits) {
		this.credits = credits;
	}

	public String getFaculty() {
		return faculty;
	}

	public void setFaculty(String faculty) {
		this.faculty = faculty;
	}
	
    
    
    
}
