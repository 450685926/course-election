package com.server.edu.election.entity;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "exemption_material_extension_t")
public class ExemptionCourseMaterial implements Serializable {
    /**
     * 主键
     */
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 免修规则表ID
     */
    @Column(name = "EXEMPTION_RULE_ID_")
    private Long exemptionRuleId;

    /**
     * 申请免修类型
     */
    @Column(name = "EXEMPTION_TYPE_")
    private String exemptionType;

    /**
     * 英文
     */
    @Column(name = "ENGLISH_")
    private String english;

    /**
     * 说明
     */
    @Column(name = "EXPLAIN_")
    private String explain;

    private static final long serialVersionUID = 1L;

    /**
     * 获取主键
     *
     * @return ID_ - 主键
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取免修规则表ID
     *
     * @return EXEMPTION_RULE_ID_ - 免修规则表ID
     */
    public Long getExemptionRuleId() {
        return exemptionRuleId;
    }

    /**
     * 设置免修规则表ID
     *
     * @param exemptionRuleId 免修规则表ID
     */
    public void setExemptionRuleId(Long exemptionRuleId) {
        this.exemptionRuleId = exemptionRuleId;
    }

    /**
     * 获取申请免修类型
     *
     * @return EXEMPTION_TYPE_ - 申请免修类型
     */
    public String getExemptionType() {
        return exemptionType;
    }

    /**
     * 设置申请免修类型
     *
     * @param exemptionType 申请免修类型
     */
    public void setExemptionType(String exemptionType) {
        this.exemptionType = exemptionType == null ? null : exemptionType.trim();
    }

    /**
     * 获取英文
     *
     * @return ENGLISH_ - 英文
     */
    public String getEnglish() {
        return english;
    }

    /**
     * 设置英文
     *
     * @param english 英文
     */
    public void setEnglish(String english) {
        this.english = english == null ? null : english.trim();
    }

    /**
     * 获取说明
     *
     * @return EXPLAIN_ - 说明
     */
    public String getExplain() {
        return explain;
    }

    /**
     * 设置说明
     *
     * @param explain 说明
     */
    public void setExplain(String explain) {
        this.explain = explain == null ? null : explain.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", exemptionRuleId=").append(exemptionRuleId);
        sb.append(", exemptionType=").append(exemptionType);
        sb.append(", english=").append(english);
        sb.append(", explain=").append(explain);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}