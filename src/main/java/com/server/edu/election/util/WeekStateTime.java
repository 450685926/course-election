package com.server.edu.election.util;

import java.util.TreeSet;

import com.server.edu.election.constants.Constants;


public class WeekStateTime {
    public static final long ALL_WEEK = (1L << Constants.MAX_WEEK) - 1;
    private long weekState;
    // TreeSet作为存储时间段的数据结构，保证存取效率和数据有序
    private final TreeSet<Integer> timeUnits ;

    public WeekStateTime(long weekState){
        this.weekState = weekState;
        this.timeUnits = new TreeSet<>();
    }

    private WeekStateTime(long weekState, TreeSet<Integer> timeUnits){
        this.weekState = weekState;
        this.timeUnits = timeUnits;
    }

    public void addTime(int dayOfWeek, int timeStart, int timeEnd){
        int[] timeStates = getTimeStates(dayOfWeek, timeStart, timeEnd);
        for (int timeState : timeStates) {
            timeUnits.add(timeState);
        }
    }

    public boolean hasIntersection(WeekStateTime another){
        // 是否周冲突
        if ((this.weekState & another.weekState) == 0) {
            return false;
        }
        // 是否时段冲突
        for (Integer timeUnit : another.timeUnits) {
            if (timeUnits.contains(timeUnit)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取冲突时段
     * @return 如果没有冲突返回<tt>null</tt>
     */
    public WeekStateTime getIntersection(WeekStateTime another){
        // 是否周冲突
        long conflictWeekState = this.weekState & another.weekState;
        if ((conflictWeekState) == 0) {
            return null;
        }
        // 是否时段冲突
        TreeSet<Integer> conflictTimes = new TreeSet<>();
        for (Integer timeUnit : another.timeUnits) {
            if (this.timeUnits.contains(timeUnit)) {
                conflictTimes.add(timeUnit);
            }
        }
        return conflictTimes.size() > 0?  new WeekStateTime(conflictWeekState,conflictTimes) : null;
    }

    private int[] getTimeStates(int dayOfWeek, int timeStart, int timeEnd){
        int [] indexes = new int[timeEnd - timeStart +1];
        int timeIndex = timeStart - 1 + (dayOfWeek - 1) * Constants.TIMES;
        for (int i = 0; i <= indexes.length; i++) {
            indexes[i] = timeIndex + i;
        }
        return indexes;
    }
}
