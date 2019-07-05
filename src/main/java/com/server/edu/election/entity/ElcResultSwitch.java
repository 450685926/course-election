package com.server.edu.election.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.server.edu.common.jackson.LongJsonSerializer;


/**
 * 选课结果处理开关
 * @author xlluoc
 * 2019-07-04
 */
@Table(name = "election_result_switch_t")
public class ElcResultSwitch implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
     * 主键（自增）
     */
	@Id
    @Column(name = "ID_")
    @JsonSerialize(using = LongJsonSerializer.class)
    private Long id;
	
	/**
	 * 校历ID（学年学期）
	 */
	@Column(name = "CALENDAR_ID_")
	private Long calendarId;
	
	/**
	 * 是否启用0否，1是
	 */
	@Column(name = "STATUS_")
	private Long status;
	
	/**
	 * 起始时间
	 */
	@Column(name = "OPEN_TIME_START_")
	private Date openTimeStart;
	
	/**
	 * 截止时间
	 */
	@Column(name = "OPEN_TIME_END_")
	private Date openTimeEnd;
	
	/**
	 * 创建时间
	 */
	@Column(name = "CREATED_AT_")
	private Date createAt;
	
	/**
	 * 管理部门ID
	 */
	@Column(name = "PROJ_ID_")
	private String projectId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCalendarId() {
		return calendarId;
	}

	public void setCalendarId(Long calendarId) {
		this.calendarId = calendarId;
	}

	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	public Date getOpenTimeStart() {
		return openTimeStart;
	}

	public void setOpenTimeStart(Date openTimeStart) {
		this.openTimeStart = openTimeStart;
	}

	public Date getOpenTimeEnd() {
		return openTimeEnd;
	}

	public void setOpenTimeEnd(Date openTimeEnd) {
		this.openTimeEnd = openTimeEnd;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	@Override
	public String toString() {
		return "ElcResultSwitch [id=" + id + ", calendarId=" + calendarId + ", status=" + status + ", openTimeStart="
				+ openTimeStart + ", openTimeEnd=" + openTimeEnd + ", createAt=" + createAt + ", projectId=" + projectId
				+ "]";
	}
	
}
