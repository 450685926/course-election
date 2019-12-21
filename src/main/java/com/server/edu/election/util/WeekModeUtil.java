package com.server.edu.election.util;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.entity.ArrangeRoom;
import com.server.edu.election.entity.ArrangeTime;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 根据排课周解析周模式
 */
public class WeekModeUtil {
    private static final String odd = "单周";
    private static final String oddEn = " Odd"; // 英文前加空格
    private static final String even = "双周";
    private static final String evenEn = " Even";
    private static final String[] weekDay =
            new String[]{"星期一","星期二","星期三","星期四","星期五","星期六","星期日"};
    private static final String[] weekDayEn =
            new String[]{"Mon.","Tues.","Wed.","Thur.","Fri.","Sat.","Sun."};
    private static final int format_threshold = 5;

    /**
     * 根据排课周解析周模式<br>
     * null 或 Locale中的属于中文语言的都会匹配到中文值，其他语言均返回英文值<br>
     * 中文  WeekModeUtil.parse(weeks, Locale.CHINESE)<br>
     * 英文  WeekModeUtil.parse(weeks, Locale.ENGLISH)<br>
     */
    public static String parse(List<ArrangeRoom> weeks, Locale locale) {
        return ArrangeUtils.weekStateToString(
                weeks.stream().map(ArrangeRoom::getWeekNumber).collect(Collectors.toList()), locale);
    }

    /**
     * 根据排课周解析周模式<br>
     * null 或 Locale中的属于中文语言的都会匹配到中文值，其他语言均返回英文值<br>
     * 中文  WeekModeUtil.parse(weeks, Locale.CHINESE)<br>
     * 英文  WeekModeUtil.parse(weeks, Locale.ENGLISH)<br>
     */
    public static String parse(long weekState, Locale locale) {
        return ArrangeUtils.weekStateToString(weekState,locale);
    }


    private static String parse(List<ArrangeRoom> weeks,boolean isChinese){
        Locale locale = isChinese?Locale.CHINESE:Locale.ENGLISH;
        return ArrangeUtils.weekStateToString(
                weeks.stream().map(ArrangeRoom::getWeekNumber).collect(Collectors.toList()), locale);
    }

    private static String simpleBuild(ArrayList<Integer> weekNumbers) {
        if (weekNumbers.size() <=format_threshold) {
            StringBuilder stringBuilder = new StringBuilder().append('[');
            for (Integer week : weekNumbers) {
                stringBuilder.append(week).appendCodePoint(',');
            }
            stringBuilder.deleteCharAt(stringBuilder.length()-1);
            stringBuilder.append(']');
            return stringBuilder.toString();
        }
        return null;
    }

    private static String parse(long weekState,boolean isChinese){
        int[] weekStateProfiles = new int[Constants.MAX_WEEK];
        ArrayList<Integer> weeks = new ArrayList<>();
        boolean isSuccessive = true;
        int prev = -1;
        for (int i = 0;weekState > 0;i++){
            int weekLesson = (int) (weekState & 1);
            weekStateProfiles[i] = weekLesson;
            weekState >>= 1;
            if (weekLesson == 1){
                weeks.add(i);
                if (prev >= 0 && i-prev > 1)
                    isSuccessive = false;
                prev = i;
            }
        }
        if (isSuccessive){
            return String.format("[%s-%s]",weeks.get(0)+1,weeks.get(weeks.size()-1)+1);
        }
        String simpleWeeks = simpleBuild(weeks);
        if (simpleWeeks != null) return simpleWeeks;
        return parse(weekStateProfiles, isChinese);
    }


    private static String parse(int[] weekStateProfiles, boolean isChinese){
        StringBuilder builder = new StringBuilder();
        StringBuilder weekStateBuilder = new StringBuilder();
        for (int i : weekStateProfiles) {
            weekStateBuilder.append(i);
        }
        String weekState = weekStateBuilder.toString();
        Pattern successive = Pattern.compile("(?<!10)1+(?!01)");
        Matcher matcher = successive.matcher(weekState);
        Map<Integer,String> order = new TreeMap<>(); //按周数排序
        // 匹配连续周
        while (matcher.find()) {
            int startWeek = matcher.start() + 1;
            String format = String.format("[%s-%s]", startWeek, startWeek - 1 + matcher.group().length());
            order.put(startWeek,format);
        }
        Pattern interval = Pattern.compile("(10)+1(?!01)");
        matcher = interval.matcher(weekState);
        // 匹配间隔周 单周or双周
        while (matcher.find()) {
            int startWeek = matcher.start() + 1;
            String weekMode;
            if (startWeek%2==0){
                weekMode = isChinese?even:evenEn;
            }else{
                weekMode = isChinese?odd:oddEn;
            }
            int endWeek = startWeek - 1 +  matcher.group().length();
            order.put(startWeek,String.format("[%s-%s%s]", startWeek, endWeek, weekMode));

        }
        for (String s: order.values()) {
            builder.append(s);
        }
        return builder.toString();
    }

    private static class TimeRoomWrapper{
        ArrangeTime arrangeTime;

        // 按不同教室区分的排课周
        Map<String,List<ArrangeRoom>> classroomArrangeWeeks;
    }

    /**
     *  获取中英文字符串 数组下标0中文1英文
     */
    public static String[] formatArrangeInfoBilingual(List<ArrangeTime> times, List<ArrangeRoom> rooms ) {
        TreeMap<String, TimeRoomWrapper> stringTimeRoomWrapperTreeMap = formatArrangeInfo(times, rooms);
        return new String []{buildString(stringTimeRoomWrapperTreeMap,true), buildString(stringTimeRoomWrapperTreeMap,false)};
    }

    public static Map<String,String> formatJsonArrangeInfo(List<ArrangeTime> times, List<ArrangeRoom> rooms ) {
        TreeMap<String, TimeRoomWrapper> stringTimeRoomWrapperTreeMap = formatArrangeInfo(times, rooms);
        return buildJson(stringTimeRoomWrapperTreeMap,true);
    }

    public static String formatArrangeInfo(List<ArrangeTime> times, List<ArrangeRoom> rooms, Locale locale) {
        if (locale == null || Locale.CHINESE.getLanguage().equals(locale.getLanguage())) {
            return buildString(formatArrangeInfo(times,rooms),true);
        }else{
            return buildString(formatArrangeInfo(times,rooms),false);
        }
    }


    private static TreeMap<String, TimeRoomWrapper> formatArrangeInfo(List<ArrangeTime> times, List<ArrangeRoom> rooms){
        HashMap<Long, ArrangeTime> timeRoomMap = new HashMap<>();
        TreeMap<String, TimeRoomWrapper> arrangementRoomMap = new TreeMap<>();
        for (ArrangeTime time : times) {
            timeRoomMap.put(time.getId(),time);
        }
        for (ArrangeRoom room : rooms) {
            ArrangeTime time = timeRoomMap.get(room.getArrangeTimeId());
            if (time == null) {
                throw new NullPointerException("can not build match ArrangeTime when ArrangeTime's Id is:"
                        +room.getArrangeTimeId());
            }
            TimeRoomWrapper arrangements = arrangementRoomMap.computeIfAbsent(getTimeKey(time),
                    key -> {
                        TimeRoomWrapper wrapper = new TimeRoomWrapper();
                        wrapper.arrangeTime = time;
                        wrapper.classroomArrangeWeeks = new HashMap<>();
                        return wrapper;
                    });
            List<ArrangeRoom> arrangeRooms =
                    arrangements.classroomArrangeWeeks.computeIfAbsent(room.getRoomId(), key -> new ArrayList<>());
            arrangeRooms.add(room);
        }
        return arrangementRoomMap;
    }
    private static String buildString(TreeMap<String, TimeRoomWrapper> arrangementRoomMap, boolean isChinese){
        StringBuilder builder = new StringBuilder();
        for (TimeRoomWrapper wrapper : arrangementRoomMap.values()) {
            builder.append(timeToString(wrapper.arrangeTime, isChinese)).append(":\n");
            TreeSet<String> orderWeeks = new TreeSet<>(); // 用于使周能按照从小到大的顺序排序
            orderWeeks(isChinese, wrapper, orderWeeks);
            for (String orderWeek : orderWeeks) {
                builder.append(orderWeek);
            }

        }
        return builder.toString();
    }

    private static Map<String,String> buildJson(TreeMap<String, TimeRoomWrapper> arrangementRoomMap, boolean isChinese){
        Map<String,String> timeRoomMap = new TreeMap<>();
        for (TimeRoomWrapper wrapper : arrangementRoomMap.values()) {
            TreeSet<String> orderWeeks = new TreeSet<>(); // 用于使周能按照从小到大的顺序排序
            orderWeeks(isChinese, wrapper, orderWeeks);
            for (String orderWeek : orderWeeks) {
                String s = timeRoomMap.computeIfAbsent(wrapper.arrangeTime.getId().toString(),key->"");

                timeRoomMap.put(wrapper.arrangeTime.getId().toString() ,s+orderWeek);
            }

        }
        return timeRoomMap;
    }

    private static void orderWeeks(boolean isChinese, TimeRoomWrapper wrapper, TreeSet<String> orderWeeks) {
        for (List<ArrangeRoom> arrangeRooms : wrapper.classroomArrangeWeeks.values()) {
            StringBuilder weeksBuilder = new StringBuilder();
            weeksBuilder.append(parse(arrangeRooms,isChinese));
            // 用roomId属性来维护教室名
            String roomName = arrangeRooms.get(0).getRoomId();
            if (StringUtils.isNotEmpty(roomName)) {
                weeksBuilder.append(" ").append(roomName);
            }
            weeksBuilder.append(" \n");
            orderWeeks.add(weeksBuilder.toString());
        }
    }


    // 获取一个有序的key给treemap使用 依靠字符串编码，使其value能按照周-时间-教室的顺序在treemap中排序，使得最后打印的字符串为有序的
    private static String getTimeKey(ArrangeTime time){
        return String.format("%d%02d%02d",time.getDayOfWeek(),time.getTimeStart(), time.getTimeEnd());
    }
    private static String timeToString(ArrangeTime arrangeTime, boolean isChinese){
        int dayOfWeek = arrangeTime.getDayOfWeek();
        int startTime = arrangeTime.getTimeStart();
        int endTime = arrangeTime.getTimeEnd();
        if (isChinese) {
            String day = weekDay[dayOfWeek - 1];
            return String.format("%s%d-%d节", day, startTime, endTime);
        } else {
            String day = weekDayEn[dayOfWeek - 1];
            return String.format("%s%d-%d", day, startTime, endTime);
        }

    }
    
    public static String parse(Collection<Integer> weekNumbers, Locale locale) {
        return ArrangeUtils.weekStateToString(weekNumbers,locale);
    }

}
