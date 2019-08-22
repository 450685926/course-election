package com.server.edu.election.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
/**
 * 学生课程能力
 * 
 * 
 * @author  OuYangGuoDong
 * @version  [版本号, 2019年8月22日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Table(name = "elc_stu_cou_level_t")
public class ElcStuCouLevel implements Serializable {
    @Id
    @Column(name = "ID_")
    private Long id;

    /**
     * 学号
     */
    @NotBlank
    @Column(name = "STUDENT_ID_")
    private String studentId;

    /**
     * 课程等级ID(courses_category_t)
     */
    @NotNull
    @Column(name = "COURSE_CATEGORY_ID_")
    private Long courseCategoryId;

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
     * 获取课程等级ID(courses_category_t)
     *
     * @return COURSE_CATEGORY_ID_ - 课程等级ID(courses_category_t)
     */
    public Long getCourseCategoryId() {
        return courseCategoryId;
    }

    /**
     * 设置课程等级ID(courses_category_t)
     *
     * @param courseCategoryId 课程等级ID(courses_category_t)
     */
    public void setCourseCategoryId(Long courseCategoryId) {
        this.courseCategoryId = courseCategoryId;
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
        sb.append(", studentId=").append(studentId);
        sb.append(", courseCategoryId=").append(courseCategoryId);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}