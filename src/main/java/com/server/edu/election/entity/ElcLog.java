package com.server.edu.election.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.server.edu.common.jackson.LongJsonSerializer;

/**
 * 
 * 选课日志
 * 
 * @author  OuYangGuoDong
 * @version  [版本号, 2019年2月14日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Table(name = "election_log_t")
public class ElcLog implements Serializable {
    /**
     * 主键（自增）
     */
    @Id
    @Column(name = "ID_")
    @JsonSerialize(using = LongJsonSerializer.class)
    private Long id;

    /**
     * 学号
     */
    @Column(name = "STUDENT_ID_")
    private String studentId;

    /**
     * 课程code
     */
    @Column(name = "COURSE_CODE_")
    private String courseCode;

    /**
     * 课程名称
     */
    @Column(name = "COURSE_NAME_")
    private String courseName;

    /**
     * 教学班code
     */
    @Column(name = "TEACHING_CLASS_CODE_")
    private String teachingClassCode;

    /**
     * 校历ID（学年学期）
     */
    @Column(name = "CALENDAR_ID_")
    private Long calendarId;

    /**
     * 操作类型（1选课，2退课，3中期退课...）
     */
    @Column(name = "TYPE_")
    private Integer type;

    /**
     * 选课方式（1自选，2代选）
     */
    @Column(name = "MODE_")
    private Integer mode;

    /**
     * 第几轮
     */
    @Column(name = "TURN_")
    private Integer turn;

    /**
     * 操作人
     */
    @Column(name = "CREATE_BY_")
    private String createBy;

    /**
     * 操作IP
     */
    @Column(name = "CREATE_IP_")
    private String createIp;

    /**
     * 操作人姓名
     */
    @Column(name = "CREATE_NAME_")
    private String createName;

    /**
     * 创建时间
     */
    @Column(name = "CREATED_AT_")
    private Date createdAt;


    /**
     * 选退课失败原因
     * */
    @Column(name = "CONTENT_")
    private String content;

    private static final long serialVersionUID = 1L;

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

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
     * 获取课程code
     *
     * @return COURSE_CODE_ - 课程code
     */
    public String getCourseCode() {
        return courseCode;
    }

    /**
     * 设置课程code
     *
     * @param courseCode 课程code
     */
    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode == null ? null : courseCode.trim();
    }

    /**
     * 获取课程名称
     *
     * @return COURSE_NAME_ - 课程名称
     */
    public String getCourseName() {
        return courseName;
    }

    /**
     * 设置课程名称
     *
     * @param courseName 课程名称
     */
    public void setCourseName(String courseName) {
        this.courseName = courseName == null ? null : courseName.trim();
    }

    /**
     * 获取教学班code
     *
     * @return TEACHING_CLASS_CODE_ - 教学班code
     */
    public String getTeachingClassCode() {
        return teachingClassCode;
    }

    /**
     * 设置教学班code
     *
     * @param teachingClassCode 教学班code
     */
    public void setTeachingClassCode(String teachingClassCode) {
        this.teachingClassCode = teachingClassCode == null ? null : teachingClassCode.trim();
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
     * 获取操作类型（1选课，2退课，3中期退课...）
     *
     * @return TYPE_ - 操作类型（1选课，2退课，3中期退课...）
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置操作类型（1选课，2退课，3中期退课...）
     *
     * @param type 操作类型（1选课，2退课，3中期退课...）
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * 获取选课方式（1自选，2代选）
     *
     * @return MODE_ - 选课方式（1自选，2代选）
     */
    public Integer getMode() {
        return mode;
    }

    /**
     * 设置选课方式（1自选，2代选）
     *
     * @param mode 选课方式（1自选，2代选）
     */
    public void setMode(Integer mode) {
        this.mode = mode;
    }

    /**
     * 获取第几轮
     *
     * @return TURN_ - 第几轮
     */
    public Integer getTurn() {
        return turn;
    }

    /**
     * 设置第几轮
     *
     * @param turn 第几轮
     */
    public void setTurn(Integer turn) {
        this.turn = turn;
    }

    /**
     * 获取操作人
     *
     * @return CREATE_BY_ - 操作人
     */
    public String getCreateBy() {
        return createBy;
    }

    /**
     * 设置操作人
     *
     * @param createBy 操作人
     */
    public void setCreateBy(String createBy) {
        this.createBy = createBy == null ? null : createBy.trim();
    }

    /**
     * 获取操作IP
     *
     * @return CREATE_IP_ - 操作IP
     */
    public String getCreateIp() {
        return createIp;
    }

    /**
     * 设置操作IP
     *
     * @param createIp 操作IP
     */
    public void setCreateIp(String createIp) {
        this.createIp = createIp == null ? null : createIp.trim();
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



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", studentId=").append(studentId);
        sb.append(", courseCode=").append(courseCode);
        sb.append(", courseName=").append(courseName);
        sb.append(", teachingClassCode=").append(teachingClassCode);
        sb.append(", calendarId=").append(calendarId);
        sb.append(", type=").append(type);
        sb.append(", mode=").append(mode);
        sb.append(", turn=").append(turn);
        sb.append(", createBy=").append(createBy);
        sb.append(", createIp=").append(createIp);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}