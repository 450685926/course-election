package com.server.edu.mutual.entity;

import java.io.Serializable;

/**
 * @Author htxiongc
 * @create 2020/2/26 15:59
 * @Description:
 */
public class ElcCrossStdVo implements Serializable {
    //学院
    private String faculty;
    //校历
    private Long calendarId;

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public Long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }

    @Override
    public String toString() {
        return "ElcCrossStdVo{" +
                "faculty='" + faculty + '\'' +
                ", calendarId=" + calendarId +
                '}';
    }
}
