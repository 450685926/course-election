package com.server.edu.election.entity;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;

import java.io.Serializable;
import java.util.Objects;

/**
 * @description: 重修不收费类型
 * @author: bear
 * @create: 2019-02-01 11:37
 */
@CodeI18n
public class RebuildCourseNoChargeType implements Serializable {
    private Long id;

    @Code2Text(transformer = "X_PYCC")
    private String trainingLevel;

    @Code2Text(transformer = "X_XXXS")
    private String formLearning;

    @Code2Text(transformer = "X_ZXJH")
    private String spcialPlan;

    @Code2Text(transformer = "G_XJZT")
    private String registrationStatus;

    private Integer certificateType;

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

    public String getSpcialPlan() {
        return spcialPlan;
    }

    public void setSpcialPlan(String spcialPlan) {
        this.spcialPlan = spcialPlan == null ? null : spcialPlan.trim();
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(String registrationStatus) {
        this.registrationStatus = registrationStatus == null ? null : registrationStatus.trim();
    }

    public Integer getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(Integer certificateType) {
        this.certificateType = certificateType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RebuildCourseNoChargeType that = (RebuildCourseNoChargeType) o;
        return Objects.equals(trainingLevel, that.trainingLevel) &&
                Objects.equals(formLearning, that.formLearning) &&
                Objects.equals(spcialPlan, that.spcialPlan) &&
                Objects.equals(registrationStatus, that.registrationStatus) &&
                Objects.equals(certificateType, that.certificateType);
    }

    @Override
    public int hashCode() {

        return Objects.hash(trainingLevel, formLearning, spcialPlan, registrationStatus, certificateType);
    }
}
