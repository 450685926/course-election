package com.server.edu.election.dto;

import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.election.entity.ElcMedWithdrawApply;

public class ElcMedWithdrawApplyDto extends ElcMedWithdrawApply {
    private static final long serialVersionUID = 1L;
    //校历id
    private Long calendarId;
    
    //入学季节
    private String enrolSeason;
    
    private String courseCode;
    
    @Code2Text(transformer = "X_KCXZ")
    private String nature;
    
    @Code2Text(transformer="X_YX")
    private String faculty;
    
    /**
     * 校区（取字典X_XQ）
     */
    @Code2Text(DictTypeEnum.X_XQ)
    private String campus;
    
    private String parameter;
    
    //是否开放
    private int isOpen;
    
    public static final int ISOPEN = 1; 
    
    public static final int UNOPEN = 0;
    @NotEmpty
    private List<Long> teachingClassIds;
    
    private String keyWord;
    

	public String getCourseCode() {
		return courseCode;
	}

	public void setCourseCode(String courseCode) {
		this.courseCode = courseCode;
	}


	public String getEnrolSeason() {
		return enrolSeason;
	}

	public void setEnrolSeason(String enrolSeason) {
		this.enrolSeason = enrolSeason;
	}

	public List<Long> getTeachingClassIds() {
		return teachingClassIds;
	}

	public void setTeachingClassIds(List<Long> teachingClassIds) {
		this.teachingClassIds = teachingClassIds;
	}

	public int getIsOpen() {
		return isOpen;
	}

	public void setIsOpen(int isOpen) {
		this.isOpen = isOpen;
	}

	public Long getCalendarId() {
		return calendarId;
	}

	public void setCalendarId(Long calendarId) {
		this.calendarId = calendarId;
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

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}
	
	
}
