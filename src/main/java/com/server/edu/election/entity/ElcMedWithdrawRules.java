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

@Table(name = "elc_med_withdraw_rules_t")
public class ElcMedWithdrawRules implements Serializable {
    /**
     * 主键（自增）
     */
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 名称
     */
    @NotBlank
    @Column(name = "NAME")
    private String name;

    @Column(name = "PROJECT_ID")
    private Integer projectId;

    /**
     * 校历ID（学年学期）
     */
    @NotNull
    @Column(name = "CALENDAR_ID_")
    private Long calendarId;

    /**
     * 是否开启
     */
    @Column(name = "OPEN_FLAG_")
    private Boolean openFlag;

    /**
     * 课程结束周小于等于不得退课
     */
    @Column(name = "COURSE_END_WEEK_")
    private Integer courseEndWeek;

    /**
     * 外语强化班不得退课
     */
    @Column(name = "ENGLISH_COURSE_")
    private Boolean englishCourse;

    /**
     * 体育课不得退课
     */
    @Column(name = "PE_COURSE_")
    private Boolean peCourse;

    /**
     * 实践课不得退课 
     */
    @Column(name = "PRACTICE_COURSE_")
    private Boolean practiceCourse;

    /**
     * 重修课程不得退课
     */
    @Column(name = "RETAKE_COURSE_")
    private Boolean retakeCourse;

    /**
     * 收费单价
     */
    @Column(name = "PRICE_")
    private Double price;

    /**
     * 申请开始时间
     */
    @NotNull
    @Column(name = "BEGIN_TIME_")
    private Date beginTime;

    /**
     * 申请结束时间  
     */
    @NotNull
    @Column(name = "END_TIME_")
    private Date endTime;

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
     * 获取名称
     *
     * @return NAME - 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称
     *
     * @param name 名称
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * @return PROJECT_ID
     */
    public Integer getProjectId() {
        return projectId;
    }

    /**
     * @param projectId
     */
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    /**
     * 获取校历ID（学年学期）
     *
     * @return CALENDAR_ID_ - 校历ID（学年学期）
     */
    public Long getCalendarId() {
        return calendarId;
    }

    /**
     * 设置校历ID（学年学期）
     *
     * @param calendarId 校历ID（学年学期）
     */
    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }

    /**
     * 获取是否开启
     *
     * @return OPEN_FLAG_ - 是否开启
     */
    public Boolean getOpenFlag() {
        return openFlag;
    }

    /**
     * 设置是否开启
     *
     * @param openFlag 是否开启
     */
    public void setOpenFlag(Boolean openFlag) {
        this.openFlag = openFlag;
    }

    /**
     * 获取课程结束周小于等于不得退课
     *
     * @return COURSE_END_WEEK_ - 课程结束周小于等于不得退课
     */
    public Integer getCourseEndWeek() {
        return courseEndWeek;
    }

    /**
     * 设置课程结束周小于等于不得退课
     *
     * @param courseEndWeek 课程结束周小于等于不得退课
     */
    public void setCourseEndWeek(Integer courseEndWeek) {
        this.courseEndWeek = courseEndWeek;
    }

    /**
     * 获取外语强化班不得退课
     *
     * @return ENGLISH_COURSE_ - 外语强化班不得退课
     */
    public Boolean getEnglishCourse() {
        return englishCourse;
    }

    /**
     * 设置外语强化班不得退课
     *
     * @param englishCourse 外语强化班不得退课
     */
    public void setEnglishCourse(Boolean englishCourse) {
        this.englishCourse = englishCourse;
    }

    /**
     * 获取体育课不得退课
     *
     * @return PE_COURSE_ - 体育课不得退课
     */
    public Boolean getPeCourse() {
        return peCourse;
    }

    /**
     * 设置体育课不得退课
     *
     * @param peCourse 体育课不得退课
     */
    public void setPeCourse(Boolean peCourse) {
        this.peCourse = peCourse;
    }

    /**
     * 获取实践课不得退课 
     *
     * @return PRACTICE_COURSE_ - 实践课不得退课 
     */
    public Boolean getPracticeCourse() {
        return practiceCourse;
    }

    /**
     * 设置实践课不得退课 
     *
     * @param practiceCourse 实践课不得退课 
     */
    public void setPracticeCourse(Boolean practiceCourse) {
        this.practiceCourse = practiceCourse;
    }

    /**
     * 获取重修课程不得退课
     *
     * @return RETAKE_COURSE_ - 重修课程不得退课
     */
    public Boolean getRetakeCourse() {
        return retakeCourse;
    }

    /**
     * 设置重修课程不得退课
     *
     * @param retakeCourse 重修课程不得退课
     */
    public void setRetakeCourse(Boolean retakeCourse) {
        this.retakeCourse = retakeCourse;
    }

    /**
     * 获取收费单价
     *
     * @return PRICE_ - 收费单价
     */
    public Double getPrice() {
        return price;
    }

    /**
     * 设置收费单价
     *
     * @param price 收费单价
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * 获取申请开始时间
     *
     * @return BEGIN_TIME_ - 申请开始时间
     */
    public Date getBeginTime() {
        return beginTime;
    }

    /**
     * 设置申请开始时间
     *
     * @param beginTime 申请开始时间
     */
    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    /**
     * 获取申请结束时间  
     *
     * @return END_TIME_ - 申请结束时间  
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * 设置申请结束时间  
     *
     * @param endTime 申请结束时间  
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
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
        sb.append(", name=").append(name);
        sb.append(", projectId=").append(projectId);
        sb.append(", calendarId=").append(calendarId);
        sb.append(", openFlag=").append(openFlag);
        sb.append(", courseEndWeek=").append(courseEndWeek);
        sb.append(", englishCourse=").append(englishCourse);
        sb.append(", peCourse=").append(peCourse);
        sb.append(", practiceCourse=").append(practiceCourse);
        sb.append(", retakeCourse=").append(retakeCourse);
        sb.append(", price=").append(price);
        sb.append(", beginTime=").append(beginTime);
        sb.append(", endTime=").append(endTime);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}