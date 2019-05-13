package com.server.edu.election.studentelec.utils;

public class Keys
{
    private Keys()
    {
    }
    
    /**学生缓存KEY*/
    public static final String STD = "elec-std-";
    
    /**轮次-课程 elec-roundCourse-[roundid]_[courseCode] */
    public static final String ROUND_COURSE = "elec-roundCourse-%s_%s";
    
    /**学期-教学班 elec-teachClass-[classid]*/
    public static final String CALENDAR_CLASS = "elec-teachClass-%s";
    
    /** 教学班选课人数elec-classNum-[count] */
    public static final String ROUND_CLASS_NUM = "elec-classNum-%s";
    
    /** 轮次信息 elec-rounData-[roundId] */
    public static final String ROUND_KEY = "elec-rounData-%s";
    
    /** 轮次的规则信息 elec-roundRule-[rountid] */
    public static final String ROUND_RULE = "elec-roundRule-%s";
    
    /** 学生状态锁  format参数 roundid studentid*/
    public static final String STD_STATUS_LOCK = "elec-stdlock-%s_%s";
    
    public static final String STD_STATUS_LOCK_PATTERN = "elec-stdlock-*";
    
    /** 学生选课状态，值为 com.server.edu.election.studentelec.utils.ElecStatus  format参数 roundid studentid*/
    public static final String STD_STATUS = "elec-stdstatus-%s_%s";
    
    /**轮次学生 elec-roundStudent-%s */
    public static final String ROUND_STUDENT = "elec-roundStudent-%s";
    
    /**选课申请管理课程 elec-calendarId-%s-applyCourse*/
    public static final String APPLY_COURSE = "elec-calendarId-%s-applyCourse";
    
    /**轮次上一学期 elec-roundPreSemester-%s */
    public static final String ROUND_PRESEMESTER = "elec-roundPreSemester-%s";
    
    /**轮次条件 elec-roundCondition-[roundid] */
    public static final String ROUND_CONDITION = "elec-roundCondition-%s";
    
    /**轮次缓存KEY*/
    public static final String RULES = "elec-rules";
    
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
    
    /**轮次条件k round-condition* */
    public static String getRoundConditionOne(Long roundId)
    {
        return String.format(Keys.ROUND_CONDITION, roundId);
    }
    
    /**轮次规则Key*/
    public static String getRoundRuleKey(Long roundId)
    {
        return String.format(Keys.ROUND_RULE, roundId);
    }
    
    /**轮次学生Key*/
    public static String getRoundStuKey(Long roundId)
    {
        return String.format(Keys.ROUND_STUDENT, roundId);
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
    
    /**教学班key*/
    public static String getClassKey(Long teachClassId)
    {
        return String.format(Keys.CALENDAR_CLASS, teachClassId);
    }
    
    /**教学班选课人数Key*/
    public static String getClassElecNumberKey(Long teachClassId)
    {
        return String.format(Keys.ROUND_CLASS_NUM, teachClassId);
    }
    
    /**轮次规则Key*/
    public static String getRuleKey()
    {
        return Keys.RULES;
    }
}
