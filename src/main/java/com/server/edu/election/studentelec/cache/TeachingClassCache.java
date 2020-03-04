package com.server.edu.election.studentelec.cache;

import java.io.Serializable;
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
public class TeachingClassCache extends ElecCourse implements Serializable
{
    private static final long serialVersionUID = 1L;

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
    
    private String label;
    
    /**校区*/
    @Code2Text(DictTypeEnum.X_XQ)
    private String campus;
    
    @Code2Text(DictTypeEnum.X_YX)
    private String faculty;
    
    @Code2Text(transformer = "X_KKXQ")
    private String term;
    
    private Integer manArrangeFlag;

    private String replaceCourse;

    private String replaceCourseName;
    
    private Double period;
    
    private String assessmentMode;
    
    /** 教师名称(教师工号) */
    private String teacherNameAndCode;
    
    private Integer isPublicCourse;
    
    private Long id;
    
    private String studentId;

    /**替代课程原课程是否有教学班 1 有 0 否*/
    private String isOldCourse;

    /**替代课程学分*/
    private Double replaceCredits;

    public Double getReplaceCredits() {
        return replaceCredits;
    }

    public void setReplaceCredits(Double replaceCredits) {
        this.replaceCredits = replaceCredits;
    }

    public String getIsOldCourse() {
        return isOldCourse;
    }

    public void setIsOldCourse(String isOldCourse) {
        this.isOldCourse = isOldCourse;
    }

    public String getReplaceCourse() {
		return replaceCourse;
	}

	public void setReplaceCourse(String replaceCourse) {
		this.replaceCourse = replaceCourse;
	}

    //教学班预留人数
    private Integer reserveNumber;
    
    /**
     * 选课第三、四轮退课人数
     */
    private Integer thirdWithdrawNumber;

    private int firstTurnNum;

    private int secondTurnNum;

    public String getReplaceCourseName() {
        return replaceCourseName;
    }

    public void setReplaceCourseName(String replaceCourseName) {
        this.replaceCourseName = replaceCourseName;
    }

    public int getFirstTurnNum() {
        return firstTurnNum;
    }

    public void setFirstTurnNum(int firstTurnNum) {
        this.firstTurnNum = firstTurnNum;
    }

    public int getSecondTurnNum() {
        return secondTurnNum;
    }

    public void setSecondTurnNum(int secondTurnNum) {
        this.secondTurnNum = secondTurnNum;
    }

    public Integer getThirdWithdrawNumber() {
		return thirdWithdrawNumber;
	}

	public void setThirdWithdrawNumber(Integer thirdWithdrawNumber) {
		this.thirdWithdrawNumber = thirdWithdrawNumber;
	}

	public Integer getReserveNumber() {
        return reserveNumber;
    }

    public void setReserveNumber(Integer reserveNumber) {
        this.reserveNumber = reserveNumber;
    }

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
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
	
	public String getTeacherNameAndCode() {
		return teacherNameAndCode;
	}

	public void setTeacherNameAndCode(String teacherNameAndCode) {
		this.teacherNameAndCode = teacherNameAndCode;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Double getPeriod() {
		return period;
	}

	public void setPeriod(Double period) {
		this.period = period;
	}

	public String getAssessmentMode() {
		return assessmentMode;
	}

	public void setAssessmentMode(String assessmentMode) {
		this.assessmentMode = assessmentMode;
	}

	public Integer getIsPublicCourse() {
		return isPublicCourse;
	}

	public void setIsPublicCourse(Integer isPublicCourse) {
		this.isPublicCourse = isPublicCourse;
	}

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
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
