package com.server.edu.exam.query;

import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.dictionary.translator.SchoolCalendarTranslator;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 研究生排考学生查询
 * @author: bear
 * @create: 2019-09-09 11:21
 */
@CodeI18n
public class StudentQuery implements Serializable{
    @Code2Text(translator = SchoolCalendarTranslator.class)
    private Long calendarId;
    private String courseCode;
    private String campus;
    @Code2Text(DictTypeEnum.X_YX)
    private String faculty;
    @Code2Text(DictTypeEnum.G_ZY)
    private String major;
    @Code2Text(DictTypeEnum.X_PYCC)
    private String trainingLevel;
    @Code2Text(DictTypeEnum.X_PYLB)
    private String trainingCategory;
    @Code2Text(DictTypeEnum.X_XWLX)
    private String degreeType;
    @Code2Text(DictTypeEnum.X_XXXS)
    private String formLearning;
    private String isOverseas;
    private String keyword;
    private Integer examType;
    private List<Long> examInfoIds;
    private List<Long> examRoomIds;
    private Integer mode;
    /** 0 未排考学生 1 已排考学生*/
    private Integer examStatus;

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public Integer getExamStatus() {
        return examStatus;
    }

    public void setExamStatus(Integer examStatus) {
        this.examStatus = examStatus;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public List<Long> getExamRoomIds() {
        return examRoomIds;
    }

    public void setExamRoomIds(List<Long> examRoomIds) {
        this.examRoomIds = examRoomIds;
    }

    public List<Long> getExamInfoIds() {
        return examInfoIds;
    }

    public void setExamInfoIds(List<Long> examInfoIds) {
        this.examInfoIds = examInfoIds;
    }

    public Integer getExamType() {
        return examType;
    }

    public void setExamType(Integer examType) {
        this.examType = examType;
    }


    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }


    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getTrainingLevel() {
        return trainingLevel;
    }

    public void setTrainingLevel(String trainingLevel) {
        this.trainingLevel = trainingLevel;
    }

    public String getTrainingCategory() {
        return trainingCategory;
    }

    public void setTrainingCategory(String trainingCategory) {
        this.trainingCategory = trainingCategory;
    }

    public String getDegreeType() {
        return degreeType;
    }

    public void setDegreeType(String degreeType) {
        this.degreeType = degreeType;
    }

    public String getFormLearning() {
        return formLearning;
    }

    public void setFormLearning(String formLearning) {
        this.formLearning = formLearning;
    }

    public String getIsOverseas() {
        return isOverseas;
    }

    public void setIsOverseas(String isOverseas) {
        this.isOverseas = isOverseas;
    }
}
