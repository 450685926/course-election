package com.server.edu.election.vo;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;

@CodeI18n
public class RetakeCourseCountVo {
    private Long id;

    @Code2Text(transformer = "X_PYCC")
    private String trainingLevel;

    @Code2Text(transformer = "X_PYLB")
    private String trainingCategory;

    @Code2Text(transformer = "X_XWLX")
    private String degreeType;

    @Code2Text(transformer = "G_XXXS")
    private String formLearning;

    private Integer retakeCount;

    @Code2Text(transformer = "X_GLBM")
    private String managerDeptId;

    /**删除状态，0未删除，1删除*/
    private Integer status;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getRetakeCount() {
        return retakeCount;
    }

    public void setRetakeCount(Integer retakeCount) {
        this.retakeCount = retakeCount;
    }

    public String getManagerDeptId() {
        return managerDeptId;
    }

    public void setManagerDeptId(String managerDeptId) {
        this.managerDeptId = managerDeptId;
    }
}
