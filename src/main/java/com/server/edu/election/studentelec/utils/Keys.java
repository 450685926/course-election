package com.server.edu.election.studentelec.utils;

public class Keys
{
    /**学生缓存KEY*/
    public static final String STD = "elec-std-";
    
    /**轮次-课程 elec-round-roundid-course-courseCode */
    public static final String ROUND_COURSE = "elec-round-%s-course-%s";
    
    /**轮次-教学班 elec-round-roundid-class-classid*/
    public static final String ROUND_CLASS = "elec-round-%s-class-%s";
    /** 教学班选课人数elec-round-class-num-%s */
    public static final String ROUND_CLASS_NUM = "elec-round-class-num-%s";
    
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
    /**选课申请管理课程 elec-calendarId-%s-applyCourse-%s*/
    public static final String APPLY_COURSE = "elec-calendarId-%s-applyCourse";

    /**轮次上一学期 elec-round-%s-preSemester-*/
    public static final String ROUND_PRESEMESTER="elec-round-%s-preSemester";
    
    /**轮次条件 elec-round-%s-condition-*/
    public static final String ROUND_CONDITION="elec-round-%s-condition";
    
    /**轮次的redis pattern elec-rounData-* */
    public static String getRoundKeyPattern()
    {
        return String.format(Keys.ROUND_KEY, "*");
    }
    
    /**轮次条件redis round-condition* */
    public static String getRoundCondition()
    {
        return String.format(Keys.ROUND_CONDITION, "*");
    }
    
    /**轮次条件k round-condition* */
    public static String getRoundConditionOne(Long roundId)
    {
        return String.format(Keys.ROUND_CONDITION, roundId);
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
    
//    /**教学班key pattern*/
//    public static String getClassKeyPattern(Long calendarId)
//    {
//        return String.format(Keys.CALENDAR_CLASS, calendarId, "*");
//    }
//    
//    /**教学班key*/
//    public static String getClassKey(Long calendarId, Long teachClassId)
//    {
//        return String.format(Keys.CALENDAR_CLASS, calendarId, teachClassId);
//    }
    
}
