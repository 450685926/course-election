package com.server.edu.election.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Table(name = "election_tpl_t")
public class ElectionTpl implements Serializable {
    /**
     * 主键（自增）
     */
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 方案名称
     */
    @NotNull
    @Column(name = "NAME_")
    private String name;

    /**
     * 方案描述
     */
    @Column(name = "REMARK_")
    private String remark;

    /**
     * 是否启用0否，1是
     */
    @NotNull
    @Column(name = "STATUS_")
    private Integer status;
    
    @Column(name = "MANAGER_DEPT_ID_")
    private String projectId;

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
     * 获取方案名称
     *
     * @return NAME_ - 方案名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置方案名称
     *
     * @param name 方案名称
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * 获取方案描述
     *
     * @return REMARK_ - 方案描述
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置方案描述
     *
     * @param remark 方案描述
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

    public String getProjectId()
    {
        return projectId;
    }

    public void setProjectId(String projectId)
    {
        this.projectId = projectId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", remark=").append(remark);
        sb.append(", status=").append(status);
        sb.append(", projectId=").append(projectId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}