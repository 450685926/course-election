package com.server.edu.election.entity;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;

import java.io.Serializable;
import javax.persistence.*;

@CodeI18n
@Table(name = "rebuild_course_charge_t")
public class RebuildCourseCharge implements Serializable {
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 培养层次 X_PYCC
     */
    @Code2Text(transformer = "X_PYCC")
    @Column(name = "TRAINING_LEVEL_")
    private String trainingLevel;

    /**
     * 学习形式X_XXXS
     */
    @Code2Text(transformer = "X_XXXS")
    @Column(name = "FORM_LEARNING_")
    private String formLearning;

    /**
     * 是否收费 0 免费 1 收费
     */
    @Column(name = "IS_CHARGE_")
    private Integer isCharge;

    /**
     * 单价/每学分
     */
    @Column(name = "UNIT_PRICE_")
    private Integer unitPrice;

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
     * 获取学习形式X_XXXS
     *
     * @return FORM_LEARNING_ - 学习形式X_XXXS
     */
    public String getFormLearning() {
        return formLearning;
    }

    /**
     * 设置学习形式X_XXXS
     *
     * @param formLearning 学习形式X_XXXS
     */
    public void setFormLearning(String formLearning) {
        this.formLearning = formLearning == null ? null : formLearning.trim();
    }

    /**
     * 获取是否收费 0 免费 1 收费
     *
     * @return IS_CHARGE_ - 是否收费 0 免费 1 收费
     */
    public Integer getIsCharge() {
        return isCharge;
    }

    /**
     * 设置是否收费 0 免费 1 收费
     *
     * @param isCharge 是否收费 0 免费 1 收费
     */
    public void setIsCharge(Integer isCharge) {
        this.isCharge = isCharge;
    }

    /**
     * 获取单价/每学分
     *
     * @return UNIT_PRICE_ - 单价/每学分
     */
    public Integer getUnitPrice() {
        return unitPrice;
    }

    /**
     * 设置单价/每学分
     *
     * @param unitPrice 单价/每学分
     */
    public void setUnitPrice(Integer unitPrice) {
        this.unitPrice = unitPrice;
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
        sb.append(", isCharge=").append(isCharge);
        sb.append(", unitPrice=").append(unitPrice);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}