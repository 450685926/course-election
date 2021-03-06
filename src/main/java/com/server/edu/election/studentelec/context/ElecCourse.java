package com.server.edu.election.studentelec.context;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;

import org.apache.commons.lang3.StringUtils;

import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;

@CodeI18n
public class ElecCourse implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**课程代码*/
    private String courseCode;
    
    /**课程名称*/
    private String courseName;
    
    /**学分*/
    private Double credits;
    
    /**课程名称（英文）*/
    private String nameEn;
    
    /**课程性质*/
    @Code2Text(DictTypeEnum.X_KCXZ)
    private String nature;
    
    /** 是否公选课 */
    private boolean publicElec;
    
    /**
     * 学期
     */
    private Long calendarId;
    
    /**学期周学时对应学期*/
    private String calendarName;
    
    /**选课申请ID*/
    private Long electionApplyId;
    
    /**选课申请状态*/
    private Integer apply;
    
    /**是否必修*/
    private String compulsory;
    
    /**
     * 备注
     */
    private String remark;
    /**
     * 培养课程类别
     */
    private Long labelId;
    /**
     * 培养课程类别名称
     */
    private String labelName;

    // 精品
    private String jp;

    // 创新
    private boolean isCx;

    // 艺术
    private boolean isYs;

    private String campus;
    
    private Integer chosen;
    
    private Integer isQhClass;

    private Integer isPE;
    
    /**
     * 授课语言X_SKYY
     */
    @Code2Text(DictTypeEnum.X_SKYY)
    private String teachingLanguage;

    @Code2Text(transformer = "X_YX")
    private String faculty;

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getTeachingLanguage() {
		return teachingLanguage;
	}

	public void setTeachingLanguage(String teachingLanguage) {
		this.teachingLanguage = teachingLanguage;
	}
    private String courseLabel;

	public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
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

	public String getJp() {
        return jp;
    }

    public void setJp(String jp) {
        this.jp = jp;
    }

    public boolean isCx() {
        return isCx;
    }

    public void setCx(boolean cx) {
        isCx = cx;
    }

    public boolean isYs() {
        return isYs;
    }

    public void setYs(boolean ys) {
        isYs = ys;
    }

    public Long getLabelId() {
		return labelId;
	}

	public void setLabelId(Long labelId) {
		this.labelId = labelId;
	}

	public String getLabelName() {
		return labelName;
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
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
    
    public String getNature()
    {
        return nature;
    }
    
    public void setNature(String nature)
    {
        this.nature = nature;
    }
    
    public boolean isPublicElec()
    {
        return publicElec;
    }
    
    public void setPublicElec(boolean publicElec)
    {
        this.publicElec = publicElec;
    }
    
    public Long getCalendarId()
    {
        return calendarId;
    }
    
    public void setCalendarId(Long calendarId)
    {
        this.calendarId = calendarId;
    }
    
    public String getCalendarName()
    {
        return calendarName;
    }
    
    public void setCalendarName(String calendarName)
    {
        this.calendarName = calendarName;
    }
    
    public Long getElectionApplyId()
    {
        return electionApplyId;
    }
    
    public void setElectionApplyId(Long electionApplyId)
    {
        this.electionApplyId = electionApplyId;
    }
    
    public Integer getApply()
    {
        return apply;
    }
    
    public void setApply(Integer apply)
    {
        this.apply = apply;
    }
    
    public String getCompulsory()
    {
        return compulsory;
    }
    
    public void setCompulsory(String compulsory)
    {
        this.compulsory = compulsory;
    }
    
    public String getRemark()
    {
        return remark;
    }
    
    public void setRemark(String remark)
    {
        this.remark = remark;
    }
    
    public String getCourseLabel() {
		return courseLabel;
	}

	public void setCourseLabel(String courseLabel) {
		this.courseLabel = courseLabel;
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

    public Integer getIsPE() {
        return isPE;
    }

    public void setIsPE(Integer isPE) {
        this.isPE = isPE;
    }
}
