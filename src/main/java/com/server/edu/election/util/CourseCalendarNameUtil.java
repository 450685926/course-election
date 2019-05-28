package com.server.edu.election.util;
import com.server.edu.common.locale.I18nUtil;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 课程学期转化
 * @author: bear
 * @create: 2019-04-28 10:40
 */
public class CourseCalendarNameUtil {

    public static String getCalendarName(Integer grade,String semester){
        if(StringUtils.isBlank(semester)){
            return "";
        }
        String[] semes = semester.split(",");
        List<String> list=new ArrayList<>();
        for (String s : semes) {
            int  i= Integer.parseInt(s);
            int num=(i-1)/2;
            int geadrStart=grade+num;
            if(i%2==0){
                String calendarName = I18nUtil.getMsg("election.calendarName",
                        geadrStart+"",
                        geadrStart + 1+"",
                        2);
                list.add(calendarName);
            }else{
                String calendarName = I18nUtil.getMsg("election.calendarName",
                        geadrStart+"",
                        geadrStart + 1+"",
                        1);
                list.add(calendarName);
            }
        }
        String s = String.join(",",list);
        return s;
    }

}
