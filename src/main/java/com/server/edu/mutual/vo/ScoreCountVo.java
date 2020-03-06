package com.server.edu.mutual.vo;

import java.util.List;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;

@CodeI18n
public class ScoreCountVo {
	//课程代码
	private String  courseCode;
	//课程名称
	private String  courseName;
	//学号
	private String studentId;
	//姓名
	private String studentName;
	//年级
    private String  yearGrade;
	//导师
	private String  tutor;
	 //专业
	@Code2Text(transformer="G_ZY")
  	private String profession;
  	//学院
  	@Code2Text(transformer="X_YX")
  	private String college;
    //培养层次
  	@Code2Text(transformer="X_PYCC")
  	private String trainingLevel; 
  	//培养类别
  	@Code2Text(transformer="X_PYLB")
  	private String trainingCategory;
  	//学位类别
  	@Code2Text(transformer="X_XWLB")
  	private String degreeCategory;
	//总成绩=课程成绩*课程学分数
    private Double totalScore;
    //学位课总成绩
    private Double totalScoreFordegree;
    //非学位课总成绩
    private Double totalScoreForNodegree;
    //总学分数
    private Double totalCredit;
    //学位课总学分
    private Double totalCreditFordegree;
    //非学位课总学分
    private Double totalCreditForNodegree;
    //标记总学分数
    private Double totalMarkCredit;
    //学位课平均成绩
    private Double degreeAvgScore;
    //非学位课平均成绩
    private Double noDegreeAvgScore;
    //是否学位课
    private Integer isDegreeCourse;
    //平均成绩
    private Double avgTotalScore;
    //学习形式
    @Code2Text(transformer="X_XXXS")
	private String formLearning;
    //总门数
    private  Integer totalCourseNum;
    //校历ID
    private Long calendarId;
    //校历名称
    private String calendar;
    //总学位学分数
    private Double totalDegreeCredit;
    //总绩点
    private Double totalGradePoint;
    //学位课总绩点
    private Double totalGradePointForDegree;
    //非学位课总绩点
    private Double totalGradePointNoDegree;
    //平均绩点   计算公式  sum(绩点*学分)/sum(学分)
    private Double avgGradePoint;
    //统计时间
    private String countTime; 
    //学院统计信息
    private List<String> scoreCollegeCountList;
    //学生统计信息
    private List<String> studentList;

	//学位类型
	@Code2Text(transformer="X_XWLX")
	private String degreeType;

	public Double getTotalMarkCredit() {
		return totalMarkCredit;
	}

	public Double getTotalScoreFordegree() {
		return totalScoreFordegree;
	}

	public void setTotalScoreFordegree(Double totalScoreFordegree) {
		this.totalScoreFordegree = totalScoreFordegree;
	}

	public Double getTotalScoreForNodegree() {
		return totalScoreForNodegree;
	}

	public void setTotalScoreForNodegree(Double totalScoreForNodegree) {
		this.totalScoreForNodegree = totalScoreForNodegree;
	}

	public Double getTotalCreditFordegree() {
		return totalCreditFordegree;
	}

	public void setTotalCreditFordegree(Double totalCreditFordegree) {
		this.totalCreditFordegree = totalCreditFordegree;
	}

	public Double getTotalCreditForNodegree() {
		return totalCreditForNodegree;
	}

	public void setTotalCreditForNodegree(Double totalCreditForNodegree) {
		this.totalCreditForNodegree = totalCreditForNodegree;
	}

	public Double getTotalGradePointForDegree() {
		return totalGradePointForDegree;
	}

	public void setTotalGradePointForDegree(Double totalGradePointForDegree) {
		this.totalGradePointForDegree = totalGradePointForDegree;
	}

	public Double getTotalGradePointNoDegree() {
		return totalGradePointNoDegree;
	}

	public void setTotalGradePointNoDegree(Double totalGradePointNoDegree) {
		this.totalGradePointNoDegree = totalGradePointNoDegree;
	}

	public void setTotalMarkCredit(Double totalMarkCredit) {
		this.totalMarkCredit = totalMarkCredit;
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
	public String getYearGrade() {
		return yearGrade;
	}
	public void setYearGrade(String yearGrade) {
		this.yearGrade = yearGrade;
	}
	public String getTutor() {
		return tutor;
	}
	public void setTutor(String tutor) {
		this.tutor = tutor;
	}
	public String getProfession() {
		return profession;
	}
	public void setProfession(String profession) {
		this.profession = profession;
	}
	public String getCollege() {
		return college;
	}
	public void setCollege(String college) {
		this.college = college;
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
	public String getDegreeCategory() {
		return degreeCategory;
	}
	public void setDegreeCategory(String degreeCategory) {
		this.degreeCategory = degreeCategory;
	}
	public String getFormLearning() {
		return formLearning;
	}
	public void setFormLearning(String formLearning) {
		this.formLearning = formLearning;
	}
	public Double getTotalScore() {
		return totalScore;
	}
	public void setTotalScore(Double totalScore) {
		this.totalScore = totalScore;
	}
	public Double getTotalCredit() {
		return totalCredit;
	}
	public void setTotalCredit(Double totalCredit) {
		this.totalCredit = totalCredit;
	}
	public Double getDegreeAvgScore() {
		return degreeAvgScore;
	}
	public void setDegreeAvgScore(Double degreeAvgScore) {
		this.degreeAvgScore = degreeAvgScore;
	}
	public Double getNoDegreeAvgScore() {
		return noDegreeAvgScore;
	}
	public void setNoDegreeAvgScore(Double noDegreeAvgScore) {
		this.noDegreeAvgScore = noDegreeAvgScore;
	}
	public Integer getIsDegreeCourse() {
		return isDegreeCourse;
	}
	public void setIsDegreeCourse(Integer isDegreeCourse) {
		this.isDegreeCourse = isDegreeCourse;
	}
	public Double getAvgTotalScore() {
		return avgTotalScore;
	}
	public void setAvgTotalScore(Double avgTotalScore) {
		this.avgTotalScore = avgTotalScore;
	}
	public Integer getTotalCourseNum() {
		return totalCourseNum;
	}
	public void setTotalCourseNum(Integer totalCourseNum) {
		this.totalCourseNum = totalCourseNum;
	}
	public Long getCalendarId() {
		return calendarId;
	}
	public void setCalendarId(Long calendarId) {
		this.calendarId = calendarId;
	}
	public String getCalendar() {
		return calendar;
	}
	public void setCalendar(String calendar) {
		this.calendar = calendar;
	}
	public Double getTotalDegreeCredit() {
		return totalDegreeCredit;
	}
	public void setTotalDegreeCredit(Double totalDegreeCredit) {
		this.totalDegreeCredit = totalDegreeCredit;
	}
	public Double getTotalGradePoint() {
		return totalGradePoint;
	}
	public void setTotalGradePoint(Double totalGradePoint) {
		this.totalGradePoint = totalGradePoint;
	}
	public Double getAvgGradePoint() {
		return avgGradePoint;
	}
	public void setAvgGradePoint(Double avgGradePoint) {
		this.avgGradePoint = avgGradePoint;
	}
	public String getCountTime() {
		return countTime;
	}
	public void setCountTime(String countTime) {
		this.countTime = countTime;
	}
	public List<String> getScoreCollegeCountList() {
		return scoreCollegeCountList;
	}
	public void setScoreCollegeCountList(
			List<String> scoreCollegeCountList) {
		this.scoreCollegeCountList = scoreCollegeCountList;
	}
	public List<String> getStudentList() {
		return studentList;
	}
	public void setStudentList(List<String> studentList) {
		this.studentList = studentList;
	}

	public String getDegreeType() {
		return degreeType;
	}

	@Override
	public String toString() {
		return "ScoreCountVo [courseCode=" + courseCode + ", courseName=" + courseName + ", studentId=" + studentId
				+ ", studentName=" + studentName + ", yearGrade=" + yearGrade + ", tutor=" + tutor + ", profession="
				+ profession + ", college=" + college + ", trainingLevel=" + trainingLevel + ", trainingCategory="
				+ trainingCategory + ", degreeCategory=" + degreeCategory + ", totalScore=" + totalScore
				+ ", totalCredit=" + totalCredit + ", totalMarkCredit=" + totalMarkCredit + ", degreeAvgScore="
				+ degreeAvgScore + ", noDegreeAvgScore=" + noDegreeAvgScore + ", isDegreeCourse=" + isDegreeCourse
				+ ", avgTotalScore=" + avgTotalScore + ", formLearning=" + formLearning + ", totalCourseNum="
				+ totalCourseNum + ", calendarId=" + calendarId + ", calendar=" + calendar + ", totalDegreeCredit="
				+ totalDegreeCredit + ", totalGradePoint=" + totalGradePoint + ", avgGradePoint=" + avgGradePoint
				+ ", countTime=" + countTime + ", scoreCollegeCountList=" + scoreCollegeCountList + ", studentList="
				+ studentList + ", degreeType=" + degreeType + "]";
	}

	public void setDegreeType(String degreeType) {
		this.degreeType = degreeType;
	}
	
}
