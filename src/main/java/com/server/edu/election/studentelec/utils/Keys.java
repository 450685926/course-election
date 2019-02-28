package com.server.edu.election.studentelec.utils;

public class Keys
{
    /**学生缓存KEY*/
    public static final String STD = "elec-std-";
    
    /**轮次-课程elec-round-%s-course-* 用来查询轮次对应的所有课程*/
    public static final String ROUND_COURSE_PREFIX = "elec-round-%s-course-*";
    /**轮次-课程 elec-round-roundid-course-courseCode */
    public static final String ROUND_COURSE = "elec-round-%s-course-%s";
    
    /**轮次-教学班 elec-round-roundid-class-classid*/
    public static final String ROUND_CLASS = "elec-round-%s-class-%s";
    
    /** 轮次信息 elec-round-%s */
    public static final String ROUND_KEY = "elec-round-%s";
    /** 轮次对应所有的规则KEY */
    public static final String ROUND_RULE_PREFIX = "elec-round-%s-rule-*";
    
    /** 轮次的规则信息 elec-round-%s-rule-%s */
    public static final String ROUND_RULE = "elec-round-%s-rule-%s";
    
    /** 学生状态锁  format参数 roundid studentid*/
    public static final String STD_STATUS_LOCK = "elec-stdlock-%s-%s";
    
    /** 学生选课状态，值为 com.server.edu.election.studentelec.utils.ElecStatus  format参数 roundid studentid*/
    public static final String STD_STATUS = "elec-stdstatus-%s-%s";
    
}
