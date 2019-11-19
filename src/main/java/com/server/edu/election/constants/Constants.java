package com.server.edu.election.constants;

public interface Constants
{
	 public static final int ZERO = 0;
	 public static final int ONE = 1;
	 public static final int TOW = 2;
	 public static final int THREE = 3;
	 public static final String EN_AUDIT = "EN_AUDIT";
	 public static final String CREATE = "CREATE";
	 /**普通班*/
	 public static final String ORDINARY_CALSS = "1";
	 /**重修班*/
	 public static final String REBUILD_CALSS = "2";
	 /**留学生标识*/
	 public static final String IS_OVERSEAS = "1";
	 
	 public static final String STU = "STU";
	 public static final String DEPART_ADMIN = "DEPART_ADMIN";
	 public static final String MANAGER_ADMIN = "MANAGER_ADMIN";
	 public static final int PRACTICE_COURSE_ = 2;
	 public static final int IS_OPEN = 1;
	 /**申请*/
	 public static final int APPIY = 1;
	 /**审批*/
	 public static final int APPROVAL = 2;
	 /**部门Id 1 本科 2 普通研究生 3 在职研究生*/
	 public static final String PROJ_UNGRADUATE="1";
	 public static final String PROJ_GRADUATE="2";
	 public static final String PROJ_LINE_GRADUATE="4";
	 
	 public static final int TEACHER_DEFAULT=0;
	 
	 public static final Integer NORMAL_MODEL=1;
	 public static final Integer PE_MODEL=2;
	 public static final Integer ENGLISH_MODEL=3;
	 
    /**
     * 选课轮次
     */
    public static final int FIRST_TURN = 1;
    public static final int SECOND_TURN = 2;
    public static final int THIRD_TURN = 3;
    public static final int FOURTH_TURN = 4;
    
    public static final int HUNDRED = 100;
    
    public static final double NEW_LIMIT_CREDITS = 48;
    
    public static final double TOTAL_LIMIT_CREDITS = 48;
    
    public static final int REBUILD_LIMIT_NUMBER = 6;
    
    /** 数据库逻辑删除标识  1-已删除;0-未删除 */
    public static final Integer DELETE_TRUE = 1;
    public static final Integer DELETE_FALSE = 0;
    
    public static final Integer IS = 1;
    
    /** 已缴费 */
    public static final Integer PAID = 1;
    
    /** 未缴费 */
    public static final Integer UN_PAID = 0;
    /** 优*/
    public static final String EXCELLENT = "优";
    
    public static final Integer RECYCLETYPE = 1;
    
    public static final Integer AUTOTYPE = 2;
    
    /** 2018级及以后学分门数限制   年级 */
    public static final Integer GRADE = 2018;
    

}
