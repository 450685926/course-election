package com.server.edu.election.studentelec.utils;

public class Keys
{
    private Keys()
    {
    }
    
    /**学生缓存KEY*/
    public static final String STD = "elec-std-";
    
    /**轮次-课程 elec-round-roundid-course-courseCode */
    public static final String ROUND_COURSE = "elec-round-%s-course-%s";
    
    /**学期-教学班 elec-calendar-[calendarId]-class-[classid]*/
    public static final String CALENDAR_CLASS = "elec-class-%s";
    
    /** 教学班选课人数elec-round-class-num-%s */
    public static final String ROUND_CLASS_NUM = "elec-class-num-%s";
    
    /** 轮次信息 elec-rounData-%s */
    public static final String ROUND_KEY = "elec-rounData-%s";
    
    /** 轮次的规则信息 elec-round-%s-rule-%s */
    public static final String ROUND_RULE = "elec-round-%s-rule-%s";
    
    /** 学生状态锁  format参数 roundid studentid*/
    public static final String STD_STATUS_LOCK = "elec-stdlock-%s-%s";
    
    public static final String STD_STATUS_LOCK_PATTERN = "elec-stdlock-*";
    
    /** 学生选课状态，值为 com.server.edu.election.studentelec.utils.ElecStatus  format参数 roundid studentid*/
    public static final String STD_STATUS = "elec-stdstatus-%s-%s";
    
    /**轮次学生 elec-round-%s-student-%s*/
    public static final String ROUND_STUDENT = "elec-round-%s-student-%s";
    
    /**轮次的redis pattern elec-rounData-* */
    public static String getRoundKeyPattern()
    {
        return String.format(Keys.ROUND_KEY, "*");
    }
    
    /**轮次的redis key*/
    public static String getRoundKey(Long roundId)
    {
        return String.format(Keys.ROUND_KEY, roundId);
    }
    
    /**轮次规则Key pattern*/
    public static String getRoundRuleKeyPattern(Long roundId)
    {
        return String.format(Keys.ROUND_RULE, roundId, "*");
    }
    
    /**轮次规则Key*/
    public static String getRoundRuleKey(Long roundId, String serviceName)
    {
        return String.format(Keys.ROUND_RULE, roundId, serviceName);
    }
    
    /**轮次学生Key pattern*/
    public static String getRoundStuPattern(Long roundId)
    {
        return String.format(Keys.ROUND_STUDENT, roundId, "*");
    }
    
    /**轮次学生Key*/
    public static String getRoundStuKey(Long roundId, String studentId)
    {
        return String.format(Keys.ROUND_STUDENT, roundId, studentId);
    }
    
    /**轮次课程key pattern*/
    public static String getRoundCoursePattern(Long roundId)
    {
        return String.format(Keys.ROUND_COURSE, roundId, "*");
    }
    
    /**轮次课程key*/
    public static String getRoundCourseKey(Long roundId, String courseCode)
    {
        return String.format(Keys.ROUND_COURSE, roundId, courseCode);
    }
    
    /**教学班key pattern*/
    public static String getClassKeyPattern(Long calendarId)
    {
        return String.format(Keys.CALENDAR_CLASS, calendarId, "*");
    }
    
    /**教学班key*/
    public static String getClassKey(Long calendarId, Long teachClassId)
    {
        return String.format(Keys.CALENDAR_CLASS, calendarId, teachClassId);
    }
    
    /**教学班选课人数Key*/
    public static String getClassElecNumberKey(Long teachClassId)
    {
        return String.format(Keys.ROUND_CLASS_NUM, teachClassId);
    }
}
