package com.server.edu.election.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "teaching_class_elective_restrict_attr_t")
public class TeachingClassElectiveRestrictAttr implements Serializable {
    /**
     * 主键（自增）
     */
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 教学班ID
     */
    @Column(name = "TEACHING_CLASS_ID_")
    private Long teachingClassId;

    /**
     * 年级(默认0，代表研究生专业)
     */
    @Column(name = "GRADE_")
    private String grade;

    /**
     * 培养层次X_PYCC(专科   本科   硕士   博士    其他    预科)
     */
    @Column(name = "TRAINING_LEVEL_")
    private String trainingLevel;

    /**
     * 学习形式X_XXXS（未使用，目前废弃）
     */
    @Column(name = "FORM_LEARNING_")
    private String formLearning;

    /**
     * 学位类型
     */
    @Column(name = "DEGREE_TYPE_")
    private String degreeType;

    /**
     * 学位类别
     */
    @Column(name = "DEGREE_CATEGORY_")
    private String degreeCategory;

    /**
     * 培养类别
     */
    @Column(name = "TRAINING_CATEGORY_")
    private String trainingCategory;

    /**
     * 专项计划
     */
    @Column(name = "SPCIAL_PLAN_")
    private String spcialPlan;

    /**
     * 是否留学生 0：否  1：是
     */
    @Column(name = "IS_OVERSEAS_")
    private String isOverseas;

    /**
     * 是否男女生班 0：不区分  1：男生班 2：女生班
     */
    @Column(name = "IS_DIVSEX_")
    private String isDivsex;

    /**
     * 男生人数
     */
    @Column(name = "NUMBER_MALE_")
    private Integer numberMale;

    /**
     * 女生人数
     */
    @Column(name = "NUMBER_FEMALE_")
    private Integer numberFemale;

    /**
     * 备注
     */
    @Column(name = "REMARK_")
    private String remark;

    /**
     * 创建时间
     */
    @Column(name = "CREATED_AT_")
    private Date createdAt;

    /**
     * 修改时间
     */
    @Column(name = "UPDATED_AT_")
    private Date updatedAt;

    /**
     * 开课学院(开课课程属性冗余)
     */
    @Column(name = "FACULTY_")
    private String faculty;

    /**
     * 专业（编码）
     */
    @Column(name = "PROFESSION_")
    private String profession;

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
     * 获取教学班ID
     *
     * @return TEACHING_CLASS_ID_ - 教学班ID
     */
    public Long getTeachingClassId() {
        return teachingClassId;
    }

    /**
     * 设置教学班ID
     *
     * @param teachingClassId 教学班ID
     */
    public void setTeachingClassId(Long teachingClassId) {
        this.teachingClassId = teachingClassId;
    }

    /**
     * 获取年级(默认0，代表研究生专业)
     *
     * @return GRADE_ - 年级(默认0，代表研究生专业)
     */
    public String getGrade() {
        return grade;
    }

    /**
     * 设置年级(默认0，代表研究生专业)
     *
     * @param grade 年级(默认0，代表研究生专业)
     */
    public void setGrade(String grade) {
        this.grade = grade;
    }

    /**
     * 获取培养层次X_PYCC(专科   本科   硕士   博士    其他    预科)
     *
     * @return TRAINING_LEVEL_ - 培养层次X_PYCC(专科   本科   硕士   博士    其他    预科)
     */
    public String getTrainingLevel() {
        return trainingLevel;
    }

    /**
     * 设置培养层次X_PYCC(专科   本科   硕士   博士    其他    预科)
     *
     * @param trainingLevel 培养层次X_PYCC(专科   本科   硕士   博士    其他    预科)
     */
    public void setTrainingLevel(String trainingLevel) {
        this.trainingLevel = trainingLevel == null ? null : trainingLevel.trim();
    }

    /**
     * 获取学习形式X_XXXS（未使用，目前废弃）
     *
     * @return FORM_LEARNING_ - 学习形式X_XXXS（未使用，目前废弃）
     */
    public String getFormLearning() {
        return formLearning;
    }

    /**
     * 设置学习形式X_XXXS（未使用，目前废弃）
     *
     * @param formLearning 学习形式X_XXXS（未使用，目前废弃）
     */
    public void setFormLearning(String formLearning) {
        this.formLearning = formLearning == null ? null : formLearning.trim();
    }

    /**
     * 获取学位类型
     *
     * @return DEGREE_TYPE_ - 学位类型
     */
    public String getDegreeType() {
        return degreeType;
    }

    /**
     * 设置学位类型
     *
     * @param degreeType 学位类型
     */
    public void setDegreeType(String degreeType) {
        this.degreeType = degreeType == null ? null : degreeType.trim();
    }

    /**
     * 获取学位类别
     *
     * @return DEGREE_CATEGORY_ - 学位类别
     */
    public String getDegreeCategory() {
        return degreeCategory;
    }

    /**
     * 设置学位类别
     *
     * @param degreeCategory 学位类别
     */
    public void setDegreeCategory(String degreeCategory) {
        this.degreeCategory = degreeCategory == null ? null : degreeCategory.trim();
    }

    /**
     * 获取培养类别
     *
     * @return TRAINING_CATEGORY_ - 培养类别
     */
    public String getTrainingCategory() {
        return trainingCategory;
    }

    /**
     * 设置培养类别
     *
     * @param trainingCategory 培养类别
     */
    public void setTrainingCategory(String trainingCategory) {
        this.trainingCategory = trainingCategory == null ? null : trainingCategory.trim();
    }

    /**
     * 获取专项计划
     *
     * @return SPCIAL_PLAN_ - 专项计划
     */
    public String getSpcialPlan() {
        return spcialPlan;
    }

    /**
     * 设置专项计划
     *
     * @param spcialPlan 专项计划
     */
    public void setSpcialPlan(String spcialPlan) {
        this.spcialPlan = spcialPlan == null ? null : spcialPlan.trim();
    }

    /**
     * 获取是否留学生 0：否  1：是
     *
     * @return IS_OVERSEAS_ - 是否留学生 0：否  1：是
     */
    public String getIsOverseas() {
        return isOverseas;
    }

    /**
     * 设置是否留学生 0：否  1：是
     *
     * @param isOverseas 是否留学生 0：否  1：是
     */
    public void setIsOverseas(String isOverseas) {
        this.isOverseas = isOverseas == null ? null : isOverseas.trim();
    }

    /**
     * 获取是否男女生班 0：不区分  1：男生班 2：女生班
     *
     * @return IS_DIVSEX_ - 是否男女生班 0：不区分  1：男生班 2：女生班
     */
    public String getIsDivsex() {
        return isDivsex;
    }

    /**
     * 设置是否男女生班 0：不区分  1：男生班 2：女生班
     *
     * @param isDivsex 是否男女生班 0：不区分  1：男生班 2：女生班
     */
    public void setIsDivsex(String isDivsex) {
        this.isDivsex = isDivsex == null ? null : isDivsex.trim();
    }

    /**
     * 获取男生人数
     *
     * @return NUMBER_MALE_ - 男生人数
     */
    public Integer getNumberMale() {
        return numberMale;
    }

    /**
     * 设置男生人数
     *
     * @param numberMale 男生人数
     */
    public void setNumberMale(Integer numberMale) {
        this.numberMale = numberMale;
    }

    /**
     * 获取女生人数
     *
     * @return NUMBER_FEMALE_ - 女生人数
     */
    public Integer getNumberFemale() {
        return numberFemale;
    }

    /**
     * 设置女生人数
     *
     * @param numberFemale 女生人数
     */
    public void setNumberFemale(Integer numberFemale) {
        this.numberFemale = numberFemale;
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

    /**
     * 获取创建时间
     *
     * @return CREATED_AT_ - 创建时间
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * 设置创建时间
     *
     * @param createdAt 创建时间
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 获取修改时间
     *
     * @return UPDATED_AT_ - 修改时间
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 设置修改时间
     *
     * @param updatedAt 修改时间
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * 获取开课学院(开课课程属性冗余)
     *
     * @return FACULTY_ - 开课学院(开课课程属性冗余)
     */
    public String getFaculty() {
        return faculty;
    }

    /**
     * 设置开课学院(开课课程属性冗余)
     *
     * @param faculty 开课学院(开课课程属性冗余)
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", teachingClassId=").append(teachingClassId);
        sb.append(", grade=").append(grade);
        sb.append(", trainingLevel=").append(trainingLevel);
        sb.append(", formLearning=").append(formLearning);
        sb.append(", degreeType=").append(degreeType);
        sb.append(", degreeCategory=").append(degreeCategory);
        sb.append(", trainingCategory=").append(trainingCategory);
        sb.append(", spcialPlan=").append(spcialPlan);
        sb.append(", isOverseas=").append(isOverseas);
        sb.append(", isDivsex=").append(isDivsex);
        sb.append(", numberMale=").append(numberMale);
        sb.append(", numberFemale=").append(numberFemale);
        sb.append(", remark=").append(remark);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", faculty=").append(faculty);
        sb.append(", profession=").append(profession);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}