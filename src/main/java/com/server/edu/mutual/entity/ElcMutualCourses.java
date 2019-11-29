package com.server.edu.mutual.entity;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "elc_mutual_courses_t")
public class ElcMutualCourses implements Serializable {
    /**
     * 主键
     */
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 校历ID（学年学期）
     */
    @Column(name = "CALENDAR_ID_")
    private Long calendarId;

    /**
     * 课程ID(老系统是用课程ID关联，为了兼容老系统此处也是用ID进行关联，且关联的表为course_open_t)
     */
    @Column(name = "COURSE_ID_")
    private Long courseId;

    /**
     * 本研互选课程标识字段  值为1表示为互选课程  值为null表示不为互选课程
     */
    @Column(name = "BY_TYPE_")
    private Integer byType;

    /**
     * 跨院系互选课程标识字段  值为1表示为互选课程  值为null表示不为互选课程
     */
    @Column(name = "IN_TYPE_")
    private Integer inType;

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
     * 获取课程ID
     *
     * @return COURSE_ID_ - 课程ID
     */
    public Long getCourseId() {
        return courseId;
    }

    /**
     * 设置课程ID
     *
     * @param courseId 课程ID
     */
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    /**
     * 获取本研互选课程标识字段  值为1表示为互选课程  值为null表示不为互选课程
     *
     * @return BY_TYPE_ - 本研互选课程标识字段  值为1表示为互选课程  值为null表示不为互选课程
     */
    public Integer getByType() {
        return byType;
    }

    /**
     * 设置本研互选课程标识字段  值为1表示为互选课程  值为null表示不为互选课程
     *
     * @param byType 本研互选课程标识字段  值为1表示为互选课程  值为null表示不为互选课程
     */
    public void setByType(Integer byType) {
        this.byType = byType;
    }

    /**
     * 获取跨院系互选课程标识字段  值为1表示为互选课程  值为null表示不为互选课程
     *
     * @return IN_TYPE_ - 跨院系互选课程标识字段  值为1表示为互选课程  值为null表示不为互选课程
     */
    public Integer getInType() {
        return inType;
    }

    /**
     * 设置跨院系互选课程标识字段  值为1表示为互选课程  值为null表示不为互选课程
     *
     * @param inType 跨院系互选课程标识字段  值为1表示为互选课程  值为null表示不为互选课程
     */
    public void setInType(Integer inType) {
        this.inType = inType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", calendarId=").append(calendarId);
        sb.append(", courseId=").append(courseId);
        sb.append(", byType=").append(byType);
        sb.append(", inType=").append(inType);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}