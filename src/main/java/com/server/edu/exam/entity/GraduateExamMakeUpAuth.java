package com.server.edu.exam.entity;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.dictionary.translator.SchoolCalendarTranslator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "graduate_exam_make_up_auth_t")
@CodeI18n
public class GraduateExamMakeUpAuth implements Serializable {
    /**
     * 主键
     */
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 学期ID
     */
    @Code2Text(translator = SchoolCalendarTranslator.class)
    @Column(name = "CALENDAR_ID_")
    private Long calendarId;

    /**
     * 申请类型 3 补考 2 缓考
     */
    @Column(name = "APPLY_TYPE_")
    private Integer applyType;

    /**
     * 开始时间
     */
    @Column(name = "BEGIN_TIME_")
    private Date beginTime;

    /**
     * 结束时间
     */
    @Column(name = "END_TIME_")
    private Date endTime;

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

    /**
     * 删除 1 已删除 0 未删除
     */
    @Column(name = "DELETE_STATUS_")
    private Integer deleteStatus;

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
     * 获取学期ID
     *
     * @return CALENDAR_ID_ - 学期ID
     */
    public Long getCalendarId() {
        return calendarId;
    }

    /**
     * 设置学期ID
     *
     * @param calendarId 学期ID
     */
    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }

    /**
     * 获取申请类型 1 补考 2 缓考
     *
     * @return APPLY_TYPE_ - 申请类型 1 补考 2 缓考
     */
    public Integer getApplyType() {
        return applyType;
    }

    /**
     * 设置申请类型 1 补考 2 缓考
     *
     * @param applyType 申请类型 1 补考 2 缓考
     */
    public void setApplyType(Integer applyType) {
        this.applyType = applyType;
    }

    /**
     * 获取开始时间
     *
     * @return BEGIN_TIME_ - 开始时间
     */
    public Date getBeginTime() {
        return beginTime;
    }

    /**
     * 设置开始时间
     *
     * @param beginTime 开始时间
     */
    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    /**
     * 获取结束时间
     *
     * @return END_TIME_ - 结束时间
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * 设置结束时间
     *
     * @param endTime 结束时间
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", calendarId=").append(calendarId);
        sb.append(", applyType=").append(applyType);
        sb.append(", beginTime=").append(beginTime);
        sb.append(", endTime=").append(endTime);
        sb.append(", projId=").append(projId);
        sb.append(", createAt=").append(createAt);
        sb.append(", updateAt=").append(updateAt);
        sb.append(", deleteStatus=").append(deleteStatus);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}