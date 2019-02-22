package com.server.edu.election.vo;

import java.io.Serializable;

/**
 * @description: 课表时间显示
 * @author: bear
 * @create: 2019-02-20 19:34
 */
public class TimeTable implements Serializable{
    private Integer dayOfWeek;
    private Integer timeStart;
    private Integer timeEnd;
    private String value;

    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Integer getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Integer timeStart) {
        this.timeStart = timeStart;
    }

    public Integer getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Integer timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
