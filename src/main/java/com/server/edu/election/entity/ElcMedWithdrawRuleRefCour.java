package com.server.edu.election.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Table(name = "elc_med_withdraw_rule_ref_cour_t")
public class ElcMedWithdrawRuleRefCour implements Serializable {
    /**
     * 中期退课规则ID
     */
	@NotNull
    @Id
    @Column(name = "MED_WITHDRAW_RULE_ID_")
    private Long medWithdrawRuleId;

    /**
     * 教学班ID
     */
    @Id
    @Column(name = "TEACHING_CLASS_ID_")
    private Long teachingClassId;

    private static final long serialVersionUID = 1L;

    /**
     * 获取中期退课规则ID
     *
     * @return MED_WITHDRAW_RULE_ID_ - 中期退课规则ID
     */
    public Long getMedWithdrawRuleId() {
        return medWithdrawRuleId;
    }

    /**
     * 设置中期退课规则ID
     *
     * @param medWithdrawRuleId 中期退课规则ID
     */
    public void setMedWithdrawRuleId(Long medWithdrawRuleId) {
        this.medWithdrawRuleId = medWithdrawRuleId;
    }

    /**
     * 获取教学班ID
     *
     * @return TEACHING_CLASS_ID_ - 教学班ID
     */
    public Long getTeachingClassId() {
        return teachingClassId;
    }

    /**
     * 设置教学班ID
     *
     * @param teachingClassId 教学班ID
     */
    public void setTeachingClassId(Long teachingClassId) {
        this.teachingClassId = teachingClassId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", medWithdrawRuleId=").append(medWithdrawRuleId);
        sb.append(", teachingClassId=").append(teachingClassId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}