package com.server.edu.election.vo;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.election.entity.ElcNoGraduateStds;

/**
 * @description: 结业生
 * @author: bear
 * @create: 2019-02-22 09:57
 */

@CodeI18n
public class ElcNoGraduateStdsVo extends ElcNoGraduateStds {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String studentName;
    private Integer grade;
    @Code2Text(transformer = "X_PYCC")
    private String trainingLevel;
    private String studentCategory;
    @Code2Text(transformer="X_YX")
    private String faculty;
    @Code2Text(transformer="G_ZY")
    private String profession;

    /**模式*/
    private Integer mode;

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
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
}
