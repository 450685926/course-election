package com.server.edu.mutual.vo;

import java.util.List;

import javax.persistence.Column;

import com.server.edu.election.dto.StudentSchoolTimetab;
import com.server.edu.election.dto.StudnetTimeTable;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.election.studentelec.context.TimeAndRoom;
import com.server.edu.mutual.entity.ElcMutualApply;

@CodeI18n
public class ElcMutualListVo extends ElcMutualApply{
	private static final long serialVersionUID = 1L;
	
	/**
	 * 姓名
	 */
	private String studentName;
	
    /**
     * 学号(重写字段)
     */
    @NotBlank
    @Column(name = "STUDENT_ID_")
    private String studentId;
    
    /**
     * 年级
     */
    private Integer grade;
    
    /**
     * 学生学院（学生所在行政学院）
     */
    @Code2Text(transformer = "X_YX")
    private String college;
    
    /**
     * 开课学院
     */
    @Code2Text(transformer = "X_YX")
    private String openCollege;
	
    /**
     * 专业
     */
    @Code2Text(transformer = "G_ZY")
    private String profession;
	
    /**
     * 培养层次(专科   本科   硕士   博士    其他    预科)
     */
    @Code2Text(transformer = "X_PYCC")
    private String stuTrainingLevel;
    
    /**
     * 课程层次(专科   本科   硕士   博士    其他    预科)
     */
    @Code2Text(transformer = "X_PYCC")
    private String courseTrainingLevel;
    
    /**
     * 学生类别
     */
    @Code2Text(transformer = "X_XSLB")
    private String studentCategory;
    
    /**
     * 培养类别
     */
    @Code2Text(transformer = "X_PYLB")
    private String trainingCategory;
	
    /**
     * 学位类型
     */
    @Code2Text(transformer = "X_XWLX")
    private String degreeType;
    
    /**
     * 学习形式
     */
    @Code2Text(transformer = "X_XXXS")
    private String formLearning;
    
    /**
     * 课程编号
     */
    private String courseCode;
    
    /**
     * 课程名称
     */
    private String courseName;
    
    /**
     * 研究生课程性质
     */
    @Code2Text(transformer="X_KCXZ")
    private String nature;
    
    /**
     * 本科生课程性质
     */
    @Code2Text(transformer="K_BKKCXZ")
    private String isElective;
    
    /**
     * 教学安排（上课时间地点）
     */
    private List<TimeAndRoom> timeTableList;
	/**
	 * 教学安排（上课时间地点）本科生
	 */
	private List<StudnetTimeTable> studnetTimeTable;
	/**
	 * 教学安排（上课时间地点）研究生
	 */
	private List<StudentSchoolTimetab> schoolTimetab;
    /**
     * 学分
     */
    private Double credits;
    
    /**
     * 修读类别(1正常修读,2重修,3免修不免考,4免修)
     */
    private Integer courseTakeType;

	@Code2Text(transformer="X_XDLX")
    private String courseTakeTypeName;
    
    /**
     * 申请人数
     */
    private Integer stuNumber;

	/**
	 * 教学安排
	 * @return
	 */
	private String time;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getStudentId()
    {
        return studentId;
    }

    public void setStudentId(String studentId)
    {
        this.studentId = studentId;
    }

    public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public String getStuTrainingLevel() {
		return stuTrainingLevel;
	}

	public void setStuTrainingLevel(String stuTrainingLevel) {
		this.stuTrainingLevel = stuTrainingLevel;
	}

	public String getTrainingCategory() {
		return trainingCategory;
	}

	public void setTrainingCategory(String trainingCategory) {
		this.trainingCategory = trainingCategory;
	}

	public String getDegreeType() {
		return degreeType;
	}

	public void setDegreeType(String degreeType) {
		this.degreeType = degreeType;
	}

	public String getFormLearning() {
		return formLearning;
	}

	public void setFormLearning(String formLearning) {
		this.formLearning = formLearning;
	}

	public String getCourseCode() {
		return courseCode;
	}

	public void setCourseCode(String courseCode) {
		this.courseCode = courseCode;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getNature() {
		return nature;
	}

	public void setNature(String nature) {
		this.nature = nature;
	}

	public String getIsElective() {
		return isElective;
	}

	public void setIsElective(String isElective) {
		this.isElective = isElective;
	}

	public List<TimeAndRoom> getTimeTableList() {
		return timeTableList;
	}

	public void setTimeTableList(List<TimeAndRoom> timeTableList) {
		this.timeTableList = timeTableList;
	}

	public Double getCredits() {
		return credits;
	}

	public void setCredits(Double credits) {
		this.credits = credits;
	}

	public Integer getCourseTakeType() {
		return courseTakeType;
	}

	public void setCourseTakeType(Integer courseTakeType) {
		this.courseTakeType = courseTakeType;
		if(StringUtils.isEmpty(this.courseTakeTypeName)){
			this.courseTakeTypeName = courseTakeType !=null? String.valueOf(courseTakeType) :"";
		}
	}

	public String getCollege() {
		return college;
	}

	public void setCollege(String college) {
		this.college = college;
	}

	public String getStudentCategory() {
		return studentCategory;
	}

	public void setStudentCategory(String studentCategory) {
		this.studentCategory = studentCategory;
	}

	public String getOpenCollege() {
		return openCollege;
	}

	public void setOpenCollege(String openCollege) {
		this.openCollege = openCollege;
	}

	public String getCourseTrainingLevel() {
		return courseTrainingLevel;
	}

	public void setCourseTrainingLevel(String courseTrainingLevel) {
		this.courseTrainingLevel = courseTrainingLevel;
	}

	public Integer getStuNumber() {
		return stuNumber;
	}

	public void setStuNumber(Integer stuNumber) {
		this.stuNumber = stuNumber;
	}

	public String getCourseTakeTypeName() {
		return courseTakeTypeName;
	}

	public void setCourseTakeTypeName(String courseTakeTypeName) {
		this.courseTakeTypeName = courseTakeTypeName;
	}

	public List<StudentSchoolTimetab> getSchoolTimetab() {
		return schoolTimetab;
	}

	public void setSchoolTimetab(List<StudentSchoolTimetab> schoolTimetab) {
		this.schoolTimetab = schoolTimetab;
	}

	public List<StudnetTimeTable> getStudnetTimeTable() {
		return studnetTimeTable;
	}

	public void setStudnetTimeTable(List<StudnetTimeTable> studnetTimeTable) {
		this.studnetTimeTable = studnetTimeTable;
	}

	public ElcMutualListVo() {
	}

	public ElcMutualListVo(String studentName, String studentId, Integer grade, String college, String openCollege, String profession, String stuTrainingLevel, String courseTrainingLevel, String studentCategory, String trainingCategory, String degreeType, String formLearning, String courseCode, String courseName, String nature, String isElective, List<TimeAndRoom> timeTableList, List<StudnetTimeTable> studnetTimeTable, List<StudentSchoolTimetab> schoolTimetab, Double credits, Integer courseTakeType, String courseTakeTypeName, Integer stuNumber, String time) {
		this.studentName = studentName;
		this.studentId = studentId;
		this.grade = grade;
		this.college = college;
		this.openCollege = openCollege;
		this.profession = profession;
		this.stuTrainingLevel = stuTrainingLevel;
		this.courseTrainingLevel = courseTrainingLevel;
		this.studentCategory = studentCategory;
		this.trainingCategory = trainingCategory;
		this.degreeType = degreeType;
		this.formLearning = formLearning;
		this.courseCode = courseCode;
		this.courseName = courseName;
		this.nature = nature;
		this.isElective = isElective;
		this.timeTableList = timeTableList;
		this.studnetTimeTable = studnetTimeTable;
		this.schoolTimetab = schoolTimetab;
		this.credits = credits;
		this.courseTakeType = courseTakeType;
		this.courseTakeTypeName = courseTakeTypeName;
		this.stuNumber = stuNumber;
		this.time = time;
	}

	@Override
	public String toString() {
		return "ElcMutualListVo{" +
				"studentName='" + studentName + '\'' +
				", studentId='" + studentId + '\'' +
				", grade=" + grade +
				", college='" + college + '\'' +
				", openCollege='" + openCollege + '\'' +
				", profession='" + profession + '\'' +
				", stuTrainingLevel='" + stuTrainingLevel + '\'' +
				", courseTrainingLevel='" + courseTrainingLevel + '\'' +
				", studentCategory='" + studentCategory + '\'' +
				", trainingCategory='" + trainingCategory + '\'' +
				", degreeType='" + degreeType + '\'' +
				", formLearning='" + formLearning + '\'' +
				", courseCode='" + courseCode + '\'' +
				", courseName='" + courseName + '\'' +
				", nature='" + nature + '\'' +
				", isElective='" + isElective + '\'' +
				", timeTableList=" + timeTableList +
				", studnetTimeTable=" + studnetTimeTable +
				", schoolTimetab=" + schoolTimetab +
				", credits=" + credits +
				", courseTakeType=" + courseTakeType +
				", courseTakeTypeName='" + courseTakeTypeName + '\'' +
				", stuNumber=" + stuNumber +
				", time='" + time + '\'' +
				'}';
	}
}
