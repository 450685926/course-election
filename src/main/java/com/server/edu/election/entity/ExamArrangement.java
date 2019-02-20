package com.server.edu.election.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Table(name = "exam_arrangement_t")
public class ExamArrangement implements Serializable {
    /**
     * 主键（自增）
     */
    @Id
    @NotNull
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 学生代码
     */
    @Column(name = "STUDENT_CODE_")
    private String studentCode;

    /**
     * 姓名
     */
    @Column(name = "NAME_")
    private String name;

    /**
     * 性别(根据数据字典统一使用)
     */
    @Column(name = "SEX_")
    private Integer sex;

    /**
     * 考试科目
     */
    @Column(name = "SUBJECT_")
    private String subject;

    /**
     * 考试时间
     */
    @Column(name = "DATE_")
    private Date date;

    /**
     * 考试时间地点
     */
    @Column(name = "LOCATION_")
    private String location;

    /**
     * 考试须知
     */
    @Column(name = "NOTICE_")
    private String notice;

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
     * 获取学生代码
     *
     * @return STUDENT_CODE_ - 学生代码
     */
    public String getStudentCode() {
        return studentCode;
    }

    /**
     * 设置学生代码
     *
     * @param studentCode 学生代码
     */
    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode == null ? null : studentCode.trim();
    }

    /**
     * 获取姓名
     *
     * @return NAME_ - 姓名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置姓名
     *
     * @param name 姓名
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * 获取性别(根据数据字典统一使用)
     *
     * @return SEX_ - 性别(根据数据字典统一使用)
     */
    public Integer getSex() {
        return sex;
    }

    /**
     * 设置性别(根据数据字典统一使用)
     *
     * @param sex 性别(根据数据字典统一使用)
     */
    public void setSex(Integer sex) {
        this.sex = sex;
    }

    /**
     * 获取考试科目
     *
     * @return SUBJECT_ - 考试科目
     */
    public String getSubject() {
        return subject;
    }

    /**
     * 设置考试科目
     *
     * @param subject 考试科目
     */
    public void setSubject(String subject) {
        this.subject = subject == null ? null : subject.trim();
    }

    /**
     * 获取考试时间
     *
     * @return DATE_ - 考试时间
     */
    public Date getDate() {
        return date;
    }

    /**
     * 设置考试时间
     *
     * @param date 考试时间
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * 获取考试时间地点
     *
     * @return LOCATION_ - 考试时间地点
     */
    public String getLocation() {
        return location;
    }

    /**
     * 设置考试时间地点
     *
     * @param location 考试时间地点
     */
    public void setLocation(String location) {
        this.location = location == null ? null : location.trim();
    }

    /**
     * 获取考试须知
     *
     * @return NOTICE_ - 考试须知
     */
    public String getNotice() {
        return notice;
    }

    /**
     * 设置考试须知
     *
     * @param notice 考试须知
     */
    public void setNotice(String notice) {
        this.notice = notice == null ? null : notice.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", studentCode=").append(studentCode);
        sb.append(", name=").append(name);
        sb.append(", sex=").append(sex);
        sb.append(", subject=").append(subject);
        sb.append(", date=").append(date);
        sb.append(", location=").append(location);
        sb.append(", notice=").append(notice);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}