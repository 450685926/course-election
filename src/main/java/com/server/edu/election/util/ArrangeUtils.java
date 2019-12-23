package com.server.edu.election.util;

import java.util.*;

import com.server.edu.election.constants.Constants;
import com.server.edu.election.entity.ArrangeTime;

/**
 * 对应前端项目内代码 /src/utils/arrange.js, 方便在一端计算并在另一端逆运算<br>
 * {@link #calcWeekState(Collection)} 和 {@link #splitWeekState(long)}互为逆运算，
 * 注意在前端使用weekstate的位运算一定不能超过int的上限，仅在后端使用不受限制<br>
 * {@link #calcTimeState(ArrangeTimeUnit)} 和 {@link #splitTimeState(int)}互为逆运算
 *
 * @author liuzheng 2019/7/26 14:43
 */
public class ArrangeUtils {
    private static final int INIT = -1;
    private static final int START = 0;
    /** 连续的 */
    private static final int SERIAL = 1;
    /** 间隔的 */
    private static final int INTERVAL = 2;
    private static final int SEPARATED = 3;

    private static final int COMPLEX_THRESHOLD = 3;

    private static class LangString {
        String odd;
        String even;
        String[] weekDays;
    }

    private static class Params {
        List<Integer>arr;
        StringBuilder str;
        int mode;
        Locale locale;
        int count = 0;
    }
    private static final Map<String,LangString> LocaleMap = new HashMap<>(4);
    static {
        LangString cn = new LangString();
        cn.odd = "单";
        cn.even = "双";
        // 0和7都代表星期日
        cn.weekDays =  new String[]{"星期日","星期一","星期二","星期三","星期四","星期五","星期六","星期日"};
        LangString en = new LangString();
        en.odd = "Odd";
        en.even = "Even";
        en.weekDays = new String[]{"Sun.","Mon.","Tues.","Wed.","Thur.","Fri.","Sat.","Sun."};
        LocaleMap.put(Locale.CHINESE.getLanguage(),cn);
        LocaleMap.put(Locale.ENGLISH.getLanguage(),en);
    }

    private ArrangeUtils(){}

    public static boolean checkConflict(ArrangeTime timetable1,ArrangeTime timetable2){
        try{
            int time1 = 1<< timetable1.getTimeStart()-1 | 1<<timetable1.getTimeEnd()-1;
            int time2 = 1<< timetable2.getTimeStart()-1 | 1<<timetable2.getTimeEnd()-1;
            return (timetable1.getWeekState()&timetable2.getWeekState())>0
                    && (time1&time2)>0 && timetable1.getDayOfWeek().equals(timetable2.getDayOfWeek());
        }catch (NullPointerException ignore){
            return false;
        }
    }

    public static int calcTimeState(ArrangeTimeUnit timeUnit){
        int day = timeUnit.getDayOfWeek();
        int start = timeUnit.getTimeStart();
        int end = timeUnit.getTimeEnd();
        return  day-1+((1<< start-1 | 1<<end-1)<<3);
    }

    public static ArrangeTimeUnit splitTimeState(int timeState){
        int startAndEnd = timeState >> 3;
        // 快速找到比上面值大的最小2的整数幂
        int cap = 1;
        int end = 0;
        while(cap<=startAndEnd){
            cap<<=1;
            end++;
        }
        int endBit = cap>>1;
        int startBit = startAndEnd - endBit;
        int start = 0;
        if (startBit == 0){
            start = end;
        }else {
            while(startBit > 0){
                startBit >>= 1;
                start++;
            }
        }
        int dayOfWeek = (timeState & 7) + 1;
        int timeStart = start;
        int timeEnd = end;
        return new ArrangeTimeUnit() {
            @Override
            public Integer getDayOfWeek() {
                return dayOfWeek;
            }

            @Override
            public Integer getTimeStart() {
                return timeStart;
            }

            @Override
            public Integer getTimeEnd() {
                return timeEnd;
            }
        };
    }

    /**
     * @param weeks 周集合
     */
    public static long calcWeekState (Collection<Integer> weeks){
        // 虽说后端weekstate取的是long但是如果要传递给前端“计算”的weekState不能大于int最大值 因为js的位运算只支持32位
        long weekState = 0;
        for (int weekNum : weeks) {
            if (weekNum < 1 || weekNum > Constants.MAX_WEEK) {
                throw new IllegalArgumentException("weekNum必须在1-"+Constants.MAX_WEEK+"之间");
            }
            weekState |= 1 << weekNum - 1;
        }
        return weekState;
    }

    /**
     * {@link #calcWeekState(Collection)} 的逆运算
     */
    public static List<Integer> splitWeekState (long weekState){
        List<Integer> weeks = new ArrayList<>();
        int weekNum = 1;
        while (weekState > 0){
            if ((weekState & 1) > 0){
                weeks.add(weekNum);
            }
            weekNum ++;
            weekState >>=1;
        }
        return weeks;
    }

    /**
     * 周状态解析为字符串
     * @param locale 可以为空默认中文
     */
    public static String weekStateToString(long weekState,Locale locale){

        // splitWeekState 方法已经保证了有序 就不用排序了
        List<Integer> list = splitWeekState(weekState);
        return parse(list,locale);
    }
    /**
     * 周状态解析为字符串
     * @param locale 可以为空默认中文
     */
    public static String weekStateToString(Collection<Integer> weeks,Locale locale){
        // 要保证有序
        TreeSet<Integer> weekSet = new TreeSet<>(weeks);
        return parse(weekSet,locale);

    }

    public static String dayTimeToString(ArrangeTimeUnit arrangeTime){
        if (arrangeTime == null) {
            return "";
        }
        Integer end = arrangeTime.getTimeEnd();
        Integer start = arrangeTime.getTimeStart();
        Integer day = arrangeTime.getDayOfWeek();
        LangString langString = LocaleMap.get(Locale.CHINESE.getLanguage());
        return String.format("%s%s-%s节",langString.weekDays[day%7],start,end);
    }

    public static String dayTimeToString(int dayOfWeek, int start, int end){
        LangString langString = LocaleMap.get(Locale.CHINESE.getLanguage());
        return String.format("%s%s-%s节",langString.weekDays[dayOfWeek%7],start,end);
    }

    private static String simpleParse(Collection<Integer> weeks,Locale locale){
        int status = INIT;
        int preWeekNum = -1;
        int startWeek = 0;
        int endWeek = 0;
        a:for (Integer weekNum : weeks) {
            endWeek = weekNum;
            switch (status) {
                case INIT:
                    status = START;
                    startWeek = weekNum;
                    break;
                case START:
                    if (weekNum - preWeekNum == 1){
                        status = SERIAL;
                    }else if (weekNum - preWeekNum == 2){
                        status = INTERVAL;
                    }else{
                        status = SEPARATED;
                        break a;
                    }
                    break;
                case SERIAL:
                    if (weekNum - preWeekNum == 1){
                        break;
                    }else{
                        status = SEPARATED;
                        break a;
                    }
                case INTERVAL:
                    if (weekNum - preWeekNum == 2){
                        break;
                    }else{
                        status = SEPARATED;
                        break a;
                    }

            }
            preWeekNum = weekNum;
        }
        if (status == SEPARATED) {
            String str = weeks.stream().map(Object::toString).reduce((a, b) -> a + "," + b).orElse("周数据有误");
            return String.format("[%s]",str);

        }else{
            Params params = new Params();
            params.locale = locale;
            params.mode = status;
            params.arr = Arrays.asList(startWeek,endWeek);
            return modeToString(params);
        }
    }

    private static String parse(Collection<Integer> weeks,Locale locale){
        final ArrayList<Integer> arr = new ArrayList<>();
        final Params params = new Params();
        params.arr = arr;
        params.locale = locale;
        params.str = new StringBuilder();
        for (Integer weekNum : weeks) {
            if (arr.isEmpty()) {
                arr.add(weekNum);
                params.mode = START;
            } else if (arr.size() == 1){
                int pre = arr.get(0);
                int cap = weekNum - pre;
                if (cap == 1) {
                    params.mode = SERIAL;
                    arr.add(weekNum);
                }else if (cap == 2){
                    params.mode = INTERVAL;
                    arr.add(weekNum);
                }else{
                    reset(params,weekNum);
                }
            } else {
                int pre = arr.get(arr.size()-1);
                int cap = weekNum - pre;
                if (cap == 1) {
                    if (params.mode == SERIAL)
                        arr.add(weekNum);
                    else
                        reset(params, weekNum);
                }else if (cap == 2){
                    if (params.mode == INTERVAL)
                        arr.add(weekNum);
                    else
                        reset(params, weekNum);
                }else{
                    reset(params,weekNum);
                }
            }
//            if (params.count>=COMPLEX_THRESHOLD) {
//                String str = weeks.stream().map(Object::toString).reduce((a, b) -> a + "," + b).orElse("0");
//                return String.format("[%s]",str);
//            }
        }
        reset(params,0);
        return String.format("[%s]", params.str.toString().trim());
    }

    private static String getLanguage(Locale locale){
        if (locale == null || Locale.CHINESE.getLanguage().equals(locale.getLanguage())) {
            return Locale.CHINESE.getLanguage();
        } else {
            return Locale.ENGLISH.getLanguage();
        }
    }

    private static String modeToString(Params params){
        String modeStr = "";
        LangString langString = LocaleMap.get(getLanguage(params.locale));
        List<Integer> arr = params.arr;
        if (arr.size()==1) {
            return String.format("%s ",arr.get(0));
        }
        if (params.mode == INTERVAL){
            Integer start = arr.get(0);
            modeStr = start%2==0?langString.even:langString.odd;
        }
        return String.format("%s-%s%s ", arr.get(0), arr.get(arr.size()-1),modeStr);
    }

    private static void reset(Params params,int weekNum){
        if (params.arr.isEmpty()) {
            return;
        }
        params.str.append(modeToString(params));
        params.arr.clear();
        params.mode = START;
        if (weekNum>0) {
            params.count++;
            params.arr.add(weekNum);
        }


    }
}
