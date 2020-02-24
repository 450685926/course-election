package com.server.edu.election.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;

@CodeI18n
@Table(name = "rebuild_course_nocharge_type_t")
public class RebuildCourseNoChargeType implements Serializable {
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 培养层次 X_PYCC
     */
    @Code2Text(DictTypeEnum.X_PYCC)
    @Column(name = "TRAINING_LEVEL_")
    private String trainingLevel;

    /**
     * 培养类别
     */
    @Code2Text(DictTypeEnum.X_PYLB)
    @Column(name = "TRAINING_CATEGORY_")
    private String trainingCategory;

    /**
     * 入学方式
     */
    @Code2Text(DictTypeEnum.X_RXFS)
    @Column(name = "ENROL_METHODS_")
    private String enrolMethods;

    /**
     * 专项计划
     */
    @Code2Text(DictTypeEnum.X_ZXJH)
    @Column(name = "SPCIAL_PLAN_")
    private String spcialPlan;



    /**
     * 是否国际学生
     */
    @Column(name = "IS_OVERSEAS_")
    private String isOverseas;

    /**
     * 学籍异动状态
     */
    @Code2Text(transformer = "G_XJDL")
    @Column(name = "REGISTRATION_STATUS_")
    private String registrationStatus;

    private static final long serialVersionUID = 1L;

    /**
     * @return ID_
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取培养层次 X_PYCC
     *
     * @return TRAINING_LEVEL_ - 培养层次 X_PYCC
     */
    public String getTrainingLevel() {
        return trainingLevel;
    }

    /**
     * 设置培养层次 X_PYCC
     *
     * @param trainingLevel 培养层次 X_PYCC
     */
    public void setTrainingLevel(String trainingLevel) {
        this.trainingLevel = trainingLevel == null ? null : trainingLevel.trim();
    }


    /**
     * 获取专项计划
     *
     * @return SPCIAL_PLAN_ - 专项计划
     */
    public String getSpcialPlan() {
        return spcialPlan;
    }

    /**
     * 设置专项计划
     *
     * @param spcialPlan 专项计划
     */
    public void setSpcialPlan(String spcialPlan) {
        this.spcialPlan = spcialPlan == null ? null : spcialPlan.trim();
    }

    /**
     * 获取学籍状态
     *
     * @return REGISTRATION_STATUS_ - 学籍状态
     */
    public String getRegistrationStatus() {
        return registrationStatus;
    }

    /**
     * 设置学籍状态
     *
     * @param registrationStatus 学籍状态
     */
    public void setRegistrationStatus(String registrationStatus) {
        this.registrationStatus = registrationStatus == null ? null : registrationStatus.trim();
    }

    public String getTrainingCategory() {
        return trainingCategory;
    }

    public void setTrainingCategory(String trainingCategory) {
        this.trainingCategory = trainingCategory;
    }

    public String getEnrolMethods() {
        return enrolMethods;
    }

    public void setEnrolMethods(String enrolMethods) {
        this.enrolMethods = enrolMethods;
    }

    public String getIsOverseas() {
        return isOverseas;
    }

    public void setIsOverseas(String isOverseas) {
        this.isOverseas = isOverseas;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", trainingLevel=").append(trainingLevel);
        sb.append(", trainingCategory=").append(trainingCategory);
        sb.append(", enrolMethods=").append(enrolMethods);
        sb.append(", spcialPlan=").append(spcialPlan);
        sb.append(", isOverseas=").append(isOverseas);
        sb.append(", registrationStatus=").append(registrationStatus);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RebuildCourseNoChargeType that = (RebuildCourseNoChargeType) o;
        return Objects.equals(trainingLevel, that.trainingLevel) &&
                Objects.equals(trainingCategory, that.trainingCategory) &&
                Objects.equals(enrolMethods, that.enrolMethods) &&
                Objects.equals(spcialPlan, that.spcialPlan) &&
                Objects.equals(isOverseas, that.isOverseas) &&
                Objects.equals(registrationStatus, that.registrationStatus);
    }

    @Override
    public int hashCode() {

        return Objects.hash(trainingLevel, trainingCategory, enrolMethods, spcialPlan, isOverseas, registrationStatus);
    }
}