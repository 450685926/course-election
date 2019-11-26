package com.server.edu.exam.constants;

/**
 * @description: 研究生补缓考申请状态
 * @author: bear
 * @create: 2019-08-29 11:12
 */
public class ApplyStatus {
    /**审核状态 0 待审核 1 学院审核未通过 2 学院审核通过 3学校审核未通过 4学校审核通过*/
    public static final Integer NOT_EXAMINE = 0;
    public static final Integer COLLEGE_EXAMINE_NOT_PASS = 1;
    public static final Integer COLLEGE_EXAMINE_PASS = 2;
    public static final Integer SCHOOL_EXAMINE_NOT_PASS = 3;
    public static final Integer SCHOOL_EXAMINE_PASS = 4;

    /**申请来源 1 学生申请 2 代申请*/
    public static final Integer APPLY_SOURCE_MYSELF = 1;
    public static final Integer APPLY_SOURCE_OTHER = 2;

    /**常数*/
    public static final String CONSTANTS_STRING = "1";
    public static final Integer CONSTANTS_INT = 2;
    public static final Integer PASS_INT = 1;
    public static final Integer NOT_PASS_INT = 0;
    public static final String TEACHER_ROLE = "2";

    /**考试类型 1 期末考试 2 补缓考*/
    public static final Integer FINAL_EXAM = 1;
    public static final Integer MAKE_UP_EXAM = 2;

    /**考试情况 1 正常 2 缓考 3 补考 4 重修 5旷考*/
    public static final Integer EXAM_SITUATION_NORMAL = 1;
    public static final Integer EXAM_SITUATION_SLOW = 2;
    public static final Integer EXAM_SITUATION_MAKE_UP = 3;
    public static final Integer EXAM_SITUATION_REBUILD = 4;
    public static final Integer EXAM_SITUATION_DEFECT = 5;

    /**排考日志 1 排考 0 退考*/
    public static final Integer EXAM_LOG_YES = 1;
    public static final Integer EXAM_LOG_NO = 0;

    /** 1 加 0 减*/
    public static final Integer EXAM_ADD = 1;
    public static final Integer EXAM_REDUCE = 0;

}
