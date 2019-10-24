package com.server.edu.exam.vo;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.exam.entity.GraduateExamAuth;

import java.util.List;

/**
 * @description:
 * @author: bear
 * @create: 2019-08-26 15:25
 */
@CodeI18n
public class GraduateExamAuthVo extends GraduateExamAuth {
    @Code2Text(transformer = "X_PYCC",dataType= Code2Text.DataType.LIST )
    private List<String> trainingLevels;
    @Code2Text(transformer = "X_KCXZ",dataType= Code2Text.DataType.LIST )
    private List<String> courseNatures;
    @Code2Text(transformer = "X_YX",dataType= Code2Text.DataType.LIST )
    private List<String> openColleges;
    @Code2Text(transformer = "X_PYLB",dataType= Code2Text.DataType.LIST)
    private List<String> trainingCategorys;
    @Code2Text(transformer = "X_XWLX",dataType= Code2Text.DataType.LIST)
    private List<String> degreeTypes;
    @Code2Text(transformer = "X_XXXS",dataType= Code2Text.DataType.LIST)
    private List<String> formLearnings;

    public List<String> getTrainingCategorys() {
        return trainingCategorys;
    }

    public void setTrainingCategorys(List<String> trainingCategorys) {
        this.trainingCategorys = trainingCategorys;
    }

    public List<String> getDegreeTypes() {
        return degreeTypes;
    }

    public void setDegreeTypes(List<String> degreeTypes) {
        this.degreeTypes = degreeTypes;
    }

    public List<String> getFormLearnings() {
        return formLearnings;
    }

    public void setFormLearnings(List<String> formLearnings) {
        this.formLearnings = formLearnings;
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

    public List<String> getOpenColleges() {
        return openColleges;
    }

    public void setOpenColleges(List<String> openColleges) {
        this.openColleges = openColleges;
    }
}
