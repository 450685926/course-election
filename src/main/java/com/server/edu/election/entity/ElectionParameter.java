package com.server.edu.election.entity;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "election_parameter_t")
public class ElectionParameter implements Serializable {
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 参数描述
     */
    @Column(name = "DESCRIPTION_")
    private String description;

    /**
     * 参数名称
     */
    @Column(name = "NAME_")
    private String name;

    /**
     * 参数值(如：英语小课门数限制)
     */
    @Column(name = "VALUE_")
    private String value;

    /**
     * 参数标题
     */
    @Column(name = "TITLE_")
    private String title;

    /**
     * 选课规则ID
     */
    @Column(name = "RULE_ID_")
    private Long ruleId;

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
     * 获取参数描述
     *
     * @return DESCRIPTION_ - 参数描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置参数描述
     *
     * @param description 参数描述
     */
    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    /**
     * 获取参数名称
     *
     * @return NAME_ - 参数名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置参数名称
     *
     * @param name 参数名称
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * 获取参数值(如：英语小课门数限制)
     *
     * @return VALUE_ - 参数值(如：英语小课门数限制)
     */
    public String getValue() {
        return value;
    }

    /**
     * 设置参数值(如：英语小课门数限制)
     *
     * @param value 参数值(如：英语小课门数限制)
     */
    public void setValue(String value) {
        this.value = value == null ? null : value.trim();
    }

    /**
     * 获取参数标题
     *
     * @return TITLE_ - 参数标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置参数标题
     *
     * @param title 参数标题
     */
    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
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
        sb.append(", id=").append(id);
        sb.append(", description=").append(description);
        sb.append(", name=").append(name);
        sb.append(", value=").append(value);
        sb.append(", title=").append(title);
        sb.append(", ruleId=").append(ruleId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}