package com.server.edu.election.entity;

import java.io.Serializable;

/**
 * @description: 未缴费课程名单
 * @author: bear
 * @create: 2019-02-13 15:06
 */

public class RebuildCourseNoChargeList implements Serializable{
    private Long id;
    private String studentCode;
    private String studentName;
    private String code;
    private String codeName;
    private String nature;
    private Double credits;
    private Integer paid;
    private Double weekHour;
    private Integer endWeek;
    private Integer startWeek;

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
        this.studentCode = studentCode;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public Double getCredits() {
        return credits;
    }

    public void setCredits(Double credits) {
        this.credits = credits;
    }

    public Integer getPaid() {
        return paid;
    }

    public void setPaid(Integer paid) {
        this.paid = paid;
    }

    public Double getWeekHour() {
        return weekHour;
    }

    public void setWeekHour(Double weekHour) {
        this.weekHour = weekHour;
    }

    public Integer getEndWeek() {
        return endWeek;
    }

    public void setEndWeek(Integer endWeek) {
        this.endWeek = endWeek;
    }

    public Integer getStartWeek() {
        return startWeek;
    }

    public void setStartWeek(Integer startWeek) {
        this.startWeek = startWeek;
    }
}