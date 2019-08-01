package com.server.edu.election.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "exemption_apply_graduate_condition_t")
public class ExemptionApplyGraduteCondition  implements Serializable{
	private static final long serialVersionUID = 1L;

	/**
	 * 主键（自增）
     */
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
            * 课程code
     */
    @Column(name = "COURSE_CODE_")
    private String courseCode;
    
    /**
     * 课程名称
     */
    @Column(name = "COURSE_NAME_")
    private String courseName;
    
    /**
     *培养层次(逗号分隔，字典表X_PYCC)
     */
    @Column(name = "TRAINING_LEVELS_")
    private String trainingLevels;
	
    /**
     * 学习形式(逗号分隔，字典表X_XXXS)
     */
    @Column(name = "FORM_LEARNINGS_")
    private String formLearnings;
	
    /**
     * 培养类别(逗号分隔，字典表X_PYLB)
     */
    @Column(name = "TRAINING_CATEGORYS_")
    private String trainingCategorys;
    
    /**
     * 学位类型(逗号分隔，字典表X_XWLX)
     */
    @Column(name = "DEGREE_TYPES_")
    private String degreeTypes;
    
    /**
     * 条件内容
     */
    @Column(name = "CONDITIONS_")
    private String conditions;
    
    /**
     * 创建人（老系统USER_ID_）
     */
    @Column(name = "CREATE_BY_")
    private String createBy;
    
    /**
     * 创建时间
     */
    @Column(name = "CREATED_AT_")
    private Date createdAt;
    
    /**
     * 更新时间
     */
    @Column(name = "UPDATED_AT_")
    private Date updatedAt;
    
    /**
     * 管理部门id（字典取值）
     */
    @Column(name = "MANAGER_DEPT_ID_")
    private String projId;
    
    /**
     * 是否删除（1-已删除；0-未删除）
     */
    @Column(name = "DELETE_STATUS_")
    private Integer deleteStatus;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getTrainingLevels() {
		return trainingLevels;
	}

	public void setTrainingLevels(String trainingLevels) {
		this.trainingLevels = trainingLevels;
	}

	public String getFormLearnings() {
		return formLearnings;
	}

	public void setFormLearnings(String formLearnings) {
		this.formLearnings = formLearnings;
	}

	public String getTrainingCategorys() {
		return trainingCategorys;
	}

	public void setTrainingCategorys(String trainingCategorys) {
		this.trainingCategorys = trainingCategorys;
	}

	public String getDegreeTypes() {
		return degreeTypes;
	}

	public void setDegreeTypes(String degreeTypes) {
		this.degreeTypes = degreeTypes;
	}

	public String getConditions() {
		return conditions;
	}

	public void setConditions(String conditions) {
		this.conditions = conditions;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getProjId() {
		return projId;
	}

	public void setProjId(String projId) {
		this.projId = projId;
	}

	public Integer getDeleteStatus() {
		return deleteStatus;
	}

	public void setDeleteStatus(Integer deleteStatus) {
		this.deleteStatus = deleteStatus;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((courseCode == null) ? 0 : courseCode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExemptionApplyGraduteCondition other = (ExemptionApplyGraduteCondition) obj;
		if (courseCode == null) {
			if (other.courseCode != null)
				return false;
		} else if (!courseCode.equals(other.courseCode))
			return false;
		return true;
	}

	
}
