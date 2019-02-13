package com.server.edu.election.entity;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "student_t")
public class Student implements Serializable {
    /**
     * 学生ID
     */
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 学生代码
     */
    @Column(name = "STUDENT_CODE_")
    private String studentCode;

    /**
     * 姓名
     */
    @Column(name = "NAME_")
    private String name;

    /**
     * 性别(根据数据字典统一使用)
     */
    @Column(name = "SEX_")
    private Integer sex;

    /**
     * 学习形式(全日制,非全日制,其他)
     */
    @Column(name = "FORM_LEARNING_")
    private String formLearning;

    /**
     * 是否留学生 0：否  1：是
     */
    @Column(name = "IS_OVERSEAS_")
    private String isOverseas;

    /**
     * 培养层次(专科   本科   硕士   博士    其他    预科)
     */
    @Column(name = "TRAINING_LEVEL_")
    private String trainingLevel;

    /**
     * 学院
     */
    @Column(name = "FACULTY_")
    private String faculty;

    /**
     * 专业
     */
    @Column(name = "PROFESSION_")
    private String profession;

    /**
     * 年级
     */
    @Column(name = "GRADE_")
    private Integer grade;

    /**
     * 专项计划
     */
    @Column(name = "SPCIAL_PLAN_")
    private String spcialPlan;

    /**
     * 学籍状态
     */
    @Column(name = "REGISTRATION_STATUS_")
    private String registrationStatus;

    /**
     * 入学季节
     */
    @Column(name = "ENROL_SEASON_")
    private String enrolSeason;

    /**
     * 校区
     */
    @Column(name = "CAMPUS_")
    private String campus;

    /**
     * 培养类别
     */
    @Column(name = "TRAINING_CATEGORY_")
    private String trainingCategory;

    /**
     * 学位类别
     */
    @Column(name = "DEGREE_CATEGORY_")
    private String degreeCategory;

    private static final long serialVersionUID = 1L;

    /**
     * 获取学生ID
     *
     * @return ID_ - 学生ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置学生ID
     *
     * @param id 学生ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取学生代码
     *
     * @return STUDENT_CODE_ - 学生代码
     */
    public String getStudentCode() {
        return studentCode;
    }

    /**
     * 设置学生代码
     *
     * @param studentCode 学生代码
     */
    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode == null ? null : studentCode.trim();
    }

    /**
     * 获取姓名
     *
     * @return NAME_ - 姓名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置姓名
     *
     * @param name 姓名
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * 获取性别(根据数据字典统一使用)
     *
     * @return SEX_ - 性别(根据数据字典统一使用)
     */
    public Integer getSex() {
        return sex;
    }

    /**
     * 设置性别(根据数据字典统一使用)
     *
     * @param sex 性别(根据数据字典统一使用)
     */
    public void setSex(Integer sex) {
        this.sex = sex;
    }

    /**
     * 获取学习形式(全日制,非全日制,其他)
     *
     * @return FORM_LEARNING_ - 学习形式(全日制,非全日制,其他)
     */
    public String getFormLearning() {
        return formLearning;
    }

    /**
     * 设置学习形式(全日制,非全日制,其他)
     *
     * @param formLearning 学习形式(全日制,非全日制,其他)
     */
    public void setFormLearning(String formLearning) {
        this.formLearning = formLearning == null ? null : formLearning.trim();
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
     * 获取培养层次(专科   本科   硕士   博士    其他    预科)
     *
     * @return TRAINING_LEVEL_ - 培养层次(专科   本科   硕士   博士    其他    预科)
     */
    public String getTrainingLevel() {
        return trainingLevel;
    }

    /**
     * 设置培养层次(专科   本科   硕士   博士    其他    预科)
     *
     * @param trainingLevel 培养层次(专科   本科   硕士   博士    其他    预科)
     */
    public void setTrainingLevel(String trainingLevel) {
        this.trainingLevel = trainingLevel == null ? null : trainingLevel.trim();
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
     * 获取专业
     *
     * @return PROFESSION_ - 专业
     */
    public String getProfession() {
        return profession;
    }

    /**
     * 设置专业
     *
     * @param profession 专业
     */
    public void setProfession(String profession) {
        this.profession = profession == null ? null : profession.trim();
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
     * 获取学籍状态
     *
     * @return REGISTRATION_STATUS_ - 学籍状态
     */
    public String getRegistrationStatus() {
        return registrationStatus;
    }

    /**
     * 设置学籍状态
     *
     * @param registrationStatus 学籍状态
     */
    public void setRegistrationStatus(String registrationStatus) {
        this.registrationStatus = registrationStatus == null ? null : registrationStatus.trim();
    }

    /**
     * 获取入学季节
     *
     * @return ENROL_SEASON_ - 入学季节
     */
    public String getEnrolSeason() {
        return enrolSeason;
    }

    /**
     * 设置入学季节
     *
     * @param enrolSeason 入学季节
     */
    public void setEnrolSeason(String enrolSeason) {
        this.enrolSeason = enrolSeason == null ? null : enrolSeason.trim();
    }

    /**
     * 获取校区
     *
     * @return CAMPUS_ - 校区
     */
    public String getCampus() {
        return campus;
    }

    /**
     * 设置校区
     *
     * @param campus 校区
     */
    public void setCampus(String campus) {
        this.campus = campus == null ? null : campus.trim();
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", studentCode=").append(studentCode);
        sb.append(", name=").append(name);
        sb.append(", sex=").append(sex);
        sb.append(", formLearning=").append(formLearning);
        sb.append(", isOverseas=").append(isOverseas);
        sb.append(", trainingLevel=").append(trainingLevel);
        sb.append(", faculty=").append(faculty);
        sb.append(", profession=").append(profession);
        sb.append(", grade=").append(grade);
        sb.append(", spcialPlan=").append(spcialPlan);
        sb.append(", registrationStatus=").append(registrationStatus);
        sb.append(", enrolSeason=").append(enrolSeason);
        sb.append(", campus=").append(campus);
        sb.append(", trainingCategory=").append(trainingCategory);
        sb.append(", degreeCategory=").append(degreeCategory);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}