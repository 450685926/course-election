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

    /**轮次上一学期 elec-round-%s-preSemester-*/
    public static final String ROUND_PRESEMESTER="elec-round-%s-preSemester";
    
}
