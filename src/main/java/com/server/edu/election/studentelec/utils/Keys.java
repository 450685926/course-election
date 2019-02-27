package com.server.edu.election.studentelec.utils;

public class Keys
{
    /**学生缓存KEY*/
    public static final String STD = "elec-std-";
    
    /**轮次-课程 elec-class-[roundid]-前缀用来查询轮次对应的所有课程*/
    public static final String ROUND_COURSE_PREFIX = "elec-course-[%s]-";
    /**轮次-课程 elec-class-[roundid]-[courseCode]*/
    public static final String ROUND_COURSE = "elec-course-[%s]-[%s]";
    
    /**轮次-教学班 elec-class-[roundid]-[classid]*/
    public static final String ROUND_CLASS = "elec-class-[%s]-[%s]";
    
    /** 所有轮次的信息 */
    public static final String ROUND_KEY = "elec-rounds";
    
    /** 学生状态锁  format参数 roundid studentid*/
    public static final String STD_STATUS_LOCK = "elec-stdlock-%s-%s";
    
    /** 学生选课状态，值为 com.server.edu.election.studentelec.utils.ElecStatus  format参数 roundid studentid*/
    public static final String STD_STATUS = "elec-stdstatus-%s-%s";
    
}
