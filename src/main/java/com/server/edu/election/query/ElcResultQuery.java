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
    
    /**
     * 校区（取字典X_XQ）
     */
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
     * 教学班编号（课程序号）
     */
    private String teachingClassCode;
    
    /**
     * 教学班id
     */
    private Long teachingClassId;
    
    /**
     * 培养类别
     */
    private String trainingCategory;
    
    /**
     * 学习形式
     */
    private String formLearning;
    
    /**
     * 年级
     */
    private String grade;
    
    /**
     * 入学季节
     */
    private String enrolSeason;
    
    /**
     * 学位类型
     */
    private String degreeType;
    
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
    
    private String managerDeptId;
    
    /**
     * 开课学院
     */
    private String faculty;
    
    /**
     * 专业
     */
    private String profession;
    
    /**
     * 课程类别ID
     */
    @Code2Text(DictTypeEnum.X_KCFL)
    private Long courseLabelId;
    
    /**
     * 课程性质
     */
    private String nature;
    
    /**
     * 培养层次
     */
    private String trainingLevel;
    
    /**
     * 教室编号
     */
    private String teacherCode;
    
    private Integer actualAndUpper;
    
    private Integer minActualNumber;
    
    private Integer maxActualNumber;
    
    private Integer minUpperNumber;
    
    private Integer maxUpperNumber;
    
    private Integer dimension;
    
    private List<String> studentIds;
    
    
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
    
    public String getTrainingLevel() {
		return trainingLevel;
	}

	public void setTrainingLevel(String trainingLevel) {
		this.trainingLevel = trainingLevel;
	}

	public String getTrainingCategory() {
		return trainingCategory;
	}

	public void setTrainingCategory(String trainingCategory) {
		this.trainingCategory = trainingCategory;
	}

	public String getFormLearning() {
		return formLearning;
	}

	public void setFormLearning(String formLearning) {
		this.formLearning = formLearning;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getEnrolSeason() {
		return enrolSeason;
	}

	public void setEnrolSeason(String enrolSeason) {
		this.enrolSeason = enrolSeason;
	}

	public String getDegreeType() {
		return degreeType;
	}

	public void setDegreeType(String degreeType) {
		this.degreeType = degreeType;
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

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public String getNature() {
		return nature;
	}

	public void setNature(String nature) {
		this.nature = nature;
	}

	public String getManagerDeptId() {
		return managerDeptId;
	}

	public void setManagerDeptId(String managerDeptId) {
		this.managerDeptId = managerDeptId;
	}

	public Integer getDimension() {
		return dimension;
	}

	public void setDimension(Integer dimension) {
		this.dimension = dimension;
	}

	public List<String> getStudentIds() {
		return studentIds;
	}

	public void setStudentIds(List<String> studentIds) {
		this.studentIds = studentIds;
	}
	
	
}
