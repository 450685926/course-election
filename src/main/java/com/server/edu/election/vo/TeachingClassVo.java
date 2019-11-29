package com.server.edu.election.vo;

import java.util.List;

import javax.validation.constraints.NotNull;

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
    
    private List<String> ids;

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
     * 教室容量字符串
     */
    private String classNumberStr;
    
    /**
     * 教学安排（上课时间地点）
     */
    private List<TimeAndRoom> timeTableList;
    
    private String timeAndRoom;

    private Long calendarId;
    
    private String limitFaculty;
    
    private String limitTrainingLevel;
    
    private String limitTrainingCategory;
    
    private String limitSpcialPlan;
    
    private Integer limitIsOverseas;
    
    private String limitIsDivsex;
    @NotNull
    private Integer numberMale;
    @NotNull
    private Integer numberFemale;
    
    private Integer limitGrade;
    
    private String limitProfession;
    
    private String limitDirectionCode;
    
    private String proportion;
    
    /**
     *  教师ID
     */
    private String roomId;
    
    private Integer firstTurnNum;
    
    private Integer secondTurnNum;
    /**
     *  选课统计筛选标签
     */
    private String labelName;
    
    /**
     * 教师code集合字符串
     */
    private String teacherCodes;

    /**
     * 绑定班级Id
     * @return
     */
    private String bindClassId;

	/**
	 * 绑定班级序号
	 * @return
	 */
	private String bindClassCode;

	@Code2Text(transformer="K_BKKCXZ")
	private Integer isElective;

	public Integer getIsElective() {
		return isElective;
	}

	public void setIsElective(Integer isElective) {
		this.isElective = isElective;
	}

	public String getTeacherCodes() {
		return teacherCodes;
	}
	public void setTeacherCodes(String teacherCodes) {
		this.teacherCodes = teacherCodes;
	}
	public String getLabelName() {
		return labelName;
	}
	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}
	public Integer getFirstTurnNum() {
		return firstTurnNum;
	}
	public void setFirstTurnNum(Integer firstTurnNum) {
		this.firstTurnNum = firstTurnNum;
	}
	public Integer getSecondTurnNum() {
		return secondTurnNum;
	}
	public void setSecondTurnNum(Integer secondTurnNum) {
		this.secondTurnNum = secondTurnNum;
	}
	public String getProportion() {
		return proportion;
	}
	public void setProportion(String proportion) {
		this.proportion = proportion;
	}
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
	public String getLimitProfession() {
		return limitProfession;
	}
	public void setLimitProfession(String limitProfession) {
		this.limitProfession = limitProfession;
	}
	public String getLimitDirectionCode() {
		return limitDirectionCode;
	}
	public void setLimitDirectionCode(String limitDirectionCode) {
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
	public String getLimitIsDivsex() {
		return limitIsDivsex;
	}
	public void setLimitIsDivsex(String limitIsDivsex) {
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
	public String getRoomId() {
		return roomId;
	}
	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}
	public String getClassNumberStr() {
		return classNumberStr;
	}
	public void setClassNumberStr(String classNumberStr) {
		this.classNumberStr = classNumberStr;
	}
	public String getBindClassId() {
		return bindClassId;
	}
	public void setBindClassId(String bindClassId) {
		this.bindClassId = bindClassId;
	}

	public List<String> getIds() {
		return ids;
	}

	public void setIds(List<String> ids) {
		this.ids = ids;
	}

	public String getTimeAndRoom() {
		return timeAndRoom;
	}

	public void setTimeAndRoom(String timeAndRoom) {
		this.timeAndRoom = timeAndRoom;
	}

	public String getBindClassCode() {
		return bindClassCode;
	}

	public void setBindClassCode(String bindClassCode) {
		this.bindClassCode = bindClassCode;
	}
}
