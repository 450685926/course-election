package com.server.edu.election.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "exemption_apply_audit_switch_t")
public class ExemptionApplyAuditSwitch implements Serializable{
	private static final long serialVersionUID = 1L;
	
    /**
     * 主键（自增）
     */
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 年级(逗号分隔)
     */
    @Column(name = "GRADES_")
    private String grades;
	
    /**
     * 入学成绩优秀线
     */
    @Column(name = "EXCELLENT_SCORE_")
    private Double excellentScore;
	
    /**
     * 培养层次(逗号分隔，字典表X_PYCC)
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
     * 学位类别(逗号分隔，字典表X_XWLB)
     */
    @Column(name = "DEGREE_CATEGORYS_")
    private String degreeCategorys;
    
    /**
     * 入学季节
     */
    @Column(name = "ENROL_SEASON_")
    private String enrolSeason;
    
    /**
     * 是否申请开放(1-是，0-否)
     */
    @Column(name = "APPLY_OPEN_")
    private Integer applyOpen;
    
    /**
     * 是否审核开放(1-是，0-否)
     */
    @Column(name = "AUDIT_OPEN_")
    private Integer auditOpen;
    
    /**
     * 创建时间
     */
    @Column(name = "CREATED_AT_")
    private Date createdAt;
    
    /**
     * 管理部门ID
     */
    @Column(name = "PROJ_ID_")
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

	public String getGrades() {
		return grades;
	}

	public void setGrades(String grades) {
		this.grades = grades;
	}

	public Double getExcellentScore() {
		return excellentScore;
	}

	public void setExcellentScore(Double excellentScore) {
		this.excellentScore = excellentScore;
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

	public String getDegreeCategorys() {
		return degreeCategorys;
	}

	public void setDegreeCategorys(String degreeCategorys) {
		this.degreeCategorys = degreeCategorys;
	}

	public String getEnrolSeason() {
		return enrolSeason;
	}

	public void setEnrolSeason(String enrolSeason) {
		this.enrolSeason = enrolSeason;
	}

	public Integer getApplyOpen() {
		return applyOpen;
	}

	public void setApplyOpen(Integer applyOpen) {
		this.applyOpen = applyOpen;
	}

	public Integer getAuditOpen() {
		return auditOpen;
	}

	public void setAuditOpen(Integer auditOpen) {
		this.auditOpen = auditOpen;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
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
    
}
