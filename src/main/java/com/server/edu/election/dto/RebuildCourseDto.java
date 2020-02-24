package com.server.edu.election.dto;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.election.entity.RebuildCourseNoChargeType;

/**
 * @description:
 * @author: bear
 * @create: 2019-05-23 17:23
 */
public class RebuildCourseDto {
    @NotNull
    private Long calendarId;
    
    private String courseCode;

    /**课程名称*/
    private String courseName;

    /**课程性质*/
    private String nature;
    
    private String label;
    
    private Integer isCharge;
    
    private Integer grade;
    
    @Code2Text(transformer = "X_YX")
    private String faculty;
    
    @Code2Text(transformer = "G_ZY")
    private String major;
    
    private String studentCategory;
    
    private String keyWord;
    
    private Integer mode;
    
    private String deptId;

    private Integer paid;
    
    private Integer type;

    private Integer turn;

    private String turnName;

    private String labelName;
    /**
     * 选课对象未翻译字段
     */
    private String electionObj;


    /**
     * 专业
     * @return
     */
    @Code2Text(DictTypeEnum.G_ZY)
    private String profession;

    /**
     * 开课学院
     */
    @Code2Text(DictTypeEnum.X_YX)
    private String courseFaculty;

    @Code2Text(DictTypeEnum.X_XDLX)
    private Integer courseTakeType;

    /**
     * 不收费学生类型
     */
    private List<RebuildCourseNoChargeType> noStuPay = new ArrayList<>();

    private Long abnormalEndTime;
    private Long abnormalStartTime;

    //学年
    private Integer year;
    //学期
    private Integer semester;

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public Long getAbnormalEndTime() {
        return abnormalEndTime;
    }

    public void setAbnormalEndTime(Long abnormalEndTime) {
        this.abnormalEndTime = abnormalEndTime;
    }

    public Long getAbnormalStartTime() {
        return abnormalStartTime;
    }

    public void setAbnormalStartTime(Long abnormalStartTime) {
        this.abnormalStartTime = abnormalStartTime;
    }

    public List<RebuildCourseNoChargeType> getNoStuPay() {
        return noStuPay;
    }

    public void setNoStuPay(List<RebuildCourseNoChargeType> noStuPay) {
        this.noStuPay = noStuPay;
    }

    public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getPaid() {
        return paid;
    }

    public void setPaid(Integer paid) {
        this.paid = paid;
    }

    /**学号*/
    private String studentId;
    /**姓名*/
    private String studentName;
    /**性别*/
    private String sex;
    /**课程序号*/
    private String teachingClassCode;

    /**id集合*/
    private List<Long> ids;
    private Long id;

    /**分库分表标识*/
    private Integer index;

    /**是否编级。1是0否*/
    private Integer hasChangeGrade;

    public Integer getHasChangeGrade() {
        return hasChangeGrade;
    }

    public void setHasChangeGrade(Integer hasChangeGrade) {
        this.hasChangeGrade = hasChangeGrade;
    }

    //============冗余两个字段，校历开始时间。结束时间================
    private Long endTime;
    private Long beginTime;

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Long beginTime) {
        this.beginTime = beginTime;
    }
    //==============================================================

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
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

    public String getTeachingClassCode() {
        return teachingClassCode;
    }

    public void setTeachingClassCode(String teachingClassCode) {
        this.teachingClassCode = teachingClassCode;
    }

    /**培养计划对应课程Id集合*/
    private List<Long> planCourseIds;

    public List<Long> getPlanCourseIds() {
        return planCourseIds;
    }

    public void setPlanCourseIds(List<Long> planCourseIds) {
        this.planCourseIds = planCourseIds;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getDeptId()
    {
        return deptId;
    }
    
    public void setDeptId(String deptId)
    {
        this.deptId = deptId;
    }
    
    public Long getCalendarId()
    {
        return calendarId;
    }
    
    public void setCalendarId(Long calendarId)
    {
        this.calendarId = calendarId;
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
    
    public String getLabel()
    {
        return label;
    }
    
    public void setLabel(String label)
    {
        this.label = label;
    }
    
    public Integer getIsCharge()
    {
        return isCharge;
    }
    
    public void setIsCharge(Integer isCharge)
    {
        this.isCharge = isCharge;
    }
    
    public Integer getGrade()
    {
        return grade;
    }
    
    public void setGrade(Integer grade)
    {
        this.grade = grade;
    }
    
    public String getFaculty()
    {
        return faculty;
    }
    
    public void setFaculty(String faculty)
    {
        this.faculty = faculty;
    }
    
    public String getMajor()
    {
        return major;
    }
    
    public void setMajor(String major)
    {
        this.major = major;
    }
    
    public String getStudentCategory()
    {
        return studentCategory;
    }
    
    public void setStudentCategory(String studentCategory)
    {
        this.studentCategory = studentCategory;
    }
    
    public String getKeyWord()
    {
        return keyWord;
    }
    
    public void setKeyWord(String keyWord)
    {
        this.keyWord = keyWord;
    }
    
    public Integer getMode()
    {
        return mode;
    }
    
    public void setMode(Integer mode)
    {
        this.mode = mode;
    }

    public Integer getTurn() {
        return turn;
    }

    public void setTurn(Integer turn) {
        this.turn = turn;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getCourseFaculty() {
        return courseFaculty;
    }

    public void setCourseFaculty(String courseFaculty) {
        this.courseFaculty = courseFaculty;
    }

    public Integer getCourseTakeType() {
        return courseTakeType;
    }

    public void setCourseTakeType(Integer courseTakeType) {
        this.courseTakeType = courseTakeType;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getElectionObj() {
        return electionObj;
    }

    public void setElectionObj(String electionObj) {
        this.electionObj = electionObj;
    }


    public String getTurnName() {
        return turnName;
    }

    public void setTurnName(String turnName) {
        this.turnName = turnName;
    }
}
