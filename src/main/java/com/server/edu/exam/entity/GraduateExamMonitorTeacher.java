package com.server.edu.exam.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "graduate_exam_monitor_teacher_t")
public class GraduateExamMonitorTeacher implements Serializable {
    /**
     * 主键
     */
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 考生数量低值
     */
    @Column(name = "STUDENT_MIN_NUMBER_")
    private Integer studentMinNumber;

    /**
     * 考生数量高值
     */
    @Column(name = "STUDENT_MAX_NUMBER_")
    private Integer studentMaxNumber;

    /**
     * 监考老师数量
     */
    @Column(name = "TEACHER_NUMBER_")
    private Integer teacherNumber;

    /**
     * 删除 1 已删除 0 未删除
     */
    @Column(name = "DELETE_STATUS_")
    private Integer deleteStatus;

    /**
     * 部门ID,预留在职研究生
     */
    @Column(name = "PROJ_ID_")
    private String projId;

    /**
     * 创建时间
     */
    @Column(name = "CREATE_AT_")
    private Date createAt;

    /**
     * 更新时间
     */
    @Column(name = "UPDATE_AT_")
    private Date updateAt;

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
     * 获取考生数量低值
     *
     * @return STUDENT_MIN_NUMBER_ - 考生数量低值
     */
    public Integer getStudentMinNumber() {
        return studentMinNumber;
    }

    /**
     * 设置考生数量低值
     *
     * @param studentMinNumber 考生数量低值
     */
    public void setStudentMinNumber(Integer studentMinNumber) {
        this.studentMinNumber = studentMinNumber;
    }

    /**
     * 获取考生数量高值
     *
     * @return STUDENT_MAX_NUMBER_ - 考生数量高值
     */
    public Integer getStudentMaxNumber() {
        return studentMaxNumber;
    }

    /**
     * 设置考生数量高值
     *
     * @param studentMaxNumber 考生数量高值
     */
    public void setStudentMaxNumber(Integer studentMaxNumber) {
        this.studentMaxNumber = studentMaxNumber;
    }

    /**
     * 获取监考老师数量
     *
     * @return TEACHER_NUMBER_ - 监考老师数量
     */
    public Integer getTeacherNumber() {
        return teacherNumber;
    }

    /**
     * 设置监考老师数量
     *
     * @param teacherNumber 监考老师数量
     */
    public void setTeacherNumber(Integer teacherNumber) {
        this.teacherNumber = teacherNumber;
    }

    /**
     * 获取删除 1 已删除 0 未删除
     *
     * @return DELETE_STATUS_ - 删除 1 已删除 0 未删除
     */
    public Integer getDeleteStatus() {
        return deleteStatus;
    }

    /**
     * 设置删除 1 已删除 0 未删除
     *
     * @param deleteStatus 删除 1 已删除 0 未删除
     */
    public void setDeleteStatus(Integer deleteStatus) {
        this.deleteStatus = deleteStatus;
    }

    /**
     * 获取部门ID,预留在职研究生
     *
     * @return PROJ_ID_ - 部门ID,预留在职研究生
     */
    public String getProjId() {
        return projId;
    }

    /**
     * 设置部门ID,预留在职研究生
     *
     * @param projId 部门ID,预留在职研究生
     */
    public void setProjId(String projId) {
        this.projId = projId == null ? null : projId.trim();
    }

    /**
     * 获取创建时间
     *
     * @return CREATE_AT_ - 创建时间
     */
    public Date getCreateAt() {
        return createAt;
    }

    /**
     * 设置创建时间
     *
     * @param createAt 创建时间
     */
    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    /**
     * 获取更新时间
     *
     * @return UPDATE_AT_ - 更新时间
     */
    public Date getUpdateAt() {
        return updateAt;
    }

    /**
     * 设置更新时间
     *
     * @param updateAt 更新时间
     */
    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", studentMinNumber=").append(studentMinNumber);
        sb.append(", studentMaxNumber=").append(studentMaxNumber);
        sb.append(", teacherNumber=").append(teacherNumber);
        sb.append(", deleteStatus=").append(deleteStatus);
        sb.append(", projId=").append(projId);
        sb.append(", createAt=").append(createAt);
        sb.append(", updateAt=").append(updateAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}