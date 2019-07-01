package com.server.edu.election.entity;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "election_rounds_condition_t")
public class ElcRoundCondition implements Serializable {
    /**
     * 轮次ID
     */
    @Id
    @Column(name = "ROUND_ID_")
    private Long roundId;

    /**
     * 培养层次(逗号分隔)
     */
    @Column(name = "TRAINING_LEVELS_")
    private String trainingLevels;

    /**
     * 年级(逗号分隔)
     */
    @Column(name = "GRADES_")
    private String grades;

    /**
     * 校区(逗号分隔)
     */
    @Column(name = "CAMPUS_")
    private String campus;

    /**
     * 学院(逗号分隔)
     */
    @Column(name = "FACULTYS_")
    private String facultys;

    /**
     * 专业(逗号分隔)
     */
    @Column(name = "MAJORS_")
    private String majors;

    /**
     * 专项计划(逗号分隔)
     */
    @Column(name = "SPCIAL_PLANS_")
    private String spcialPlans;
    
    /**
              *  培养类别(逗号分隔)
     */
    @Column(name = "TRAINING_CATEGORYS_")
    private String trainingCategorys;
    
    /**
             *  学位类型(逗号分隔)
     */
    @Column(name = "DEGREE_TYPES_")
    private String degreeTypes;
    
    /**
            *  学习形式(逗号分隔)
     */
    @Column(name = "FORM_LEARNINGS_")
    private String formLearings;

    private static final long serialVersionUID = 1L;

    /**
     * 获取轮次ID
     *
     * @return ROUND_ID_ - 轮次ID
     */
    public Long getRoundId() {
        return roundId;
    }

    /**
     * 设置轮次ID
     *
     * @param roundId 轮次ID
     */
    public void setRoundId(Long roundId) {
        this.roundId = roundId;
    }

    /**
     * 获取培养层次(逗号分隔)
     *
     * @return TRAINING_LEVELS_ - 培养层次(逗号分隔)
     */
    public String getTrainingLevels() {
        return trainingLevels;
    }

    /**
     * 设置培养层次(逗号分隔)
     *
     * @param trainingLevels 培养层次(逗号分隔)
     */
    public void setTrainingLevels(String trainingLevels) {
        this.trainingLevels = trainingLevels == null ? null : trainingLevels.trim();
    }

    /**
     * 获取年级(逗号分隔)
     *
     * @return GRADES_ - 年级(逗号分隔)
     */
    public String getGrades() {
        return grades;
    }

    /**
     * 设置年级(逗号分隔)
     *
     * @param grades 年级(逗号分隔)
     */
    public void setGrades(String grades) {
        this.grades = grades == null ? null : grades.trim();
    }

    /**
     * 获取校区(逗号分隔)
     *
     * @return CAMPUS_ - 校区(逗号分隔)
     */
    public String getCampus() {
        return campus;
    }

    /**
     * 设置校区(逗号分隔)
     *
     * @param campus 校区(逗号分隔)
     */
    public void setCampus(String campus) {
        this.campus = campus == null ? null : campus.trim();
    }

    /**
     * 获取学院(逗号分隔)
     *
     * @return FACULTYS_ - 学院(逗号分隔)
     */
    public String getFacultys() {
        return facultys;
    }

    /**
     * 设置学院(逗号分隔)
     *
     * @param facultys 学院(逗号分隔)
     */
    public void setFacultys(String facultys) {
        this.facultys = facultys == null ? null : facultys.trim();
    }

    /**
     * 获取专业(逗号分隔)
     *
     * @return MAJORS_ - 专业(逗号分隔)
     */
    public String getMajors() {
        return majors;
    }

    /**
     * 设置专业(逗号分隔)
     *
     * @param majors 专业(逗号分隔)
     */
    public void setMajors(String majors) {
        this.majors = majors == null ? null : majors.trim();
    }

    /**
     * 获取专项计划(逗号分隔)
     *
     * @return SPCIAL_PLANS_ - 专项计划(逗号分隔)
     */
    public String getSpcialPlans() {
        return spcialPlans;
    }

    /**
     * 设置专项计划(逗号分隔)
     *
     * @param spcialPlans 专项计划(逗号分隔)
     */
    public void setSpcialPlans(String spcialPlans) {
        this.spcialPlans = spcialPlans == null ? null : spcialPlans.trim();
    }
    
    

    /**
             * 获取培养类别(逗号分隔)
     * 
     * @return TRAINING_CATEGORY_ 培养类别(逗号分隔)
     */
    public String getTrainingCategorys() {
		return trainingCategorys;
	}

	/**
	 * 设置培养类别(逗号分隔)
	 * 
	 * @param TRAINING_CATEGORY_ 培养类别(逗号分隔)
	 */
	public void setTrainingCategorys(String trainingCategorys) {
		this.trainingCategorys = trainingCategorys;
	}

	/**
	 * 获取学位类型(逗号分隔)
	 * 
	 * @return DEGREE_TYPE_  学位类型(逗号分隔)
	 */
	public String getDegreeTypes() {
		return degreeTypes;
	}

	/**
	 * 设置学位类型(逗号分隔)
	 * 
	 * @param DEGREE_TYPE_  学位类型(逗号分隔)
	 */
	public void setDegreeTypes(String degreeTypes) {
		this.degreeTypes = degreeTypes;
	}

	/**
	  * 获取学习形式(逗号分隔)
	 * 
	 * @return FORM_LEARNING_  学习形式(逗号分隔)
	 */
	public String getFormLearings() {
		return formLearings;
	}

	/**
	 * 设置学习形式(逗号分隔)
	 * 
	 * @param FORM_LEARNING_  学习形式(逗号分隔)
	 */
	public void setFormLearings(String formLearings) {
		this.formLearings = formLearings;
	}

	@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", roundId=").append(roundId);
        sb.append(", trainingLevels=").append(trainingLevels);
        sb.append(", grades=").append(grades);
        sb.append(", campus=").append(campus);
        sb.append(", facultys=").append(facultys);
        sb.append(", majors=").append(majors);
        sb.append(", spcialPlans=").append(spcialPlans);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}