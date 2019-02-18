package com.server.edu.election.studentelec.utils;

public class Keys {

    /** 所有轮次的信息 */
    public static final String ROUND_KEY="elecrounds";

    /** 学生状态锁  format参数 roundid studentid*/
    public static final String STD_STATUS_LOCK ="elec-stdlock-%s-%s";
    /** 学生选课状态，值为 com.server.edu.election.studentelec.utils.ElecStatus  format参数 roundid studentid*/
    public static final String STD_STATUS ="elec-stdstatus-%s-%s";

}
