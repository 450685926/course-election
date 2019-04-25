package com.server.edu.election.vo;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.election.entity.ExemptionCourse;

import javax.persistence.Column;

/**
 * @description:
 * @author: bear
 * @create: 2019-01-31 15:01
 */
@CodeI18n
public class ExemptionCourseVo extends ExemptionCourse {
    private String calendarName;

    private String courseName;

    /**
     * 培养层次X_PYCC(专科,本科,硕士,博士,其他,预科)
     */
    @Code2Text(transformer="X_PYCC")
    private String trainingLevel;



    /**
     * 学习形式X_XXXS
     */
    @Code2Text(transformer = "X_XXXS")
    private String formLearning;


    /**
     * 学院
     */
    @Code2Text(transformer="X_YX")
    private String faculty;

    /**
     * 部门ID
     * */
    private String managerDeptId;

    public String getManagerDeptId() {
        return managerDeptId;
    }

    public void setManagerDeptId(String managerDeptId) {
        this.managerDeptId = managerDeptId;
    }

    public String getCalendarName() {
        return calendarName;
    }

    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
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
}
