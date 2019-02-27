package com.server.edu.election.studentelec.utils;

public class Keys
{
    /**学生缓存KEY*/
    public static final String STD = "elec-std-";
    
    /**轮次-课程 elec-class-[roundid]-前缀用来查询轮次对应的所有课程*/
    public static final String ROUND_COURSE_PREFIX = "elec-course-[%s]-*";
    /**轮次-课程 elec-class-[roundid]-[courseCode]*/
    public static final String ROUND_COURSE = "elec-course-[%s]-[%s]";
    
    /**轮次-教学班 elec-class-[roundid]-[classid]*/
    public static final String ROUND_CLASS = "elec-class-[%s]-[%s]";
    
    /** 轮次信息 elec-rounds-%s */
    public static final String ROUND_KEY = "elec-rounds-%s";
    
    /** 轮次的规则信息 elec-rounds-%s-rule-%s */
    public static final String ROUND_RULE = "elec-rounds-%s-rule-%s";
    /** 轮次对应所有的规则KEY */
    public static final String ROUND_RULE_PREFIX = "elec-rounds-%s-rule-*";
    
    /** 学生状态锁  format参数 roundid studentid*/
    public static final String STD_STATUS_LOCK = "elec-stdlock-%s-%s";
    
    /** 学生选课状态，值为 com.server.edu.election.studentelec.utils.ElecStatus  format参数 roundid studentid*/
    public static final String STD_STATUS = "elec-stdstatus-%s-%s";
    
}
