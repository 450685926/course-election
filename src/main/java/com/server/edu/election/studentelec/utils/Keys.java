package com.server.edu.election.studentelec.utils;

public class Keys
{
    private Keys()
    {
    }
    
    /**学生缓存KEY*/
    public static final String STD = "elec-std-";
    
    /** 学生状态锁  format参数 roundid studentid*/
    public static final String STD_STATUS_LOCK = "elec-stdlock-%s_%s";
    
    /** 学生选课状态，值为 com.server.edu.election.studentelec.utils.ElecStatus  format参数 roundid studentid*/
    public static final String STD_STATUS = "elec-stdstatus-%s_%s";
    
    /**轮次的redis key elec-roundData1*/
    public static String getRoundKey()
    {
        return String.format("elec-roundData1");
    }
    
    /**轮次条件 elec-roundCondition-[roundid] */
    public static String getRoundConditionOne(Long roundId)
    {
        return String.format("elec-roundCondition-%s", roundId);
    }
    
    /**轮次规则Key*/
    public static String getRoundRuleKey(Long roundId)
    {
        return String.format("elec-roundRule-%s", roundId);
    }
    
    /**轮次学生Key*/
    public static String getRoundStuKey(Long roundId)
    {
        return String.format("elec-roundStudent-%s", roundId);
    }
    
    /**轮次课程key*/
    public static String getRoundCourseKey(Long roundId)
    {
        return String.format("elec-roundCourse-%s", roundId);
    }
    
    /** 研究生管理员代理选课存储学年学期课程 elec-calendarId-%s-course*/
    public static String getCalendarCourseKey(Long calendarId)
    {
        return String.format("elec-calendarId-%s-course", calendarId);
    }
    
    /**教学班key elec-teachClass1*/
    public static String getClassKey()
    {
        return String.format("elec-teachClass1");
    }
    
    /**教学班选课人数Key elec-classNum1*/
    public static String getClassElecNumberKey()
    {
        return String.format("elec-classNum1");
    }
    
    /**第三、四轮退课人数key elec-witdthDrawNum1*/
    public static String getWitdthDrawNumberKey()
    {
        return String.format("elec-witdthDrawNum1");
    }
    
    /**轮次规则Key*/
    public static String getRuleKey()
    {
        return "elec-rules";
    }
    
    /**选课申请管理课程 elec-calendarId-%s-applyCourse*/
    public static String getApplyCourseKey(Long calendarId)
    {
        return String.format("elec-calendarId-%s-applyCourse", calendarId);
    }
    
    /**上个学期*/
    public static String getRoundPresemesterKey(Long roundId)
    {
        return String.format("elec-roundPreSemester-%s", roundId);
    }
    
    /** 公选课课程代码*/
    public static String getPublicCourseKey()
    {
        return "elec-PublicCourse";
    }
    
}
