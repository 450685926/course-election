package com.server.edu.election.dto;

import java.util.List;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.election.entity.CourseOpen;

@CodeI18n
public class CourseOpenDto extends CourseOpen
{
    private static final long serialVersionUID = 1L;

    private Long roundId;
    
    private Long teachingClassId;
    
    /**
     *  教学班编号
     */
    private String teachingClassCode;

    /**
     *  校区
     */
    @Code2Text(transformer="X_XQ")
    private String campus;
    
    /**
     * 教学班名称
     */
    private String teachingClassName;

    private String teachClassType;

    private Integer maxNumber;

    private Integer currentNumber;
    
    private Integer suggestStatus;
    
    private String keyWord;
    
    private List<String> courses;
    
    private String projectId;
    
    
    private Long courseLabelId;
    
    private String teacherCode;
    
    

	public Long getCourseLabelId() {
		return courseLabelId;
	}

	public void setCourseLabelId(Long courseLabelId) {
		this.courseLabelId = courseLabelId;
	}

	public String getTeacherCode() {
		return teacherCode;
	}

	public void setTeacherCode(String teacherCode) {
		this.teacherCode = teacherCode;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public List<String> getCourses() {
		return courses;
	}

	public void setCourses(List<String> courses) {
		this.courses = courses;
	}

	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public Integer getSuggestStatus() {
		return suggestStatus;
	}

	public void setSuggestStatus(Integer suggestStatus) {
		this.suggestStatus = suggestStatus;
	}

	public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getTeachClassType() {
        return teachClassType;
    }

    public void setTeachClassType(String teachClassType) {
        this.teachClassType = teachClassType;
    }

    public Integer getMaxNumber() {
        return maxNumber;
    }

    public void setMaxNumber(Integer maxNumber) {
        this.maxNumber = maxNumber;
    }

    public Integer getCurrentNumber() {
        return currentNumber;
    }

    public void setCurrentNumber(Integer currentNumber) {
        this.currentNumber = currentNumber;
    }

    public Long getRoundId()
    {
        return roundId;
    }
    
    public void setRoundId(Long roundId)
    {
        this.roundId = roundId;
    }
    
    public Long getTeachingClassId()
    {
        return teachingClassId;
    }
    
    public void setTeachingClassId(Long teachingClassId)
    {
        this.teachingClassId = teachingClassId;
    }
    
    public String getTeachingClassCode()
    {
        return teachingClassCode;
    }
    
    public void setTeachingClassCode(String teachingClassCode)
    {
        this.teachingClassCode = teachingClassCode;
    }

	public String getTeachingClassName() {
		return teachingClassName;
	}

	public void setTeachingClassName(String teachingClassName) {
		this.teachingClassName = teachingClassName;
	}
    
}
