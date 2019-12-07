package com.server.edu.election.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.election.entity.ElcCourseTake;

import java.util.List;

@CodeI18n
public class HonorPlanStdsVo
{
    /**
     * 主键
     */
    private Long id;

    /**
     * 学号
     */
    private String studentId;

    /**
     * 学年学期
     */
    private Long calendarId;

    /**
     * 荣誉计划名称
     */
    private String honorPlanName;

    /**
     * 课程方向名称
     */
    private String directionName;

    /**
     *姓名
     */
    private String studentName;

    /**
     * 培养层次
     */
    @Code2Text(DictTypeEnum.X_PYCC)
    private String trainingLevel;

    /**
     * 年级
     */
    private String grade;

    /**
     * 学院
     */
    @Code2Text(DictTypeEnum.X_YX)
    private String faculty;

    /**
     * 专业
     */
    @Code2Text(DictTypeEnum.G_ZY)
    private String profession;

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

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getTrainingLevel() {
        return trainingLevel;
    }

    public void setTrainingLevel(String trainingLevel) {
        this.trainingLevel = trainingLevel;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
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
}
