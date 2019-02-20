package com.server.edu.election.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotBlank;

@Table(name = "election_constants_t")
public class ElectionConstants implements Serializable {
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 管理部门id（字典取值）
     */
    @NotBlank
    @Column(name = "MANAGER_DEPT_ID_")
    private String managerDeptId;

    /**
     * 培养层次(专科   本科   硕士   博士    其他    预科)
     */
    @NotBlank
    @Column(name = "TRAINING_LEVEL_")
    private String trainingLevel;

    /**
     * 规则键值
     */
    @NotBlank
    @Column(name = "KEY_")
    private String key;

    /**
     * 规则名称
     */
    @NotBlank
    @Column(name = "NAME_")
    private String name;

    /**
     * 备注说明
     */
    @Column(name = "REMARK_")
    private String remark;

    /**
     * 规则参数值
     */
    @NotBlank
    @Column(name = "VALUE_")
    private String value;

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



    public String getManagerDeptId() {
		return managerDeptId;
	}

	public void setManagerDeptId(String managerDeptId) {
		this.managerDeptId = managerDeptId;
	}

	/**
     * 获取培养层次(专科   本科   硕士   博士    其他    预科)
     *
     * @return TRAINING_LEVEL_ - 培养层次(专科   本科   硕士   博士    其他    预科)
     */
    public String getTrainingLevel() {
        return trainingLevel;
    }

    /**
     * 设置培养层次(专科   本科   硕士   博士    其他    预科)
     *
     * @param trainingLevel 培养层次(专科   本科   硕士   博士    其他    预科)
     */
    public void setTrainingLevel(String trainingLevel) {
        this.trainingLevel = trainingLevel == null ? null : trainingLevel.trim();
    }

    /**
     * 获取规则键值
     *
     * @return KEY_ - 规则键值
     */
    public String getKey() {
        return key;
    }

    /**
     * 设置规则键值
     *
     * @param key 规则键值
     */
    public void setKey(String key) {
        this.key = key == null ? null : key.trim();
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
     * 获取备注说明
     *
     * @return REMARK_ - 备注说明
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注说明
     *
     * @param remark 备注说明
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    /**
     * 获取规则参数值
     *
     * @return VALUE_ - 规则参数值
     */
    public String getValue() {
        return value;
    }

    /**
     * 设置规则参数值
     *
     * @param value 规则参数值
     */
    public void setValue(String value) {
        this.value = value == null ? null : value.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", managerDeptId=").append(managerDeptId);
        sb.append(", trainingLevel=").append(trainingLevel);
        sb.append(", key=").append(key);
        sb.append(", name=").append(name);
        sb.append(", remark=").append(remark);
        sb.append(", value=").append(value);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}