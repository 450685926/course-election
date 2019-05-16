package com.server.edu.election.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "teaching_class_teacher_t")
public class TeachingClassTeacher implements Serializable {
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
     * 教师类别(0任课教师,1助课教师,2课程负责人，3研究生助教)
     */
    @Column(name = "TYPE_")
    private Integer type;

    /**
     * 授课教师
     */
    @Column(name = "TEACHER_CODE_")
    private String teacherCode;

    /**
     * 授课教师姓名（冗余）
     */
    @Column(name = "TEACHER_NAME_")
    private String teacherName;

    /**
     * 性别
     */
    @Column(name = "SEX_")
    private Integer sex;

    /**
     * 教师职称（冗余）
     */
    @Column(name = "TEACHER_TITLE_")
    private String teacherTitle;

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
     * 获取教师类别(0任课教师,1助课教师,2课程负责人，3研究生助教)
     *
     * @return TYPE_ - 教师类别(0任课教师,1助课教师,2课程负责人，3研究生助教)
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置教师类别(0任课教师,1助课教师,2课程负责人，3研究生助教)
     *
     * @param type 教师类别(0任课教师,1助课教师,2课程负责人，3研究生助教)
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * 获取授课教师
     *
     * @return TEACHER_CODE_ - 授课教师
     */
    public String getTeacherCode() {
        return teacherCode;
    }

    /**
     * 设置授课教师
     *
     * @param teacherCode 授课教师
     */
    public void setTeacherCode(String teacherCode) {
        this.teacherCode = teacherCode == null ? null : teacherCode.trim();
    }

    /**
     * 获取授课教师姓名（冗余）
     *
     * @return TEACHER_NAME_ - 授课教师姓名（冗余）
     */
    public String getTeacherName() {
        return teacherName;
    }

    /**
     * 设置授课教师姓名（冗余）
     *
     * @param teacherName 授课教师姓名（冗余）
     */
    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName == null ? null : teacherName.trim();
    }

    /**
     * 获取性别
     *
     * @return SEX_ - 性别
     */
    public Integer getSex() {
        return sex;
    }

    /**
     * 设置性别
     *
     * @param sex 性别
     */
    public void setSex(Integer sex) {
        this.sex = sex;
    }

    /**
     * 获取教师职称（冗余）
     *
     * @return TEACHER_TITLE_ - 教师职称（冗余）
     */
    public String getTeacherTitle() {
        return teacherTitle;
    }

    /**
     * 设置教师职称（冗余）
     *
     * @param teacherTitle 教师职称（冗余）
     */
    public void setTeacherTitle(String teacherTitle) {
        this.teacherTitle = teacherTitle == null ? null : teacherTitle.trim();
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
        sb.append(", type=").append(type);
        sb.append(", teacherCode=").append(teacherCode);
        sb.append(", teacherName=").append(teacherName);
        sb.append(", sex=").append(sex);
        sb.append(", teacherTitle=").append(teacherTitle);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}