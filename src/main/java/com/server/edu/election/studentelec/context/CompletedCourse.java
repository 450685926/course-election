package com.server.edu.election.studentelec.context;

import java.util.Objects;

import com.server.edu.election.studentelec.cache.TeachingClassCache;
import org.apache.commons.lang3.StringUtils;

/**
 * 已完成课程
 */
public class CompletedCourse extends TeachingClassCache {
    /**
     * 成绩
     */
    private String score;

    /**
     * 等级
     */
    private String rank;

    /**
     * 是否作弊
     */
    private boolean cheat;

    /**
     * 是否为优级
     */
    private boolean excellent;

    /**
     * 学期
     */
    private Long calendarId;

    public Long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public boolean isCheat() {
        return cheat;
    }

    public void setCheat(boolean cheat) {
        this.cheat = cheat;
    }

    public boolean isExcellent() {
        return excellent;
    }

    public void setExcellent(boolean excellent) {
        this.excellent = excellent;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.score, this.getCourseCode(), this.calendarId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof CompletedCourse) {
            CompletedCourse o = (CompletedCourse) obj;
            return StringUtils.equals(this.score, o.score)
                    && StringUtils.equals(this.getCourseCode(), o.getCourseCode())
                    && Objects.equals(this.calendarId,o.calendarId );
        }
        return false;
    }

}