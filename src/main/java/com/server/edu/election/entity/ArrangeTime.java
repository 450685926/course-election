package com.server.edu.election.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import com.server.edu.common.BaseEntity;
import com.server.edu.election.util.ArrangeTimeUnit;

@Table(name = "teaching_class_arrange_time_t")
public class ArrangeTime extends BaseEntity implements ArrangeTimeUnit
{
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
     * 星期几1-7
     */
    @Column(name = "DAY_OF_WEEK_")
    private Integer dayOfWeek;

    /**
     * 开始节次1-12
     */
    @Column(name = "TIME_START_")
    private Integer timeStart;

    /**
     * 结束节次1-12
     */
    @Column(name = "TIME_END_")
    private Integer timeEnd;

    /**
     * 一周的第几次课
     */
    @Column(name = "TIME_NUMBER_")
    private Integer timeNumber;


    /**
     * 一周的第几次课
     */
    @Column(name = "WEEK_STATE_")
    private Long weekState;

    /**
     * 排课概述信息 用于展示
     */
    @Column(name = "PROFILE_")
    private String profile;

    /**
     * 类型1预排课, 2手工排课
     */
    @Column(name = "TYPE_")
    private Integer type;

//    @Column(name = "ARRANGE_MODE_")
//    private Integer arrangeMode;

    /**
     * 创建时间
     */
    @Column(name = "CREATED_AT_")
    private Date createdAt;


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
     * 获取星期几1-7
     *
     * @return DAY_OF_WEEK_ - 星期几1-7
     */
    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    /**
     * 设置星期几1-7
     *
     * @param dayOfWeek 星期几1-7
     */
    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    /**
     * 获取开始节次1-12
     *
     * @return TIME_START_ - 开始节次1-12
     */
    public Integer getTimeStart() {
        return timeStart;
    }

    /**
     * 设置开始节次1-12
     *
     * @param timeStart 开始节次1-12
     */
    public void setTimeStart(Integer timeStart) {
        this.timeStart = timeStart;
    }

    /**
     * 获取结束节次1-12
     *
     * @return TIME_END_ - 结束节次1-12
     */
    public Integer getTimeEnd() {
        return timeEnd;
    }

    /**
     * 设置结束节次1-12
     *
     * @param timeEnd 结束节次1-12
     */
    public void setTimeEnd(Integer timeEnd) {
        this.timeEnd = timeEnd;
    }
    
    /**
     * 获取一周的第几次课
     *
     * @return TIME_NUMBER_ - 一周的第几次课
     */
    public Integer getTimeNumber()
    {
        return timeNumber;
    }

    /**
     * 设置一周的第几次课
     *
     * @param timeNumber 一周的第几次课
     */
    public void setTimeNumber(Integer timeNumber)
    {
        this.timeNumber = timeNumber;
    }

    /**
     * 获取类型1预排课, 2手工排课
     *
     * @return TYPE_ - 类型1预排课, 2手工排课
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置类型1预排课, 2手工排课
     *
     * @param type 类型1预排课, 2手工排课
     */
    public void setType(Integer type) {
        this.type = type;
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
     * 节次是否相等
     * @param unitDto
     * @return
     */
    public boolean isEqual(ArrangeTime unitDto) {
        return this.dayOfWeek == unitDto.getDayOfWeek()
                && this.timeStart == unitDto.getTimeStart()
                && this.timeEnd == unitDto.getTimeEnd();
    }

    /**
     * 传入的节次是否与当前节次有交集
     * @param unitDto
     * @return max(A.start,B.start)<=min(A.end,B.end)
     */
    public boolean isCrossing(ArrangeTime unitDto) {
        return this.dayOfWeek == unitDto.getDayOfWeek()
                && Math.max(this.timeStart, unitDto.getTimeStart()) <= Math.min(this.timeEnd, unitDto.getTimeEnd());
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public Long getWeekState() {
        return weekState;
    }

    public void setWeekState(Long weekState) {
        this.weekState = weekState;
    }
}