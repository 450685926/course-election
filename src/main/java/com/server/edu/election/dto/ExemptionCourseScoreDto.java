package com.server.edu.election.dto;

import java.io.Serializable;

/**
 * @description: 免修免考成绩查询条件
 * @author: bear
 * @create: 2019-01-31 09:49
 */
public class ExemptionCourseScoreDto implements Serializable{
    private  Long calendarId;
    private Integer grade;
    private String  trainingCategory;
    private String trainingLevel;
    private String formLearning;
    private String faculty;
    private String  profession;
    private String campus;
    private String  registrationStatus;
    private String  isOverseas;
    private String enrolSeason;
    private String condition;
    private String calendarName;

    public String getCalendarName() {
        return calendarName;
    }

    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }

    public Long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getTrainingCategory() {
        return trainingCategory;
    }

    public void setTrainingCategory(String trainingCategory) {
        this.trainingCategory = trainingCategory;
    }

    public String getTrainingLevel() {
        return trainingLevel;
    }

    public void setTrainingLevel(String trainingLevel) {
        this.trainingLevel = trainingLevel;
    }

    public String getFormLearning() {
        return formLearning;
    }

    public void setFormLearning(String formLearning) {
        this.formLearning = formLearning;
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

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(String registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public String getIsOverseas() {
        return isOverseas;
    }

    public void setIsOverseas(String isOverseas) {
        this.isOverseas = isOverseas;
    }

    public String getEnrolSeason() {
        return enrolSeason;
    }

    public void setEnrolSeason(String enrolSeason) {
        this.enrolSeason = enrolSeason;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
