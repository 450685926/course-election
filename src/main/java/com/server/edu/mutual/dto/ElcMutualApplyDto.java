package com.server.edu.mutual.dto;

import java.util.List;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.mutual.entity.ElcMutualApply;

@CodeI18n
public class ElcMutualApplyDto extends ElcMutualApply {
    private static final long serialVersionUID = 1L;
    
    private String courseCode;
    
    private String courseName;
    
    private Double credits;
    
    /**
     * 学生所在行政学院
     */
    @Code2Text(transformer="X_YX")
    private String college;
    
    /**
     * 课程所在学院（开课学院）
     */
    @Code2Text(transformer="X_YX")
    private String openCollege;
    
    /** 本科生课程性质 */
    @Code2Text(transformer="X_KCXZ")
    private String nature;
    
    /** 研究生课程性质 */
    @Code2Text(transformer="K_BKKCXZ")
    private String isElective;
    
    @Code2Text(transformer="X_KSLX")
    private String assessmentMode; 
    /**
     * 课程层次
     */
    @Code2Text(transformer = "X_PYCC")
    private String trainingLevel;
    
    private Integer mode;
    
    /** 本研互选开关类别：1:本研互选  2：跨学科互选 */
    private Integer category;
    
    private Integer byType;
    
    private Integer inType;
    
    private List<Long> mutualCourseIds;
    
    private String projectId;
    private List<String> projectIds;
    
    private String profession;
    
    private String studentName;
    
    private String stuTrainingLevel;
    
    private Integer grade;
    
    /** 审核通过标志（1-审核通过；0-审核不通过） */
    private Integer auditFlag; 
    
    /** 审核类型（1-行政学院审核、2-开课学院审核） */
    private Integer auditType; 
    
    /**
     * 关键字（课程编号或者课程名称）
     */
    private String keyWords;
    
    /**
     * 审核状态集合
     */
    private List<Integer> statusArray;

    //可管理的学院（当前学院+管理的学院）
    private List<String> collegeList;

	/*学生申请增加选课标志位*/
	private String courseSelectionMark;

	public String getCourseSelectionMark() {
		return courseSelectionMark;
	}

	public void setCourseSelectionMark(String courseSelectionMark) {
		this.courseSelectionMark = courseSelectionMark;
	}

	public Integer getGrade() {
		return grade;
	}
	public void setGrade(Integer grade) {
		this.grade = grade;
	}
	public String getOpenCollege() {
		return openCollege;
	}
	public void setOpenCollege(String openCollege) {
		this.openCollege = openCollege;
	}
	public String getStuTrainingLevel() {
		return stuTrainingLevel;
	}
	public void setStuTrainingLevel(String stuTrainingLevel) {
		this.stuTrainingLevel = stuTrainingLevel;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public String getProfession() {
		return profession;
	}
	public void setProfession(String profession) {
		this.profession = profession;
	}
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	public List<Long> getMutualCourseIds() {
		return mutualCourseIds;
	}
	public void setMutualCourseIds(List<Long> mutualCourseIds) {
		this.mutualCourseIds = mutualCourseIds;
	}
	public Integer getByType() {
		return byType;
	}
	public void setByType(Integer byType) {
		this.byType = byType;
	}
	public Integer getInType() {
		return inType;
	}
	public void setInType(Integer inType) {
		this.inType = inType;
	}
	public Integer getMode() {
		return mode;
	}
	public void setMode(Integer mode) {
		this.mode = mode;
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
	public Double getCredits() {
		return credits;
	}
	public void setCredits(Double credits) {
		this.credits = credits;
	}
	public String getCollege() {
		return college;
	}
	public void setCollege(String college) {
		this.college = college;
	}
	public String getNature() {
		return nature;
	}
	public void setNature(String nature) {
		this.nature = nature;
	}
	public String getAssessmentMode() {
		return assessmentMode;
	}
	public void setAssessmentMode(String assessmentMode) {
		this.assessmentMode = assessmentMode;
	}
	public String getTrainingLevel() {
		return trainingLevel;
	}
	public void setTrainingLevel(String trainingLevel) {
		this.trainingLevel = trainingLevel;
	}
	public Integer getCategory() {
		return category;
	}
	public void setCategory(Integer category) {
		this.category = category;
	}
	public String getIsElective() {
		return isElective;
	}
	public void setIsElective(String isElective) {
		this.isElective = isElective;
	}
	public List<String> getProjectIds() {
		return projectIds;
	}
	public void setProjectIds(List<String> projectIds) {
		this.projectIds = projectIds;
	}
	public Integer getAuditFlag() {
		return auditFlag;
	}
	public void setAuditFlag(Integer auditFlag) {
		this.auditFlag = auditFlag;
	}
	public Integer getAuditType() {
		return auditType;
	}
	public void setAuditType(Integer auditType) {
		this.auditType = auditType;
	}
	public String getKeyWords() {
		return keyWords;
	}
	public void setKeyWords(String keyWords) {
		this.keyWords = keyWords;
	}
	public List<Integer> getStatusArray() {
		return statusArray;
	}
	public void setStatusArray(List<Integer> statusArray) {
		this.statusArray = statusArray;
	}

	public List<String> getCollegeList() {
		return collegeList;
	}

	public void setCollegeList(List<String> collegeList) {
		this.collegeList = collegeList;
	}

	public ElcMutualApplyDto() {

	}

	public ElcMutualApplyDto(String courseCode, String courseName, Double credits, String college, String openCollege, String nature, String isElective, String assessmentMode, String trainingLevel, Integer mode, Integer category, Integer byType, Integer inType, List<Long> mutualCourseIds, String projectId, List<String> projectIds, String profession, String studentName, String stuTrainingLevel, Integer grade, Integer auditFlag, Integer auditType, String keyWords, List<Integer> statusArray, List<String> collegeList, String courseSelectionMark) {
		this.courseCode = courseCode;
		this.courseName = courseName;
		this.credits = credits;
		this.college = college;
		this.openCollege = openCollege;
		this.nature = nature;
		this.isElective = isElective;
		this.assessmentMode = assessmentMode;
		this.trainingLevel = trainingLevel;
		this.mode = mode;
		this.category = category;
		this.byType = byType;
		this.inType = inType;
		this.mutualCourseIds = mutualCourseIds;
		this.projectId = projectId;
		this.projectIds = projectIds;
		this.profession = profession;
		this.studentName = studentName;
		this.stuTrainingLevel = stuTrainingLevel;
		this.grade = grade;
		this.auditFlag = auditFlag;
		this.auditType = auditType;
		this.keyWords = keyWords;
		this.statusArray = statusArray;
		this.collegeList = collegeList;
		this.courseSelectionMark = courseSelectionMark;
	}

	@Override
	public String toString() {
		return "ElcMutualApplyDto{" +
				"courseCode='" + courseCode + '\'' +
				", courseName='" + courseName + '\'' +
				", credits=" + credits +
				", college='" + college + '\'' +
				", openCollege='" + openCollege + '\'' +
				", nature='" + nature + '\'' +
				", isElective='" + isElective + '\'' +
				", assessmentMode='" + assessmentMode + '\'' +
				", trainingLevel='" + trainingLevel + '\'' +
				", mode=" + mode +
				", category=" + category +
				", byType=" + byType +
				", inType=" + inType +
				", mutualCourseIds=" + mutualCourseIds +
				", projectId='" + projectId + '\'' +
				", projectIds=" + projectIds +
				", profession='" + profession + '\'' +
				", studentName='" + studentName + '\'' +
				", stuTrainingLevel='" + stuTrainingLevel + '\'' +
				", grade=" + grade +
				", auditFlag=" + auditFlag +
				", auditType=" + auditType +
				", keyWords='" + keyWords + '\'' +
				", statusArray=" + statusArray +
				", collegeList=" + collegeList +
				", courseSelectionMark='" + courseSelectionMark + '\'' +
				'}';
	}
}
