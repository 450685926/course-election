package com.server.edu.election.entity;

import java.io.Serializable;

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


    private String deptId;

    /**
     * 培养层次 X_PYCC
     */
    @Code2Text(DictTypeEnum.X_PYCC)
    @Column(name = "TRAINING_LEVEL_")
    private String trainingLevel;

    /**
     * 学习形式(全日制,非全日制,其他)
     */
    @Code2Text(DictTypeEnum.X_XXXS)
    @Column(name = "FORM_LEARNING_")
    private String formLearning;

    /**
     * 专项计划
     */
    @Code2Text(DictTypeEnum.X_ZXJH)
    @Column(name = "SPCIAL_PLAN_")
    private String spcialPlan;

    /**
     * 学籍状态
     */
    @Code2Text(DictTypeEnum.G_XJZT)
    @Column(name = "REGISTRATION_STATUS_")
    private String registrationStatus;

    /**
     * 结业证书类型 1 结业生 空 未结业
     */
    @Column(name = "CERTIFICATE_TYPE_")
    private Integer certificateType;

    private static final long serialVersionUID = 1L;

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

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
     * 获取学习形式(全日制,非全日制,其他)
     *
     * @return FORM_LEARNING_ - 学习形式(全日制,非全日制,其他)
     */
    public String getFormLearning() {
        return formLearning;
    }

    /**
     * 设置学习形式(全日制,非全日制,其他)
     *
     * @param formLearning 学习形式(全日制,非全日制,其他)
     */
    public void setFormLearning(String formLearning) {
        this.formLearning = formLearning == null ? null : formLearning.trim();
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

    /**
     * 获取结业证书类型 1 结业生 空 未结业
     *
     * @return CERTIFICATE_TYPE_ - 结业证书类型 1 结业生 空 未结业
     */
    public Integer getCertificateType() {
        return certificateType;
    }

    /**
     * 设置结业证书类型 1 结业生 空 未结业
     *
     * @param certificateType 结业证书类型 1 结业生 空 未结业
     */
    public void setCertificateType(Integer certificateType) {
        this.certificateType = certificateType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", trainingLevel=").append(trainingLevel);
        sb.append(", formLearning=").append(formLearning);
        sb.append(", spcialPlan=").append(spcialPlan);
        sb.append(", registrationStatus=").append(registrationStatus);
        sb.append(", certificateType=").append(certificateType);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}