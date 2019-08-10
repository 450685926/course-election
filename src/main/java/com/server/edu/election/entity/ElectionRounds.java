package com.server.edu.election.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotBlank;
import com.server.edu.dictionary.utils.SchoolCalendarCacheUtil;

@Table(name = "election_rounds_t")
public class ElectionRounds implements Serializable {
    /**
     * 主键（自增）
     */
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 校历ID（学年学期）
     */
    @NotNull
    @Column(name = "CALENDAR_ID_")
    private Long calendarId;

    /**
     * 第几轮
     */
    @NotNull
    @Column(name = "TURN_")
    private Integer turn;

    /**
     * 选课对象(STU学生，DEPART_ADMIN教务员，MANAGER_ADMIN管理员)
     */
    @NotBlank
    @Column(name = "ELECTION_OBJ_")
    private String electionObj;

    /**
     * 选课模式(1正常,2实践课,3结业生,4留学结业生)
     */
    @NotNull
    @Column(name = "MODE_")
    private Integer mode;

    /**
     * 管理部门id（字典取值）
     */
    @NotBlank
    @Column(name = "MANAGER_DEPT_ID_")
    private String projectId;

    /**
     * 轮次名称
     */
    @NotBlank
    @Column(name = "NAME_")
    private String name;

    /**
     * 是否开启(0否,1是)
     */
    @NotNull
    @Column(name = "OPEN_FLAG_")
    private Integer openFlag;

    /**
     * 开始时间
     */
    @NotNull
    @Column(name = "BEGIN_TIME_")
    private Date beginTime;

    /**
     * 结束时间
     */
    @Column(name = "END_TIME_")
    private Date endTime;

    /**
     * 备注
     */
    @NotBlank
    @Column(name = "REMARK_")
    private String remark;

    /**
     * 创建时间
     */
    @Column(name = "CREATED_AT_")
    private Date createdAt;

    @Column(name = "UPDATED_AT_")
    private Date updatedAt;

    private static final long serialVersionUID = 1L;
    
    /**
            * 学年学期名称
     */
    @Transient
    private String calendarName;
    
    /**
     * 是否删除（1-已删除；0-未删除）
     */
    @Column(name = "DELETE_STATUS_")
    private Integer deleteStatus;
    
    public String getCalendarName() {
		return calendarName;
	}

	public void setCalendarName(String calendarName) {
		this.calendarName = calendarName;
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
        if (calendarId != null) {
			setCalendarName(SchoolCalendarCacheUtil.getName(calendarId));
		}
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
     * 获取选课对象(STU学生，DEPART_ADMIN教务员，MANAGER_ADMIN管理员)
     *
     * @return ELECTION_OBJ_ - 选课对象(STU学生，DEPART_ADMIN教务员，MANAGER_ADMIN管理员)
     */
    public String getElectionObj() {
        return electionObj;
    }

    /**
     * 设置选课对象(STU学生，DEPART_ADMIN教务员，MANAGER_ADMIN管理员)
     *
     * @param electionObj 选课对象(STU学生，DEPART_ADMIN教务员，MANAGER_ADMIN管理员)
     */
    public void setElectionObj(String electionObj) {
        this.electionObj = electionObj == null ? null : electionObj.trim();
    }

    /**
     * 获取选课模式(1正常,2实践课,3结业生,4留学结业生)
     *
     * @return MODE_ - 选课模式(1正常,2实践课,3结业生,4留学结业生)
     */
    public Integer getMode() {
        return mode;
    }

    /**
     * 设置选课模式(1正常,2实践课,3结业生,4留学结业生)
     *
     * @param mode 选课模式(1正常,2实践课,3结业生,4留学结业生)
     */
    public void setMode(Integer mode) {
        this.mode = mode;
    }

    /**
     * 获取PROJECT_ID
     *
     * @return MANAGER_DEPT_ID_ - PROJECT_ID
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * 设置PROJECT_ID
     *
     * @param projectId PROJECT_ID
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    /**
     * 获取轮次名称
     *
     * @return NAME_ - 轮次名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置轮次名称
     *
     * @param name 轮次名称
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * 获取是否开启(0否,1是)
     *
     * @return OPEN_FLAG_ - 是否开启(0否,1是)
     */
    public Integer getOpenFlag() {
        return openFlag;
    }

    /**
     * 设置是否开启(0否,1是)
     *
     * @param openFlag 是否开启(0否,1是)
     */
    public void setOpenFlag(Integer openFlag) {
        this.openFlag = openFlag;
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
     * 获取备注
     *
     * @return REMARK_ - 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注
     *
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
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
     * @return UPDATED_AT_
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * @param updatedAt
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

	public Integer getDeleteStatus() {
		return deleteStatus;
	}

	public void setDeleteStatus(Integer deleteStatus) {
		this.deleteStatus = deleteStatus;
	}

	@Override
	public String toString() {
		return "ElectionRounds [id=" + id + ", calendarId=" + calendarId + ", turn=" + turn + ", electionObj="
				+ electionObj + ", mode=" + mode + ", projectId=" + projectId + ", name=" + name + ", openFlag="
				+ openFlag + ", beginTime=" + beginTime + ", endTime=" + endTime + ", remark=" + remark + ", createdAt="
				+ createdAt + ", updatedAt=" + updatedAt + ", calendarName=" + calendarName + ", deleteStatus="
				+ deleteStatus + "]";
	}
    
	
}