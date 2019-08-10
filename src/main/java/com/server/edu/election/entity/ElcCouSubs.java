package com.server.edu.election.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

/**替代课程*/
@Table(name = "elc_cou_subs_t")
public class ElcCouSubs implements Serializable {
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 学号
     */
    @NotBlank
    @Column(name = "STUDENT_ID_")
    private String studentId;

    /**
     * 管理部门ID
     */
    @Column(name = "PROJECT_ID_")
    private String projectId;
    
    /**
     * 原课程ID
     */
    @NotNull
    @Column(name = "ORIGS_COURSE_ID_")
    private Long origsCourseId;

    /**
     * 替代课程ID
     */
    @NotNull
    @Column(name = "SUB_COURSE_ID_")
    private Long subCourseId;

    /**
     * 备注
     */
    @Column(name = "REMARK_")
    private String remark;

    /**
     * 创建时间
     */
    @Column(name = "CREATED_AT_")
    private Date createdAt;

    /**
     * 修改时间
     */
    @Column(name = "UPDATED_AT")
    private Date updatedAt;

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
     * 获取学号
     *
     * @return STUDENT_ID_ - 学号
     */
    public String getStudentId() {
        return studentId;
    }

    /**
     * 设置学号
     *
     * @param studentId 学号
     */
    public void setStudentId(String studentId) {
        this.studentId = studentId == null ? null : studentId.trim();
    }

    /**
     * 获取管理部门ID
     *
     * @return PROJECT_ID_ - 管理部门ID
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * 设置管理部门ID
     *
     * @param projectId 管理部门ID
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId == null ? null : projectId.trim();
    }

    /**
     * 获取原课程ID
     *
     * @return ORIGS_COURSE_ID_ - 原课程ID
     */
    public Long getOrigsCourseId() {
        return origsCourseId;
    }

    /**
     * 设置原课程ID
     *
     * @param origsCourseId 原课程ID
     */
    public void setOrigsCourseId(Long origsCourseId) {
        this.origsCourseId = origsCourseId;
    }

    /**
     * 获取替代课程ID
     *
     * @return SUB_COURSE_ID_ - 替代课程ID
     */
    public Long getSubCourseId() {
        return subCourseId;
    }

    /**
     * 设置替代课程ID
     *
     * @param subCourseId 替代课程ID
     */
    public void setSubCourseId(Long subCourseId) {
        this.subCourseId = subCourseId;
    }

    /**
     * 获取备注
     *
     * @return REMARK_ - 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注
     *
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
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
     * @return UPDATED_AT - 修改时间
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
        sb.append(", projectId=").append(projectId);
        sb.append(", origsCourseId=").append(origsCourseId);
        sb.append(", subCourseId=").append(subCourseId);
        sb.append(", remark=").append(remark);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}