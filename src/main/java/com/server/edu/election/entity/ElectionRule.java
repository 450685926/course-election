package com.server.edu.election.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Table(name = "election_rule_t")
public class ElectionRule implements Serializable {
    /**
     * 主键（自增）
     */
    @Id
    @NotNull
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 规则名称
     */
    @Column(name = "NAME_")
    private String name;

    /**
     * 类别(ELECTION选课、GENERAL登录、WITHDRAW退课)
     */
    @Column(name = "TYPE_")
    private String type;

    /**
     * java对象名
     */
    @JsonIgnore
    @Column(name = "SERVICE_NAME_")
    private String serviceName;

    /**
     * PROJECT_ID
     */
    @Column(name = "PROJECT_ID_")
    private String projectId;

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
     * 获取类别(ELECTION选课、GENERAL登录、WITHDRAW退课)
     *
     * @return TYPE_ - 类别(ELECTION选课、GENERAL登录、WITHDRAW退课)
     */
    public String getType() {
        return type;
    }

    /**
     * 设置类别(ELECTION选课、GENERAL登录、WITHDRAW退课)
     *
     * @param type 类别(ELECTION选课、GENERAL登录、WITHDRAW退课)
     */
    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    /**
     * 获取java对象名
     *
     * @return SERVICE_NAME_ - java对象名
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * 设置java对象名
     *
     * @param serviceName java对象名
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName == null ? null : serviceName.trim();
    }

    /**
     * 获取PROJECT_ID
     *
     * @return PROJECT_ID_ - PROJECT_ID
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * 设置PROJECT_ID
     *
     * @param projectId PROJECT_ID
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId == null ? null : projectId.trim();
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
        sb.append(", serviceName=").append(serviceName);
        sb.append(", projectId=").append(projectId);
        sb.append(", remark=").append(remark);
        sb.append(", status=").append(status);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}