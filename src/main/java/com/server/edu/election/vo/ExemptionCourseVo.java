package com.server.edu.election.vo;

import com.server.edu.election.entity.ExemptionCourse;

/**
 * @description:
 * @author: bear
 * @create: 2019-01-31 15:01
 */
public class ExemptionCourseVo extends ExemptionCourse {
    private String calendarName;

    public String getCalendarName() {
        return calendarName;
    }

    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }
}
