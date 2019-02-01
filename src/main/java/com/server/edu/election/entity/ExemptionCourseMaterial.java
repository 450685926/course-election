package com.server.edu.election.entity;

import java.io.Serializable;

/**
 * @description: 材料申请
 * @author: bear
 * @create: 2019-02-01 16:05
 */
public class ExemptionCourseMaterial implements Serializable {
    private Long id;

    private Long exemptionRuleId;

    private String exemptionType;

    private String english;

    private String explain;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getExemptionRuleId() {
        return exemptionRuleId;
    }

    public void setExemptionRuleId(Long exemptionRuleId) {
        this.exemptionRuleId = exemptionRuleId;
    }

    public String getExemptionType() {
        return exemptionType;
    }

    public void setExemptionType(String exemptionType) {
        this.exemptionType = exemptionType == null ? null : exemptionType.trim();
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english == null ? null : english.trim();
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain == null ? null : explain.trim();
    }
}
