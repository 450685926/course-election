package com.server.edu.mutual.dto;

import com.server.edu.election.dto.Student4Elc;

public class ElcMutualCrossStu extends Student4Elc {
	private Long calendarId;
	private Integer mode;
	public Long getCalendarId() {
		return calendarId;
	}
	public void setCalendarId(Long calendarId) {
		this.calendarId = calendarId;
	}
	public Integer getMode() {
		return mode;
	}
	public void setMode(Integer mode) {
		this.mode = mode;
	}
}
