package com.server.edu.election.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;

@Table(name = "elc_affinity_courses_t")
public class ElcAffinityCourses implements Serializable
{
    /**
     * 主键（自增）
     */
    @Id
    @NotNull
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 课程ID
     */
    @Column(name = "COURSE_CODE_")
    private String courseCode;
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 获取主键（自增）
     *
     * @return ID_ - 主键（自增）
     */
    public Long getId()
    {
        return id;
    }
    
    /**
     * 设置主键（自增）
     *
     * @param id 主键（自增）
     */
    public void setId(Long id)
    {
        this.id = id;
    }
    
    /**
     * 获取课程代码
     *
     * @return COURSE_ID_ - 课程ID
     */
    public String getCourseCode()
    {
        return courseCode;
    }
    
    /**
     * 设置课程代码
     *
     * @param courseCode 课程代码
     */
    public void setCourseCode(String courseCode)
    {
        this.courseCode = StringUtils.trim(courseCode);
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", courseCode=").append(courseCode);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}