package com.server.edu.election.vo;

import java.util.List;

import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.election.entity.TeachingClass;
import com.server.edu.election.studentelec.context.TimeAndRoom;

@CodeI18n
public class TeachingClassVo extends TeachingClass 
{
    private String courseCode;
    private String courseName;
    private Double credits;
    
    private String teacherName;
    
    private int withdrawNum;
    /**
     * 课程性质
     */
    @Code2Text(DictTypeEnum.X_KCXZ)
    private String nature;
    
    /**
     * 开课学院
     */
    @Code2Text(DictTypeEnum.X_YX)
    private String faculty;
    
    /**
     * 校区
     */
    @Code2Text(DictTypeEnum.X_XQ)
    private String campus; 
    
    /**
     * 教室容量
     */
    private Integer classNumber;
    
    /**
     * 教学安排（上课时间地点）
     */
    private List<TimeAndRoom> timeTableList;
    
    private Long calendarId;
    
    private String limitFaculty;
    
    private String limitTrainingLevel;
    
    private String limitTrainingCategory;
    
    private String limitSpcialPlan;
    
    private Integer limitIsOverseas;
    
    private Integer limitIsDivsex;
    
    private Integer numberMale;
    
    private Integer numberFemale;
    
    private Integer limitGrade;
    
    private Integer limitProfession;
    
    private Integer limitDirectionCode;
    
    
	public Integer getNumberMale() {
		return numberMale;
	}
	public void setNumberMale(Integer numberMale) {
		this.numberMale = numberMale;
	}
	public Integer getNumberFemale() {
		return numberFemale;
	}
	public void setNumberFemale(Integer numberFemale) {
		this.numberFemale = numberFemale;
	}
	public Integer getLimitGrade() {
		return limitGrade;
	}
	public void setLimitGrade(Integer limitGrade) {
		this.limitGrade = limitGrade;
	}
	public Integer getLimitProfession() {
		return limitProfession;
	}
	public void setLimitProfession(Integer limitProfession) {
		this.limitProfession = limitProfession;
	}
	public Integer getLimitDirectionCode() {
		return limitDirectionCode;
	}
	public void setLimitDirectionCode(Integer limitDirectionCode) {
		this.limitDirectionCode = limitDirectionCode;
	}
	public String getLimitFaculty() {
		return limitFaculty;
	}
	public void setLimitFaculty(String limitFaculty) {
		this.limitFaculty = limitFaculty;
	}
	public String getLimitTrainingLevel() {
		return limitTrainingLevel;
	}
	public void setLimitTrainingLevel(String limitTrainingLevel) {
		this.limitTrainingLevel = limitTrainingLevel;
	}
	public String getLimitTrainingCategory() {
		return limitTrainingCategory;
	}
	public void setLimitTrainingCategory(String limitTrainingCategory) {
		this.limitTrainingCategory = limitTrainingCategory;
	}
	public String getLimitSpcialPlan() {
		return limitSpcialPlan;
	}
	public void setLimitSpcialPlan(String limitSpcialPlan) {
		this.limitSpcialPlan = limitSpcialPlan;
	}
	public Integer getLimitIsOverseas() {
		return limitIsOverseas;
	}
	public void setLimitIsOverseas(Integer limitIsOverseas) {
		this.limitIsOverseas = limitIsOverseas;
	}
	public Integer getLimitIsDivsex() {
		return limitIsDivsex;
	}
	public void setLimitIsDivsex(Integer limitIsDivsex) {
		this.limitIsDivsex = limitIsDivsex;
	}
	public int getWithdrawNum() {
		return withdrawNum;
	}
	public void setWithdrawNum(int withdrawNum) {
		this.withdrawNum = withdrawNum;
	}
	public String getTeacherName() {
		return teacherName;
	}
	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
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
	@Override
	public String getCampus() {
		return campus;
	}
	@Override
	public void setCampus(String campus) {
		this.campus = campus;
	}
	
	public Integer getClassNumber() {
		return classNumber;
	}
	public void setClassNumber(Integer classNumber) {
		this.classNumber = classNumber;
	}
	public List<TimeAndRoom> getTimeTableList() {
		return timeTableList;
	}
	public void setTimeTableList(List<TimeAndRoom> timeTableList) {
		this.timeTableList = timeTableList;
	}
	public Long getCalendarId() {
		return calendarId;
	}
	public void setCalendarId(Long calendarId) {
		this.calendarId = calendarId;
	}
	
	
    
}
