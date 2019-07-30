package com.server.edu.election.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "teaching_class_elective_restrict_profession_t")
public class TeachingClassElectiveRestrictProfession implements Serializable {
    /**
     * 主键（自增）
     */
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 教学班ID
     */
    @Column(name = "TEACHING_CLASS_ID_")
    private Long teachingClassId;

    /**
     * 年级(默认0，代表研究生专业)
     */
    @Column(name = "GRADE_")
    private Integer grade;

    /**
     * 专业（编码）
     */
    @Column(name = "PROFESSION_")
    private String profession;

    /**
     * 人数
     */
    @Column(name = "NUMBER_")
    private Integer number;

    /**
     * 是否限制人数(0否 1是)
     */
    @Column(name = "IS_LIMIT_NUMBER_")
    private Integer isLimitNumber;

    /**
     * 专业方向CODE
     */
    @Column(name = "DIRECTION_CODE_")
    private String directionCode;

    /**
     * 专业方向名称（冗余）
     */
    @Column(name = "DIRECTION_NAME_")
    private String directionName;

    /**
     * 创建时间
     */
    @Column(name = "CREATED_AT_")
    private Date createdAt;

    /**
     * 修改时间
     */
    @Column(name = "UPDATED_AT_")
    private Date updatedAt;

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

    /**
     * 获取年级(默认0，代表研究生专业)
     *
     * @return GRADE_ - 年级(默认0，代表研究生专业)
     */
    public Integer getGrade() {
        return grade;
    }

    /**
     * 设置年级(默认0，代表研究生专业)
     *
     * @param grade 年级(默认0，代表研究生专业)
     */
    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    /**
     * 获取专业（编码）
     *
     * @return PROFESSION_ - 专业（编码）
     */
    public String getProfession() {
        return profession;
    }

    /**
     * 设置专业（编码）
     *
     * @param profession 专业（编码）
     */
    public void setProfession(String profession) {
        this.profession = profession == null ? null : profession.trim();
    }

    /**
     * 获取人数
     *
     * @return NUMBER_ - 人数
     */
    public Integer getNumber() {
        return number;
    }

    /**
     * 设置人数
     *
     * @param number 人数
     */
    public void setNumber(Integer number) {
        this.number = number;
    }

    /**
     * 获取是否限制人数(0否 1是)
     *
     * @return IS_LIMIT_NUMBER_ - 是否限制人数(0否 1是)
     */
    public Integer getIsLimitNumber() {
        return isLimitNumber;
    }

    /**
     * 设置是否限制人数(0否 1是)
     *
     * @param isLimitNumber 是否限制人数(0否 1是)
     */
    public void setIsLimitNumber(Integer isLimitNumber) {
        this.isLimitNumber = isLimitNumber;
    }

    /**
     * 获取专业方向CODE
     *
     * @return DIRECTION_CODE_ - 专业方向CODE
     */
    public String getDirectionCode() {
        return directionCode;
    }

    /**
     * 设置专业方向CODE
     *
     * @param directionCode 专业方向CODE
     */
    public void setDirectionCode(String directionCode) {
        this.directionCode = directionCode == null ? null : directionCode.trim();
    }

    /**
     * 获取专业方向名称（冗余）
     *
     * @return DIRECTION_NAME_ - 专业方向名称（冗余）
     */
    public String getDirectionName() {
        return directionName;
    }

    /**
     * 设置专业方向名称（冗余）
     *
     * @param directionName 专业方向名称（冗余）
     */
    public void setDirectionName(String directionName) {
        this.directionName = directionName == null ? null : directionName.trim();
    }

    /**
     * 获取创建时间
     *
     * @return CREATED_AT_ - 创建时间
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * 设置创建时间
     *
     * @param createdAt 创建时间
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 获取修改时间
     *
     * @return UPDATED_AT_ - 修改时间
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 设置修改时间
     *
     * @param updatedAt 修改时间
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", teachingClassId=").append(teachingClassId);
        sb.append(", grade=").append(grade);
        sb.append(", profession=").append(profession);
        sb.append(", number=").append(number);
        sb.append(", isLimitNumber=").append(isLimitNumber);
        sb.append(", directionCode=").append(directionCode);
        sb.append(", directionName=").append(directionName);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}