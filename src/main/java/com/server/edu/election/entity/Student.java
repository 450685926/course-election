package com.server.edu.election.entity;

import java.io.Serializable;

/**
 * @description: 学生信息
 * @author: bear
 * @create: 2019-02-12 14:59
 */
public class Student implements Serializable {
    private Long id;

    private String studentCode;

    private String name;

    private Integer sex;

    private String formLearning;

    private String isOverseas;

    private String trainingLevel;

    private String faculty;

    private String profession;

    private Integer grade;

    private String spcialPlan;

    private String registrationStatus;

    private String enrolSeason;

    private String campus;

    private String trainingCategory;

    private String degreeCategory;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode == null ? null : studentCode.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getFormLearning() {
        return formLearning;
    }

    public void setFormLearning(String formLearning) {
        this.formLearning = formLearning == null ? null : formLearning.trim();
    }

    public String getIsOverseas() {
        return isOverseas;
    }

    public void setIsOverseas(String isOverseas) {
        this.isOverseas = isOverseas == null ? null : isOverseas.trim();
    }

    public String getTrainingLevel() {
        return trainingLevel;
    }

    public void setTrainingLevel(String trainingLevel) {
        this.trainingLevel = trainingLevel == null ? null : trainingLevel.trim();
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty == null ? null : faculty.trim();
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession == null ? null : profession.trim();
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getSpcialPlan() {
        return spcialPlan;
    }

    public void setSpcialPlan(String spcialPlan) {
        this.spcialPlan = spcialPlan == null ? null : spcialPlan.trim();
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(String registrationStatus) {
        this.registrationStatus = registrationStatus == null ? null : registrationStatus.trim();
    }

    public String getEnrolSeason() {
        return enrolSeason;
    }

    public void setEnrolSeason(String enrolSeason) {
        this.enrolSeason = enrolSeason == null ? null : enrolSeason.trim();
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus == null ? null : campus.trim();
    }

    public String getTrainingCategory() {
        return trainingCategory;
    }

    public void setTrainingCategory(String trainingCategory) {
        this.trainingCategory = trainingCategory == null ? null : trainingCategory.trim();
    }

    public String getDegreeCategory() {
        return degreeCategory;
    }

    public void setDegreeCategory(String degreeCategory) {
        this.degreeCategory = degreeCategory == null ? null : degreeCategory.trim();
    }
}
