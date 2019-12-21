package com.server.edu.election.util;

import java.util.HashMap;
import java.util.Map;

public class TimeBitUtil {
    private final int days;
    private final int times;
    private final Map<Integer,Long> dayOfWeekBit1Map;
    private final Map<Integer,Long> dayOfWeekBit2Map;
    private final Map<Integer,Long> timeBit1Map;
    private final Map<Integer,Long> timeBit2Map;

    /**
     * 按照时间单元初始化timebit 现用 12 * 7 也可改为其他值只要 times * days <= 128即可
     */
    public TimeBitUtil(int times, int days){
        this.times = times;
        this.days = days;
        dayOfWeekBit1Map = new HashMap<>(8);
        dayOfWeekBit2Map = new HashMap<>(8);
        timeBit1Map = new HashMap<>(16);
        timeBit2Map = new HashMap<>(16);

        final long baseWeekBit = (1L << times) - 1;
        for (int day = 0; day < days; day++) {
            long[] timeBit = initDayOfWeekBit(day, baseWeekBit);
            dayOfWeekBit1Map.put(day,timeBit[0]);
            dayOfWeekBit2Map.put(day,timeBit[1]);
        }
        initTimeBit();
    }

    /**
     * 初始化日期map
     */
    private long[] initDayOfWeekBit(int day, long baseWeekBit){
        long [] bits = new long[2];

        int offset = day * times;
        if (offset < Long.SIZE) {
            bits[1] = baseWeekBit << offset;
        }
        if (offset + times - Long.SIZE > 0) {
            offset = offset - Long.SIZE;
            if (offset >= 0) {
                bits[0] = baseWeekBit << offset;
            }else {
                bits[0] = baseWeekBit >> -offset;
            }
        }
        return  bits;
    }
    /**
     * 初始化节次map
     */
    private void initTimeBit(){
        for (int time = 0; time < times; time++) {
            long baseBit1 = 0L;
            long baseBit2 = 0L;
            for (int day = 0; day < days; day++) {
                int offset = day * times + time;
                if (offset<Long.SIZE){
                    baseBit2 |= 1L << offset;
                } else {
                    baseBit1 |= 1L << (offset - 64);
                }
            }
            this.timeBit1Map.put(time, baseBit1);
            this.timeBit2Map.put(time, baseBit2);
        }
    }

    /**
     * 获取指定天的 timebit值
     */
    public long[] getDayBit(int ... days){
        long [] result = new long[2];
        for (int day : days) {
            if (day >=0 && day < this.days){
                result[0] |= dayOfWeekBit1Map.get(day);
                result[1] |= dayOfWeekBit2Map.get(day);
            }
        }
        return result;
    }
    /**
     * 获取指定时段的timebit值
     */
    public long[] getTimeBit(int ... times){
        long [] result = new long[2];
        for (int time : times) {
            if (time >=0 && time < this.times){
                result[0] |= timeBit1Map.get(time);
                result[1] |= timeBit2Map.get(time);
            }
        }
        return result;
    }
    /**
     * 获取指定日期时段的timebit值
     */
    public long[] getDayTimeBit(int[] days, int[] times){
        long[] dayBit = getDayBit(days);
        long[] timeBit = getTimeBit(times);
        return new long[]{dayBit[0] & timeBit[0], dayBit[1] & timeBit[1]};
    }

}
