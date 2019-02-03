package com.server.edu.election.entity;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "election_tpl_ref_rule_t")
public class ElectionTplRefRule implements Serializable {
    /**
     * 选课方案模版ID
     */
    @Id
    @Column(name = "TPL_ID_")
    private Long tplId;

    /**
     * 选课规则ID
     */
    @Id
    @Column(name = "RULE_ID_")
    private Long ruleId;

    private static final long serialVersionUID = 1L;

    /**
     * 获取选课方案模版ID
     *
     * @return TPL_ID_ - 选课方案模版ID
     */
    public Long getTplId() {
        return tplId;
    }

    /**
     * 设置选课方案模版ID
     *
     * @param tplId 选课方案模版ID
     */
    public void setTplId(Long tplId) {
        this.tplId = tplId;
    }

    /**
     * 获取选课规则ID
     *
     * @return RULE_ID_ - 选课规则ID
     */
    public Long getRuleId() {
        return ruleId;
    }

    /**
     * 设置选课规则ID
     *
     * @param ruleId 选课规则ID
     */
    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", tplId=").append(tplId);
        sb.append(", ruleId=").append(ruleId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}