package com.server.edu.election.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.election.entity.ElcCourseTake;

@CodeI18n
public class ElcCourseTakeVo extends ElcCourseTake
{
    private static final long serialVersionUID = 1L;
    
    private String studentName;
    
    private String courseName;
    
	/**学分*/
    private Double credits;
    
    /**学时*/
    private Double period;
    
    /**校区*/
    @Code2Text(DictTypeEnum.X_XQ)
    private String campus;
    
    /**专业*/
    @Code2Text(DictTypeEnum.G_ZY)
    private String profession;

    private String grade;

    /**课程性质*/
    @Code2Text(DictTypeEnum.X_KCXZ)
    private String nature;

    private String courseArrange;

    /**教学班编号*/
    private String teachingClassCode;
    /**教学班名称*/
    private String teachingClassName;

    /**是否为公共选修课(1:是，0：否)*/
    private Integer isPublicCourse;
    /**选课申请ID*/
    private Long electionApplyId;
    /**选课申请状态*/    
    private Integer apply;
    /**任课教师*/  
    private String teachingCode;
    
    private String teachingName;

    private String calendarName;

    @Code2Text(DictTypeEnum.X_PYCC)
    private String trainingLevel;

    @Code2Text(DictTypeEnum.X_YX)
    private String faculty;

    @Code2Text(DictTypeEnum.X_YX)
    private String studentFaculty;

    @Code2Text(DictTypeEnum.X_KCFL)
    private String label;

    /**考试方式*/
    private String assessmentMode;

    private String term;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long medWithdrawId;
    
    private String elcMedWithdrawStatus;
    
    /** 课程类别*/
    private String courseLabel;
    
    /** 选课方式：1-自选；2-代选 */
    @Code2Text(DictTypeEnum.K_XKFS)
    private Integer electionMode;

    @Code2Text(transformer="K_BKKCXZ")
    private Integer isElective;
    
    private String compulsory;

    @Code2Text(transformer = "G_XBIE")
    private String sex;

    private Integer turn;

    public String getStudentFaculty() {
        return studentFaculty;
    }

    public void setStudentFaculty(String studentFaculty) {
        this.studentFaculty = studentFaculty;
    }

    public String getCompulsory() {
		return compulsory;
	}

	public void setCompulsory(String compulsory) {
		this.compulsory = compulsory;
	}

	public Integer getIsElective() {
        return isElective;
    }

    public void setIsElective(Integer isElective) {
        this.isElective = isElective;
    }

    public String getElcMedWithdrawStatus() {
		return elcMedWithdrawStatus;
	}

	public void setElcMedWithdrawStatus(String elcMedWithdrawStatus) {
		this.elcMedWithdrawStatus = elcMedWithdrawStatus;
	}

	public Long getMedWithdrawId() {
		return medWithdrawId;
	}

	public void setMedWithdrawId(Long medWithdrawId) {
		this.medWithdrawId = medWithdrawId;
	}

	public String getTrainingLevel() {
        return trainingLevel;
    }

    public void setTrainingLevel(String trainingLevel) {
        this.trainingLevel = trainingLevel;
    }

    public String getCalendarName() {
        return calendarName;
    }

    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getAssessmentMode() {
        return assessmentMode;
    }

    public void setAssessmentMode(String assessmentMode) {
        this.assessmentMode = assessmentMode;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public String getCourseArrange() {
        return courseArrange;
    }

    public void setCourseArrange(String courseArrange) {
        this.courseArrange = courseArrange;
    }

    public String getTeachingCode() {
		return teachingCode;
	}

	public void setTeachingCode(String teachingCode) {
		this.teachingCode = teachingCode;
	}

	public String getTeachingName() {
		return teachingName;
	}

	public void setTeachingName(String teachingName) {
		this.teachingName = teachingName;
	}

	public Integer getApply() {
		return apply;
	}

	public void setApply(Integer apply) {
		this.apply = apply;
	}

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

	public Integer getIsPublicCourse() {
        return isPublicCourse;
    }

    public void setIsPublicCourse(Integer isPublicCourse) {
        this.isPublicCourse = isPublicCourse;
    }

    public String getStudentName()
    {
        return studentName;
    }
    
    public void setStudentName(String studentName)
    {
        this.studentName = studentName;
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
    
    public String getCampus()
    {
        return campus;
    }
    
    public void setCampus(String campus)
    {
        this.campus = campus;
    }
    
    public String getProfession()
    {
        return profession;
    }
    
    public void setProfession(String profession)
    {
        this.profession = profession;
    }
    
    public String getTeachingClassCode()
    {
        return teachingClassCode;
    }
    
    public void setTeachingClassCode(String teachingClassCode)
    {
        this.teachingClassCode = teachingClassCode;
    }

	public Double getPeriod() {
		return period;
	}

	public void setPeriod(Double period) {
		this.period = period;
	}

	public String getTeachingClassName() {
		return teachingClassName;
	}

	public void setTeachingClassName(String teachingClassName) {
		this.teachingClassName = teachingClassName;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

    public String getCourseLabel()
    {
        return courseLabel;
    }

    public void setCourseLabel(String courseLabel)
    {
        this.courseLabel = courseLabel;
    }

	public Long getStudentCode() {
		return Long.parseLong(super.getStudentId());
	}

	public Integer getElectionMode() {
		return electionMode;
	}

	public void setElectionMode(Integer electionMode) {
		this.electionMode = electionMode;
	}

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @Override
    public Integer getTurn() {
        return turn;
    }

    @Override
    public void setTurn(Integer turn) {
        this.turn = turn;
    }
}
