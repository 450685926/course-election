package com.server.edu.election.query;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;

public class ElcCourseTakeQuery
{
    /**体育课程类型 1*/
    public static final Integer PE_COURSE_TYPE = 1;
    /**英语课程类型*/
    public static final Integer EN_COURSE_TYPE = 2;
    
    /**
     * 校历ID（学年学期）
     */
    @NotNull
    private Long calendarId;
    
    private String studentId;
    
    /**学生姓名*/
    private String stuName;
    
    /**学生学院*/
    private String faculty;
    
    private Integer sex;

    /**年级*/
    private String grade;

    private String trainingCategory;

    private String trainingLevel;

    /**方向*/
    private String researchDirection;

    /**开课学院*/
    private String courseFaculty;

    /**学生专业*/
    private String profession;
    
    /**
     * 课程编号
     */
    private String courseCode;
    
    private Long teachingClassId;
    
    /**
     * 教学班编号
     */
    private String teachingClassCode;
    
    /**
     * 修读类别(1正常修读,2重修,3免修不免考,4免修)
     */
    private Integer courseTakeType;
    
    /**
     * 选课模式
     * */
    private Integer mode;
    
    private String keyword;
    
    /** 课程类型 1体育课，2英语课*/
    private Integer courseType;
    
    private String projectId;
    
    private List<String> includeCourseCodes;
    
    private String includeCourseCode;
    
    private String label;
    
    private String courseName;
    
    private String degreeType;

    private String formLearning;

    private Integer isElective;

    private String nature;

    /**导出功能选中的id集合*/
    private List<Long> ids;
    
    /**是否留学生*/
    private String overseas;
    
    /** 课程类别ID*/
    private Long courseLabelId;

    public Integer getIsElective() {
        return isElective;
    }

    public void setIsElective(Integer isElective) {
        this.isElective = isElective;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public String getFormLearning() {
        return formLearning;
    }

    public void setFormLearning(String formLearning) {
        this.formLearning = formLearning;
    }

    public String getDegreeType() {
        return degreeType;
    }

    public void setDegreeType(String degreeType) {
        this.degreeType = degreeType;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getTrainingCategory() {
        return trainingCategory;
    }

    public void setTrainingCategory(String trainingCategory) {
        this.trainingCategory = trainingCategory;
    }

    public String getTrainingLevel() {
        return trainingLevel;
    }

    public void setTrainingLevel(String trainingLevel) {
        this.trainingLevel = trainingLevel;
    }

    public String getResearchDirection() {
        return researchDirection;
    }

    public void setResearchDirection(String researchDirection) {
        this.researchDirection = researchDirection;
    }

    public String getCourseFaculty() {
        return courseFaculty;
    }

    public void setCourseFaculty(String courseFaculty) {
        this.courseFaculty = courseFaculty;
    }

    public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public Integer getMode()
    {
        return mode;
    }
    
    public void setMode(Integer mode)
    {
        this.mode = mode;
    }
    
    public String getStudentId()
    {
        return studentId;
    }
    
    public void setStudentId(String studentId)
    {
        this.studentId = studentId;
    }
    
    public Long getCalendarId()
    {
        return calendarId;
    }
    
    public void setCalendarId(Long calendarId)
    {
        this.calendarId = calendarId;
    }
    
    public Integer getCourseTakeType()
    {
        return courseTakeType;
    }
    
    public void setCourseTakeType(Integer courseTakeType)
    {
        this.courseTakeType = courseTakeType;
    }
    
    public String getStuName()
    {
        return stuName;
    }
    
    public void setStuName(String stuName)
    {
        this.stuName = stuName;
    }
    
    public String getCourseCode()
    {
        return courseCode;
    }
    
    public void setCourseCode(String courseCode)
    {
        this.courseCode = courseCode;
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
    
    public String getFaculty()
    {
        return faculty;
    }
    
    public void setFaculty(String faculty)
    {
        this.faculty = faculty;
    }
    
    public String getProfession()
    {
        return profession;
    }
    
    public void setProfession(String profession)
    {
        this.profession = profession;
    }
    
    public String getKeyword()
    {
        return keyword;
    }
    
    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }
    
    public Integer getCourseType()
    {
        return courseType;
    }
    
    public void setCourseType(Integer courseType)
    {
        this.courseType = courseType;
    }
    
    public String getProjectId()
    {
        return projectId;
    }

    public void setProjectId(String projectId)
    {
        this.projectId = projectId;
    }

    public List<String> getIncludeCourseCodes()
    {
        return includeCourseCodes;
    }
    
    public void setIncludeCourseCodes(List<String> includeCourseCodes)
    {
        this.includeCourseCodes = includeCourseCodes;
    }

	public String getIncludeCourseCode() {
		return includeCourseCode;
	}

	public void setIncludeCourseCode(String includeCourseCode) {
		this.includeCourseCode = includeCourseCode;
	}

    public String getOverseas()
    {
        return overseas;
    }

    public void setOverseas(String overseas)
    {
        this.overseas = StringUtils.trim(overseas);
    }

    public Long getCourseLabelId()
    {
        return courseLabelId;
    }

    public void setCourseLabelId(Long courseLabelId)
    {
        this.courseLabelId = courseLabelId;
    }

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}
    
}
