package com.server.edu.election.vo;

import com.server.edu.election.entity.Course;

public class ElectionApplyCoursesVo extends Course {
    private static final long serialVersionUID = 1L;
    private Long electionApplyCourseId;
    private Long calendarId;
	public Long getCalendarId() {
		return calendarId;
	}
	public void setCalendarId(Long calendarId) {
		this.calendarId = calendarId;
	}
	public Long getElectionApplyCourseId() {
		return electionApplyCourseId;
	}
	public void setElectionApplyCourseId(Long electionApplyCourseId) {
		this.electionApplyCourseId = electionApplyCourseId;
	}
	
    

}
