package com.server.edu.election.vo;

import com.server.edu.election.studentelec.context.SelectedCourse;

public class SelectedCourseVo extends SelectedCourse {
	private Long arrangeTimeId;
    private int timeStart;
    private int timeEnd;
    private int dayOfWeek;
    private int week;
    
    
	public Long getArrangeTimeId() {
		return arrangeTimeId;
	}
	
	public void setArrangeTimeId(Long arrangeTimeId) {
		this.arrangeTimeId = arrangeTimeId;
	}
	public int getTimeStart() {
		return timeStart;
	}
	public void setTimeStart(int timeStart) {
		this.timeStart = timeStart;
	}
	public int getTimeEnd() {
		return timeEnd;
	}
	public void setTimeEnd(int timeEnd) {
		this.timeEnd = timeEnd;
	}
	public int getDayOfWeek() {
		return dayOfWeek;
	}
	public void setDayOfWeek(int dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
	public int getWeek() {
		return week;
	}
	public void setWeek(int week) {
		this.week = week;
	}
    
    

}
