package com.server.edu.mutual.dto;

import java.util.List;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.mutual.entity.ElcCrossStds;

public class ElcMutualCrossStuDto extends ElcCrossStds{
    private static final long serialVersionUID = 1L;
    
    /**
     * 姓名
     */
    private String studentName;
    
    /**
     * 性别(根据数据字典统一使用)
     */
    @Code2Text(transformer = "G_XBIE")
    private Integer sex;
    
    /**
     * 培养层次(专科   本科   硕士   博士    其他    预科)
     */
    @Code2Text(transformer = "X_PYCC")
    private String trainingLevel;
    
    /**
     * 学院
     */
    @Code2Text(transformer = "X_YX")
    private String faculty;
    
    /**
     * 专业
     */
    @Code2Text(transformer = "G_ZY")
    private String profession;
    
    /**
     * 年级
     */
    private Integer grade;
    
    /**
     * 模型
     */
    private Integer mode;
    
    private List<String> studentIds;
    
    private String projectId;
    
    private String trainingCategory;
    
    private String degreeCategory;
    
    private String formLearning;
    
    private List<Long> ids;

    /*学位类别*/
    private String degreeType;

    /**
     * 关键字（学号或者姓名）
     */
    private String keyWords;
    
    /**
     * 在校状态(1 校内在读、2 校外在读、3 不在读、4 离校)
     */
    private String leaveSchool;

    //学号集合
	private List<String> studentIdList;

	//是否是批量操作：当前端上送studentIdList则此值被设置为true，否则设置为false。
	private Boolean batch = Boolean.FALSE;

	public String getDegreeType() {
		return degreeType;
	}

	public void setDegreeType(String degreeType) {
		this.degreeType = degreeType;
	}

	public String getLeaveSchool() {
		return leaveSchool;
	}

	public void setLeaveSchool(String leaveSchool) {
		this.leaveSchool = leaveSchool;
	}

	public List<Long> getIds() {
		return ids;
	}

	public void setIds(List<Long> ids) {
		this.ids = ids;
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

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public List<String> getStudentIds() {
		return studentIds;
	}

	public void setStudentIds(List<String> studentIds) {
		this.studentIds = studentIds;
	}

	public Integer getMode() {
		return mode;
	}

	public void setMode(Integer mode) {
		this.mode = mode;
	}


	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public String getTrainingLevel() {
		return trainingLevel;
	}

	public void setTrainingLevel(String trainingLevel) {
		this.trainingLevel = trainingLevel;
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

	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	public String getKeyWords() {
		return keyWords;
	}

	public void setKeyWords(String keyWords) {
		this.keyWords = keyWords;
	}

	public List<String> getStudentIdList() {
		return studentIdList;
	}

	public void setStudentIdList(List<String> studentIdList) {
		this.studentIdList = studentIdList;
	}

	public Boolean isBatch() {
		return batch;
	}

	public void setBatch(Boolean batch) {
		this.batch = batch;
	}

	public ElcMutualCrossStuDto() {
	}

	public ElcMutualCrossStuDto(String studentName, Integer sex, String trainingLevel, String faculty, String profession, Integer grade, Integer mode, List<String> studentIds, String projectId, String trainingCategory, String degreeCategory, String formLearning, List<Long> ids, String degreeType, String keyWords, String leaveSchool) {
		this.studentName = studentName;
		this.sex = sex;
		this.trainingLevel = trainingLevel;
		this.faculty = faculty;
		this.profession = profession;
		this.grade = grade;
		this.mode = mode;
		this.studentIds = studentIds;
		this.projectId = projectId;
		this.trainingCategory = trainingCategory;
		this.degreeCategory = degreeCategory;
		this.formLearning = formLearning;
		this.ids = ids;
		this.degreeType = degreeType;
		this.keyWords = keyWords;
		this.leaveSchool = leaveSchool;
	}

	@Override
	public String toString() {
		return "ElcMutualCrossStuDto{" +
				"studentName='" + studentName + '\'' +
				", sex=" + sex +
				", trainingLevel='" + trainingLevel + '\'' +
				", faculty='" + faculty + '\'' +
				", profession='" + profession + '\'' +
				", grade=" + grade +
				", mode=" + mode +
				", studentIds=" + studentIds +
				", projectId='" + projectId + '\'' +
				", trainingCategory='" + trainingCategory + '\'' +
				", degreeCategory='" + degreeCategory + '\'' +
				", formLearning='" + formLearning + '\'' +
				", ids=" + ids +
				", degreeType='" + degreeType + '\'' +
				", keyWords='" + keyWords + '\'' +
				", leaveSchool='" + leaveSchool + '\'' +
				'}';
	}
}
