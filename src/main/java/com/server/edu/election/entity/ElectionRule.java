package com.server.edu.election.entity;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "election_rule_t")
public class ElectionRule implements Serializable {
    /**
     * 主键（自增）
     */
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 规则名称
     */
    @Column(name = "NAME_")
    private String name;

    /**
     * 类别(1.选课、2.登录)
     */
    @Column(name = "TYPE_")
    private String type;

    /**
     * 规则代码
     */
    @Column(name = "CODE_")
    private String code;

    /**
     * 父级代码
     */
    @Column(name = "PARENT_CODE_")
    private Long parentCode;

    /**
     * 参数值(如：英语小课门数限制)
     */
    @Column(name = "VALUE_")
    private String value;

    /**
     * PROJECT_ID
     */
    @Column(name = "PROJECT_ID_")
    private Integer projectId;

    /**
     * 规则描述
     */
    @Column(name = "REMARK_")
    private String remark;

    /**
     * 是否启用0否，1是
     */
    @Column(name = "STATUS_")
    private Integer status;

    private static final long serialVersionUID = 1L;

    /**
     * 获取主键（自增）
     *
     * @return ID_ - 主键（自增）
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置主键（自增）
     *
     * @param id 主键（自增）
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取规则名称
     *
     * @return NAME_ - 规则名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置规则名称
     *
     * @param name 规则名称
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * 获取类别(1.选课、2.登录)
     *
     * @return TYPE_ - 类别(1.选课、2.登录)
     */
    public String getType() {
        return type;
    }

    /**
     * 设置类别(1.选课、2.登录)
     *
     * @param type 类别(1.选课、2.登录)
     */
    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    /**
     * 获取规则代码
     *
     * @return CODE_ - 规则代码
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置规则代码
     *
     * @param code 规则代码
     */
    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    /**
     * 获取父级代码
     *
     * @return PARENT_CODE_ - 父级代码
     */
    public Long getParentCode() {
        return parentCode;
    }

    /**
     * 设置父级代码
     *
     * @param parentCode 父级代码
     */
    public void setParentCode(Long parentCode) {
        this.parentCode = parentCode;
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
     * 获取PROJECT_ID
     *
     * @return PROJECT_ID_ - PROJECT_ID
     */
    public Integer getProjectId() {
        return projectId;
    }

    /**
     * 设置PROJECT_ID
     *
     * @param projectId PROJECT_ID
     */
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    /**
     * 获取规则描述
     *
     * @return REMARK_ - 规则描述
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置规则描述
     *
     * @param remark 规则描述
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    /**
     * 获取是否启用0否，1是
     *
     * @return STATUS_ - 是否启用0否，1是
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置是否启用0否，1是
     *
     * @param status 是否启用0否，1是
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", type=").append(type);
        sb.append(", code=").append(code);
        sb.append(", parentCode=").append(parentCode);
        sb.append(", value=").append(value);
        sb.append(", projectId=").append(projectId);
        sb.append(", remark=").append(remark);
        sb.append(", status=").append(status);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}