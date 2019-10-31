package com.server.edu.exam.vo;

import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.exam.entity.GraduateExamInfo;

import java.util.List;

/**
 * @description:
 * @author: bear
 * @create: 2019-09-02 14:55
 */
@CodeI18n
public class GraduateExamInfoVo extends GraduateExamInfo {
    private String courseName;
    @Code2Text(DictTypeEnum.X_YX)
    private String faculty;
    @Code2Text(DictTypeEnum.X_KCXZ)
    private String nature;
    @Code2Text(DictTypeEnum.X_PYCC)
    private String trainingLevel;
    private Double credits;
    private List<String> facultys;
    private List<String> trainingLevels;
    private List<String> courseNatures;
    private String keyword;
    @Code2Text(DictTypeEnum.X_XXXS)
    private String formLearning;

    /**合考添加课程使用*/
    private String addCourse;

    private String teachingClassName;

    private Integer mode;

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public String getTeachingClassName() {
        return teachingClassName;
    }

    public void setTeachingClassName(String teachingClassName) {
        this.teachingClassName = teachingClassName;
    }

    public String getAddCourse() {
        return addCourse;
    }

    public void setAddCourse(String addCourse) {
        this.addCourse = addCourse;
    }

    public String getFormLearning() {
        return formLearning;
    }

    public void setFormLearning(String formLearning) {
        this.formLearning = formLearning;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<String> getTrainingLevels() {
        return trainingLevels;
    }

    public void setTrainingLevels(List<String> trainingLevels) {
        this.trainingLevels = trainingLevels;
    }

    public List<String> getCourseNatures() {
        return courseNatures;
    }

    public void setCourseNatures(List<String> courseNatures) {
        this.courseNatures = courseNatures;
    }

    public List<String> getFacultys() {
        return facultys;
    }

    public void setFacultys(List<String> facultys) {
        this.facultys = facultys;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public String getTrainingLevel() {
        return trainingLevel;
    }

    public void setTrainingLevel(String trainingLevel) {
        this.trainingLevel = trainingLevel;
    }

    public Double getCredits() {
        return credits;
    }

    public void setCredits(Double credits) {
        this.credits = credits;
    }
}
