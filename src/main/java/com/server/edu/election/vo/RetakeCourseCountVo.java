package com.server.edu.election.vo;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@CodeI18n
public class RetakeCourseCountVo {
    private Long id;

    @NotEmpty
    private List<String> trainingLevel;

    @NotEmpty
    private List<String> trainingCategory;

    @NotEmpty
    private List<String> degreeType;

    @NotEmpty
    private List<String> formLearning;

    @NotNull
    private Integer retakeCount;

    @NotBlank
    private String projectName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> getTrainingLevel() {
        return trainingLevel;
    }

    public void setTrainingLevel(List<String> trainingLevel) {
        this.trainingLevel = trainingLevel;
    }

    public List<String> getTrainingCategory() {
        return trainingCategory;
    }

    public void setTrainingCategory(List<String> trainingCategory) {
        this.trainingCategory = trainingCategory;
    }

    public List<String> getDegreeType() {
        return degreeType;
    }

    public void setDegreeType(List<String> degreeType) {
        this.degreeType = degreeType;
    }

    public List<String> getFormLearning() {
        return formLearning;
    }

    public void setFormLearning(List<String> formLearning) {
        this.formLearning = formLearning;
    }

    public Integer getRetakeCount() {
        return retakeCount;
    }

    public void setRetakeCount(Integer retakeCount) {
        this.retakeCount = retakeCount;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
