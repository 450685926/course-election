package com.server.edu.election.vo;

import java.util.List;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.election.entity.ElcLoserStds;

/**
 * @description:
 * @author: bear
 * @create: 2019-05-09 16:59
 */

@CodeI18n
public class ElcLoserStdsVo extends ElcLoserStds {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String calendarName;
    private String studentName;
    private Integer grade;
    @Code2Text(transformer = "X_PYCC")
    private String trainingLevel;
    private String studentCategory;
    @Code2Text(transformer = "X_YX")
    private String faculty;
    @Code2Text(transformer = "G_ZY")
    private String profession;
    private String researchDirection;
    private List<Long> idslist;
    private String deptId;
    private String keyword;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public List<Long> getIdslist() {
        return idslist;
    }

    public void setIdslist(List<Long> idslist) {
        this.idslist = idslist;
    }

    public String getCalendarName() {
        return calendarName;
    }

    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }


    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getTrainingLevel() {
        return trainingLevel;
    }

    public void setTrainingLevel(String trainingLevel) {
        this.trainingLevel = trainingLevel;
    }

    public String getStudentCategory() {
        return studentCategory;
    }

    public void setStudentCategory(String studentCategory) {
        this.studentCategory = studentCategory;
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

    public String getResearchDirection() {
        return researchDirection;
    }

    public void setResearchDirection(String researchDirection) {
        this.researchDirection = researchDirection;
    }
}
