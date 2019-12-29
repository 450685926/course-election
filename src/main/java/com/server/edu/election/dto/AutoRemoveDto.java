package com.server.edu.election.dto;

import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotNull;

import com.server.edu.election.entity.ElcCourseTake;
import com.server.edu.election.entity.TeachingClass;
import com.server.edu.election.entity.TeachingClassElectiveRestrictAttr;
import com.server.edu.election.vo.ElcAffinityCoursesStdsVo;
import com.server.edu.election.vo.RestrictStudent;

/**
 * 自动剔除超过人数
 * 
 * 
 * @author  OuYangGuoDong
 * @version  [版本号, 2019年2月21日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class AutoRemoveDto
{
    @NotNull
    private Long calendarId;
    
    @NotNull
    private Long teachingClassId;
    
    /**是否不删除特殊学生*/
    private Boolean invincibleStu;
    
    /**删除选先学生*/
    private Boolean affinityStu;
    
    /**剔除非指定年级、专业的学生*/
    private Boolean gradAndPre;
    
    /**按教学班选课限制筛选*/
    private Boolean classElcCondition;
    
    /**按教学班对应专业配比人数进行筛选*/
    private Boolean gradAndPrePeople;
    
    /**按建议课表进行筛选*/
    private Boolean suggestSwitchCourse;
    
    private String label;
    
    private List<ElcCourseTake> takes;
    
    private TeachingClass teachingClass;
    //特殊
    private List<String> invincibleStdIds;
    //优先
    private Set<String> affinityCoursesStdSet;
    
    private List<SuggestProfessionDto> suggestProfessionList;
    
    private List<SuggestProfessionDto> restrictProfessionList;
    
    private List<String> restrictStus;
    
    private TeachingClassElectiveRestrictAttr classAttrList;
    
	public TeachingClassElectiveRestrictAttr getClassAttrList() {
		return classAttrList;
	}

	public void setClassAttrList(TeachingClassElectiveRestrictAttr classAttrList) {
		this.classAttrList = classAttrList;
	}

	public List<SuggestProfessionDto> getSuggestProfessionList() {
		return suggestProfessionList;
	}

	public void setSuggestProfessionList(List<SuggestProfessionDto> suggestProfessionList) {
		this.suggestProfessionList = suggestProfessionList;
	}

	public List<SuggestProfessionDto> getRestrictProfessionList() {
		return restrictProfessionList;
	}

	public void setRestrictProfessionList(List<SuggestProfessionDto> restrictProfessionList) {
		this.restrictProfessionList = restrictProfessionList;
	}
	
	public List<String> getRestrictStus() {
		return restrictStus;
	}

	public void setRestrictStus(List<String> restrictStus) {
		this.restrictStus = restrictStus;
	}

	public List<String> getInvincibleStdIds() {
		return invincibleStdIds;
	}

	public void setInvincibleStdIds(List<String> invincibleStdIds) {
		this.invincibleStdIds = invincibleStdIds;
	}
	
	public Set<String> getAffinityCoursesStdSet() {
		return affinityCoursesStdSet;
	}

	public void setAffinityCoursesStdSet(Set<String> affinityCoursesStdSet) {
		this.affinityCoursesStdSet = affinityCoursesStdSet;
	}

	public TeachingClass getTeachingClass() {
		return teachingClass;
	}

	public void setTeachingClass(TeachingClass teachingClass) {
		this.teachingClass = teachingClass;
	}

	public List<ElcCourseTake> getTakes() {
		return takes;
	}

	public void setTakes(List<ElcCourseTake> takes) {
		this.takes = takes;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Long getCalendarId()
    {
        return calendarId;
    }
    
    public void setCalendarId(Long calendarId)
    {
        this.calendarId = calendarId;
    }
    
    public Long getTeachingClassId()
    {
        return teachingClassId;
    }
    
    public void setTeachingClassId(Long teachingClassId)
    {
        this.teachingClassId = teachingClassId;
    }
    
    public Boolean getInvincibleStu()
    {
        return invincibleStu;
    }
    
    public void setInvincibleStu(Boolean invincibleStu)
    {
        this.invincibleStu = invincibleStu;
    }
    
    public Boolean getAffinityStu()
    {
        return affinityStu;
    }
    
    public void setAffinityStu(Boolean affinityStu)
    {
        this.affinityStu = affinityStu;
    }
    
    public Boolean getGradAndPre()
    {
        return gradAndPre;
    }
    
    public void setGradAndPre(Boolean gradAndPre)
    {
        this.gradAndPre = gradAndPre;
    }
    
    public Boolean getClassElcCondition()
    {
        return classElcCondition;
    }
    
    public void setClassElcCondition(Boolean classElcCondition)
    {
        this.classElcCondition = classElcCondition;
    }
    
    public Boolean getGradAndPrePeople()
    {
        return gradAndPrePeople;
    }
    
    public void setGradAndPrePeople(Boolean gradAndPrePeople)
    {
        this.gradAndPrePeople = gradAndPrePeople;
    }

	public Boolean getSuggestSwitchCourse() {
		return suggestSwitchCourse;
	}

	public void setSuggestSwitchCourse(Boolean suggestSwitchCourse) {
		this.suggestSwitchCourse = suggestSwitchCourse;
	}
    
    
    
}
