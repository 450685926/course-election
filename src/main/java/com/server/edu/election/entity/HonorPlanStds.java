package com.server.edu.election.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Table(name = "honor_plan_stds_t")
public class HonorPlanStds implements Serializable {

    /**
     * 主键
     */
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "STUDENT_ID_")
    private String studentId;


    @Column(name = "CALENDAR_ID_")
    private Long calendarId;


    @Column(name = "HONOR_PLAN_NAME_")
    private String honorPlanName;


    @Column(name = "DIRECTION_NAME_")
    private String directionName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public Long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }

    public String getHonorPlanName() {
        return honorPlanName;
    }

    public void setHonorPlanName(String honorPlanName) {
        this.honorPlanName = honorPlanName;
    }

    public String getDirectionName() {
        return directionName;
    }

    public void setDirectionName(String directionName) {
        this.directionName = directionName;
    }

    @Override
    public String toString() {
        return "HonorPlanStds{" +
                "studentId=" + studentId +
                ", calendarId='" + calendarId + '\'' +
                ", honorPlanName=" + honorPlanName +
                ", directionName=" + directionName +
                '}';
    }
}