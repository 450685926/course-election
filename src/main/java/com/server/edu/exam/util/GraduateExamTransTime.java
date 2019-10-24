package com.server.edu.exam.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @description: 显示研究生排考时间
 * @author: bear
 * @create: 2019-09-25 14:48
 */
public class GraduateExamTransTime {

    public static String transTime(Date examDate,String examStartTime,String examEndTime,Integer weekNumber,Integer weekDay){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String format = simpleDateFormat.format(examDate);
        String weekDayStr = transWeek(weekDay);
        return format + " 第" + weekNumber + "周 " + weekDayStr + " "+ examStartTime + "-" + examEndTime;
    }

    public static String transWeek(Integer weekDay){
        String weekDayStr = "";
        switch (weekDay){
            case 1:
                weekDayStr = "星期一";
                break;
            case 2:
                weekDayStr = "星期二";
                break;
            case 3:
                weekDayStr = "星期三";
                break;
            case 4:
                weekDayStr = "星期四";
                break;
            case 5:
                weekDayStr = "星期五";
                break;
            case 6:
                weekDayStr = "星期六";
                break;
            case 7:
                weekDayStr = "星期日";
                break;
            default:
                weekDayStr = "";
                break;
        }
        return weekDayStr;
    }
}
