package com.server.edu.election.util;

public class WeekUtil {

    /**
     *@Description: 星期
     *@Param:
     *@return:
     *@Author: bear
     *@date: 2019/2/15 13:59
     */
    public static String findWeek(Integer number){
        String week="";
        switch(number){
            case 1:
                week="星期一";
                break;
            case 2:
                week="星期二";
                break;
            case 3:
                week="星期三";
                break;
            case 4:
                week="星期四";
                break;
            case 5:
                week="星期五";
                break;
            case 6:
                week="星期六";
                break;
            case 7:
                week="星期日";
                break;
        }
        return week;
    }
}
