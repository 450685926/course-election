package com.server.edu.election.studentelec.cache;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.election.studentelec.context.ClassTimeUnit;
import com.server.edu.election.studentelec.context.ElecCourse;
import com.server.edu.election.studentelec.context.TimeAndRoom;

/**
 * 教学班缓存对象
 * 
 */
@CodeI18n
public class TeachingClassCache extends ElecCourse
{
    private Long teachClassId;
    
    private String teachClassCode;
    
    private String teachClassName;
    
    private String teachClassType;
    
    /** 是否实践课*/
    private Boolean practice = Boolean.FALSE;
    
    /** 是否重修班*/
    private Boolean retraining = Boolean.FALSE;
    
    /** 最大人数（人数上限） */
    private Integer maxNumber;
    
    /** 当前人数 */
    private Integer currentNumber;
    
    /** 上课时间按教学周拆分集合 */
    private List<ClassTimeUnit> times;
    
    /** 上课时间地点 */
    private List<TimeAndRoom> timeTableList;
    
    private String teacherCode;
    
    private String teacherName;
    
    /**校区*/
    @Code2Text(DictTypeEnum.X_XQ)
    private String campus;
    
    @Code2Text(DictTypeEnum.X_YX)
    private String faculty;
    
    @Code2Text(transformer = "X_KKXQ")
    private String term;
    
    private  Integer manArrangeFlag;
    
    public String getTerm()
    {
        return term;
    }
    
    public void setTerm(String term)
    {
        this.term = term;
    }
    
    public TeachingClassCache()
    {
    }
    
    public TeachingClassCache(ElecCourse course)
    {
        this.setCourseCode(course.getCourseCode());
        this.setCourseName(course.getCourseName());
        this.setCredits(course.getCredits());
        this.setNameEn(course.getNameEn());
        this.setNature(course.getNature());
    }
    
    public String getTeacherCode()
    {
        return teacherCode;
    }
    
    public void setTeacherCode(String teacherCode)
    {
        this.teacherCode = teacherCode;
    }
    
    public String getTeacherName()
    {
        return teacherName;
    }
    
    public void setTeacherName(String teacherName)
    {
        this.teacherName = teacherName;
    }
    
    public Long getTeachClassId()
    {
        return teachClassId;
    }
    
    public void setTeachClassId(Long teachClassId)
    {
        this.teachClassId = teachClassId;
    }
    
    public String getTeachClassCode()
    {
        return teachClassCode;
    }
    
    public void setTeachClassCode(String teachClassCode)
    {
        this.teachClassCode = teachClassCode;
    }
    
    public String getTeachClassType()
    {
        return teachClassType;
    }
    
    public void setTeachClassType(String teachClassType)
    {
        this.teachClassType = teachClassType;
    }
    
    public Boolean getPractice()
    {
        return practice;
    }
    
    public void setPractice(Boolean practice)
    {
        this.practice = practice;
    }
    
    public Boolean getRetraining()
    {
        return retraining;
    }
    
    public void setRetraining(Boolean retraining)
    {
        this.retraining = retraining;
    }
    
    public Integer getMaxNumber()
    {
        return maxNumber;
    }
    
    public void setMaxNumber(Integer maxNumber)
    {
        this.maxNumber = maxNumber;
    }
    
    public Integer getCurrentNumber()
    {
        return currentNumber;
    }
    
    public void setCurrentNumber(Integer currentNumber)
    {
        this.currentNumber = currentNumber;
    }
    
    public List<ClassTimeUnit> getTimes()
    {
        return times;
    }
    
    public void setTimes(List<ClassTimeUnit> times)
    {
        this.times = times;
    }
    
    public String getCampus()
    {
        return campus;
    }
    
    public void setCampus(String campus)
    {
        this.campus = campus;
    }
    
    public String getFaculty()
    {
        return faculty;
    }
    
    public void setFaculty(String faculty)
    {
        this.faculty = faculty;
    }
    
    public String getTeachClassName() {
		return teachClassName;
	}

	public void setTeachClassName(String teachClassName) {
		this.teachClassName = teachClassName;
	}

	@JsonIgnore
    public String getCourseCodeAndClassCode()
    {
        return String
            .format("%s[%s]", this.getCourseCode(), this.getTeachClassCode());
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash(this.teachClassId, this.teachClassCode);
    }
    
    public List<TimeAndRoom> getTimeTableList()
    {
        return timeTableList;
    }
    
    public void setTimeTableList(List<TimeAndRoom> timeTableList)
    {
        this.timeTableList = timeTableList;
    }
    
    public Integer getManArrangeFlag() {
		return manArrangeFlag;
	}

	public void setManArrangeFlag(Integer manArrangeFlag) {
		this.manArrangeFlag = manArrangeFlag;
	}

	@Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        
        if (obj instanceof TeachingClassCache)
        {
            TeachingClassCache o = (TeachingClassCache)obj;
            return StringUtils.equals(this.teachClassCode, o.teachClassCode)
                && Objects.equals(this.teachClassId, o.teachClassId);
        }
        return false;
    }
    
    @Override
    public String toString()
    {
        return "TeachingClassCache [teachClassId=" + teachClassId
            + ", teachClassCode=" + teachClassCode + ", teachClassType="
            + teachClassType + ", practice=" + practice + ", retraining="
            + retraining + ", maxNumber=" + maxNumber + ", currentNumber="
            + currentNumber + ", times=" + times + ", teacherCode="
            + teacherCode + ", teacherName=" + teacherName + ", faculty="
            + faculty + "]";
    }
    
}
