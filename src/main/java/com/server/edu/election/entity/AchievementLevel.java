package com.server.edu.election.entity;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "achievement_level_t")
public class AchievementLevel implements Serializable {
    /**
     * 主键（自增）
     */
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 方案名称
     */
    @Column(name = "NAME_")
    private Long name;

    /**
     * 年级
     */
    @Column(name = "GRADE_")
    private Integer grade;

    /**
     * 培养层次X_PYCC
     */
    @Column(name = "TRAINING_LEVEL_")
    private String trainingLevel;

    /**
     * 学习形式X_XXXS
     */
    @Column(name = "FORM_LEARNING_")
    private String formLearning;

    /**
     * 学院
     */
    @Column(name = "FACULTY_")
    private String faculty;

    /**
     * 专业（编码）
     */
    @Column(name = "PROFESSION_")
    private String profession;

    /**
     * 科目
     */
    @Column(name = "SUBJECT_")
    private String subject;

    /**
     * 起始分数
     */
    @Column(name = "MIN_SCORE_")
    private String minScore;

    /**
     * 结束分数
     */
    @Column(name = "MAX_SCORE_")
    private String maxScore;

    /**
     * 等级
     */
    @Column(name = "LEVEL_")
    private String level;

    /**
     * 描述
     */
    @Column(name = "REMARK_")
    private String remark;

    private static final long serialVersionUID = 1L;

    /**
     * 获取主键（自增）
     *
     * @return ID_ - 主键（自增）
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置主键（自增）
     *
     * @param id 主键（自增）
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取方案名称
     *
     * @return NAME_ - 方案名称
     */
    public Long getName() {
        return name;
    }

    /**
     * 设置方案名称
     *
     * @param name 方案名称
     */
    public void setName(Long name) {
        this.name = name;
    }

    /**
     * 获取年级
     *
     * @return GRADE_ - 年级
     */
    public Integer getGrade() {
        return grade;
    }

    /**
     * 设置年级
     *
     * @param grade 年级
     */
    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    /**
     * 获取培养层次X_PYCC
     *
     * @return TRAINING_LEVEL_ - 培养层次X_PYCC
     */
    public String getTrainingLevel() {
        return trainingLevel;
    }

    /**
     * 设置培养层次X_PYCC
     *
     * @param trainingLevel 培养层次X_PYCC
     */
    public void setTrainingLevel(String trainingLevel) {
        this.trainingLevel = trainingLevel == null ? null : trainingLevel.trim();
    }

    /**
     * 获取学习形式X_XXXS
     *
     * @return FORM_LEARNING_ - 学习形式X_XXXS
     */
    public String getFormLearning() {
        return formLearning;
    }

    /**
     * 设置学习形式X_XXXS
     *
     * @param formLearning 学习形式X_XXXS
     */
    public void setFormLearning(String formLearning) {
        this.formLearning = formLearning == null ? null : formLearning.trim();
    }

    /**
     * 获取学院
     *
     * @return FACULTY_ - 学院
     */
    public String getFaculty() {
        return faculty;
    }

    /**
     * 设置学院
     *
     * @param faculty 学院
     */
    public void setFaculty(String faculty) {
        this.faculty = faculty == null ? null : faculty.trim();
    }

    /**
     * 获取专业（编码）
     *
     * @return PROFESSION_ - 专业（编码）
     */
    public String getProfession() {
        return profession;
    }

    /**
     * 设置专业（编码）
     *
     * @param profession 专业（编码）
     */
    public void setProfession(String profession) {
        this.profession = profession == null ? null : profession.trim();
    }

    /**
     * 获取科目
     *
     * @return SUBJECT_ - 科目
     */
    public String getSubject() {
        return subject;
    }

    /**
     * 设置科目
     *
     * @param subject 科目
     */
    public void setSubject(String subject) {
        this.subject = subject == null ? null : subject.trim();
    }

    /**
     * 获取起始分数
     *
     * @return MIN_SCORE_ - 起始分数
     */
    public String getMinScore() {
        return minScore;
    }

    /**
     * 设置起始分数
     *
     * @param minScore 起始分数
     */
    public void setMinScore(String minScore) {
        this.minScore = minScore == null ? null : minScore.trim();
    }

    /**
     * 获取结束分数
     *
     * @return MAX_SCORE_ - 结束分数
     */
    public String getMaxScore() {
        return maxScore;
    }

    /**
     * 设置结束分数
     *
     * @param maxScore 结束分数
     */
    public void setMaxScore(String maxScore) {
        this.maxScore = maxScore == null ? null : maxScore.trim();
    }

    /**
     * 获取等级
     *
     * @return LEVEL_ - 等级
     */
    public String getLevel() {
        return level;
    }

    /**
     * 设置等级
     *
     * @param level 等级
     */
    public void setLevel(String level) {
        this.level = level == null ? null : level.trim();
    }

    /**
     * 获取描述
     *
     * @return REMARK_ - 描述
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置描述
     *
     * @param remark 描述
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", grade=").append(grade);
        sb.append(", trainingLevel=").append(trainingLevel);
        sb.append(", formLearning=").append(formLearning);
        sb.append(", faculty=").append(faculty);
        sb.append(", profession=").append(profession);
        sb.append(", subject=").append(subject);
        sb.append(", minScore=").append(minScore);
        sb.append(", maxScore=").append(maxScore);
        sb.append(", level=").append(level);
        sb.append(", remark=").append(remark);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}