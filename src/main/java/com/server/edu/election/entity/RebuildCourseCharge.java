package com.server.edu.election.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * @description: 重修收费管理
 * @author: bear
 * @create: 2019-01-31 19:31
 */
public class RebuildCourseCharge implements Serializable {
    private Long id;

    private String trainingLevel;

    private String formLearning;

    private Integer isCharge;

    private Integer unitPrice;

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
        this.trainingLevel = trainingLevel == null ? null : trainingLevel.trim();
    }

    public String getFormLearning() {
        return formLearning;
    }

    public void setFormLearning(String formLearning) {
        this.formLearning = formLearning == null ? null : formLearning.trim();
    }

    public Integer getIsCharge() {
        return isCharge;
    }

    public void setIsCharge(Integer isCharge) {
        this.isCharge = isCharge;
    }

    public Integer getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Integer unitPrice) {
        this.unitPrice = unitPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RebuildCourseCharge that = (RebuildCourseCharge) o;
        return Objects.equals(trainingLevel, that.trainingLevel) &&
                Objects.equals(formLearning, that.formLearning) &&
                Objects.equals(isCharge, that.isCharge);
    }

    @Override
    public int hashCode() {

        return Objects.hash(trainingLevel, formLearning, isCharge);
    }
}
