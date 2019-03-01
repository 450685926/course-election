package com.server.edu.election.entity;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "course_t")
public class Course implements Serializable {
    /**
     * 主键
     */
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 课程代码
     */
    @Column(name = "CODE_")
    private String code;

    /**
     * 学分
     */
    @Column(name = "CREDITS_")
    private Double credits;

    /**
     * 课时
     */
    @Column(name = "PERIOD_")
    private Double period;

    /**
     * 周课时
     */
    @Column(name = "WEEK_HOUR_")
    private Double weekHour;

    /**
     * 名称
     */
    @Column(name = "NAME_")
    private String name;

    /**
     * 英文名称
     */
    @Column(name = "NAME_EN_")
    private String nameEn;

    /**
     * 所属学院
     */
    @Column(name = "COLLEGE_")
    private String college;

    /**
     * 课程性质(字典取值X_KCXZ)
     */
    @Column(name = "NATURE_")
    private String nature;

    /**
     * 课程分类(研究生，字典取值X_KCFL)
     */
    @Column(name = "LABEL_")
    private String label;

    /**
     * 培养层次
     */
    @Column(name = "TRAINING_LEVEL_")
    private String trainingLevel;

    /**
     * 学习形式
     */
    @Column(name = "FORM_LEARNING_")
    private String formLearning;

    /**
     * 考试方式
     */
    @Column(name = "ASSESSMENT_MODE_")
    private String assessmentMode;

    /**
     * 是否跨学期(1是0否)
     */
    @Column(name = "CROSS_TERM_")
    private Integer crossTerm;

    /**
     * 是否为公共选修课(1:是，0：否)
     */
    @Column(name = "IS_ELECTIVE_")
    private Integer isElective;

    /**
     * 管理部门id（字典取值）
     */
    @Column(name = "MANAGER_DEPT_ID_")
    private String managerDeptId;

    /**
     * 备注
     */
    @Column(name = "REMARK_")
    private String remark;

    private static final long serialVersionUID = 1L;

    /**
     * 获取主键
     *
     * @return ID_ - 主键
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取课程代码
     *
     * @return CODE_ - 课程代码
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置课程代码
     *
     * @param code 课程代码
     */
    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    /**
     * 获取学分
     *
     * @return CREDITS_ - 学分
     */
    public Double getCredits() {
        return credits;
    }

    /**
     * 设置学分
     *
     * @param credits 学分
     */
    public void setCredits(Double credits) {
        this.credits = credits;
    }

    /**
     * 获取课时
     *
     * @return PERIOD_ - 课时
     */
    public Double getPeriod() {
        return period;
    }

    /**
     * 设置课时
     *
     * @param period 课时
     */
    public void setPeriod(Double period) {
        this.period = period;
    }

    /**
     * 获取周课时
     *
     * @return WEEK_HOUR_ - 周课时
     */
    public Double getWeekHour() {
        return weekHour;
    }

    /**
     * 设置周课时
     *
     * @param weekHour 周课时
     */
    public void setWeekHour(Double weekHour) {
        this.weekHour = weekHour;
    }

    /**
     * 获取名称
     *
     * @return NAME_ - 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称
     *
     * @param name 名称
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * 获取英文名称
     *
     * @return NAME_EN_ - 英文名称
     */
    public String getNameEn() {
        return nameEn;
    }

    /**
     * 设置英文名称
     *
     * @param nameEn 英文名称
     */
    public void setNameEn(String nameEn) {
        this.nameEn = nameEn == null ? null : nameEn.trim();
    }

    /**
     * 获取所属学院
     *
     * @return COLLEGE_ - 所属学院
     */
    public String getCollege() {
        return college;
    }

    /**
     * 设置所属学院
     *
     * @param college 所属学院
     */
    public void setCollege(String college) {
        this.college = college == null ? null : college.trim();
    }

    /**
     * 获取课程性质(字典取值X_KCXZ)
     *
     * @return NATURE_ - 课程性质(字典取值X_KCXZ)
     */
    public String getNature() {
        return nature;
    }

    /**
     * 设置课程性质(字典取值X_KCXZ)
     *
     * @param nature 课程性质(字典取值X_KCXZ)
     */
    public void setNature(String nature) {
        this.nature = nature == null ? null : nature.trim();
    }

    /**
     * 获取课程分类(研究生，字典取值X_KCFL)
     *
     * @return LABEL_ - 课程分类(研究生，字典取值X_KCFL)
     */
    public String getLabel() {
        return label;
    }

    /**
     * 设置课程分类(研究生，字典取值X_KCFL)
     *
     * @param label 课程分类(研究生，字典取值X_KCFL)
     */
    public void setLabel(String label) {
        this.label = label == null ? null : label.trim();
    }

    /**
     * 获取培养层次
     *
     * @return TRAINING_LEVEL_ - 培养层次
     */
    public String getTrainingLevel() {
        return trainingLevel;
    }

    /**
     * 设置培养层次
     *
     * @param trainingLevel 培养层次
     */
    public void setTrainingLevel(String trainingLevel) {
        this.trainingLevel = trainingLevel == null ? null : trainingLevel.trim();
    }

    /**
     * 获取学习形式
     *
     * @return FORM_LEARNING_ - 学习形式
     */
    public String getFormLearning() {
        return formLearning;
    }

    /**
     * 设置学习形式
     *
     * @param formLearning 学习形式
     */
    public void setFormLearning(String formLearning) {
        this.formLearning = formLearning == null ? null : formLearning.trim();
    }

    /**
     * 获取考试方式
     *
     * @return ASSESSMENT_MODE_ - 考试方式
     */
    public String getAssessmentMode() {
        return assessmentMode;
    }

    /**
     * 设置考试方式
     *
     * @param assessmentMode 考试方式
     */
    public void setAssessmentMode(String assessmentMode) {
        this.assessmentMode = assessmentMode == null ? null : assessmentMode.trim();
    }

    /**
     * 获取是否跨学期(1是0否)
     *
     * @return CROSS_TERM_ - 是否跨学期(1是0否)
     */
    public Integer getCrossTerm() {
        return crossTerm;
    }

    /**
     * 设置是否跨学期(1是0否)
     *
     * @param crossTerm 是否跨学期(1是0否)
     */
    public void setCrossTerm(Integer crossTerm) {
        this.crossTerm = crossTerm;
    }

    /**
     * 获取是否为公共选修课(1:是，0：否)
     *
     * @return IS_ELECTIVE_ - 是否为公共选修课(1:是，0：否)
     */
    public Integer getIsElective() {
        return isElective;
    }

    /**
     * 设置是否为公共选修课(1:是，0：否)
     *
     * @param isElective 是否为公共选修课(1:是，0：否)
     */
    public void setIsElective(Integer isElective) {
        this.isElective = isElective;
    }

    /**
     * 获取管理部门id（字典取值）
     *
     * @return MANAGER_DEPT_ID_ - 管理部门id（字典取值）
     */
    public String getManagerDeptId() {
        return managerDeptId;
    }

    /**
     * 设置管理部门id（字典取值）
     *
     * @param managerDeptId 管理部门id（字典取值）
     */
    public void setManagerDeptId(String managerDeptId) {
        this.managerDeptId = managerDeptId == null ? null : managerDeptId.trim();
    }

    /**
     * 获取备注
     *
     * @return REMARK_ - 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注
     *
     * @param remark 备注
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
        sb.append(", code=").append(code);
        sb.append(", credits=").append(credits);
        sb.append(", period=").append(period);
        sb.append(", weekHour=").append(weekHour);
        sb.append(", name=").append(name);
        sb.append(", nameEn=").append(nameEn);
        sb.append(", college=").append(college);
        sb.append(", nature=").append(nature);
        sb.append(", label=").append(label);
        sb.append(", trainingLevel=").append(trainingLevel);
        sb.append(", formLearning=").append(formLearning);
        sb.append(", assessmentMode=").append(assessmentMode);
        sb.append(", crossTerm=").append(crossTerm);
        sb.append(", isElective=").append(isElective);
        sb.append(", managerDeptId=").append(managerDeptId);
        sb.append(", remark=").append(remark);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}