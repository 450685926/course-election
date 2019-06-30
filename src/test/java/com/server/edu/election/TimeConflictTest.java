package com.server.edu.election;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.server.edu.election.studentelec.context.ClassTimeUnit;
import com.server.edu.election.studentelec.context.SelectedCourse;
import com.server.edu.election.studentelec.rules.bk.TimeConflictCheckerRule;

public class TimeConflictTest
{
    public static void main(String[] args)
    {
        String text = "[{\"courseCode\":\"10002810063\",\"courseName\":\"普通物理(B)上\",\"credits\":3.0,\"nameEn\":\"General Physics(B)(1)\",\"campus\":\"1\",\"publicElec\":false,\"calendarName\":null,\"electionApplyId\":null,\"apply\":null,\"teachClassId\":111111112454667,\"teachClassCode\":\"1000281006301\",\"teachClassType\":\"1\",\"maxNumber\":120,\"currentNumber\":0,\"times\":[{\"teachClassId\":111111112454667,\"arrangeTimeId\":2947213,\"timeStart\":7,\"timeEnd\":8,\"dayOfWeek\":1,\"weeks\":[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17],\"value\":\"普通物理(B)上(10002810063) null\",\"teacherCode\":\"83055\"},{\"teachClassId\":111111112454667,\"arrangeTimeId\":2947214,\"timeStart\":7,\"timeEnd\":8,\"dayOfWeek\":4,\"weeks\":[2,4,6,8,10,12,14,16],\"value\":\"普通物理(B)上(10002810063) null\",\"teacherCode\":\"83055\"}],\"teacherCode\":null,\"teacherName\":null,\"chooseObj\":3,\"courseTakeType\":1,\"turn\":1,\"isApply\":null,\"practice\":false,\"retraining\":false},{\"courseCode\":\"10002800041\",\"courseName\":\"高等数学(B)下\",\"credits\":5.0,\"nameEn\":\"Advanced Mathematics(B)2\",\"campus\":\"1\",\"publicElec\":false,\"calendarName\":null,\"electionApplyId\":null,\"apply\":null,\"teachClassId\":111111112454679,\"teachClassCode\":\"1000280004101\",\"teachClassType\":\"1\",\"maxNumber\":136,\"currentNumber\":0,\"times\":[{\"teachClassId\":111111112454679,\"arrangeTimeId\":2942207,\"timeStart\":1,\"timeEnd\":2,\"dayOfWeek\":2,\"weeks\":[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17],\"value\":\"高等数学(B)下(10002800041) null\",\"teacherCode\":\"84044\"},{\"teachClassId\":111111112454679,\"arrangeTimeId\":2942208,\"timeStart\":1,\"timeEnd\":2,\"dayOfWeek\":1,\"weeks\":[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17],\"value\":\"高等数学(B)下(10002800041) null\",\"teacherCode\":\"84044\"},{\"teachClassId\":111111112454679,\"arrangeTimeId\":2942209,\"timeStart\":1,\"timeEnd\":2,\"dayOfWeek\":5,\"weeks\":[2,4,6,8,10,12,14,16],\"value\":\"高等数学(B)下(10002800041) null\",\"teacherCode\":\"84044\"}],\"teacherCode\":null,\"teacherName\":null,\"chooseObj\":3,\"courseTakeType\":1,\"turn\":1,\"isApply\":null,\"practice\":false,\"retraining\":false},{\"courseCode\":\"10002440881\",\"courseName\":\"C/C++程序设计\",\"credits\":2.5,\"nameEn\":\"C/C++ Programming\",\"campus\":\"1\",\"publicElec\":false,\"calendarName\":null,\"electionApplyId\":null,\"apply\":null,\"teachClassId\":111111112455560,\"teachClassCode\":\"1000244088114\",\"teachClassType\":\"1\",\"maxNumber\":120,\"currentNumber\":1,\"times\":[{\"teachClassId\":111111112455560,\"arrangeTimeId\":2947426,\"timeStart\":7,\"timeEnd\":8,\"dayOfWeek\":3,\"weeks\":[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17],\"value\":\"C/C++程序设计(10002440881) null\",\"teacherCode\":\"07110\"},{\"teachClassId\":111111112455560,\"arrangeTimeId\":2947427,\"timeStart\":1,\"timeEnd\":2,\"dayOfWeek\":4,\"weeks\":[1,3,5,7,9,11,13,15,17],\"value\":\"C/C++程序设计(10002440881) null\",\"teacherCode\":\"07110\"}],\"teacherCode\":null,\"teacherName\":null,\"chooseObj\":3,\"courseTakeType\":1,\"turn\":1,\"isApply\":null,\"practice\":false,\"retraining\":false}]";
        List<SelectedCourse> list = JSON.parseArray(text, SelectedCourse.class);
        SelectedCourse course1 = list.get(0);
        SelectedCourse course2 = list.get(1);
        course1.getTimes();
        List<ClassTimeUnit> times = course1.getTimes();
        for (ClassTimeUnit v0 : course2.getTimes())
        {
            for (ClassTimeUnit v1 : times)
            {
                if (TimeConflictCheckerRule.conflict(v0, v1))
                {
                    System.out.println("true");
                }
            }
        }
    }
}
