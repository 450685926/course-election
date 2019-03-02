package com.server.edu.election.studentelec.context;

import java.util.List;

public class TimeUnit {
    private int timeStart;
    private int timeEnd;
    private int dayOfWeek;
    private List<Integer> weeks;
    
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

	public List<Integer> getWeeks() {
		return weeks;
	}

	public void setWeeks(List<Integer> weeks) {
		this.weeks = weeks;
	}
    
    
}
