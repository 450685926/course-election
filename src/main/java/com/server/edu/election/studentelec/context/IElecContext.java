package com.server.edu.election.studentelec.context;

/**
 * 执行“学生选课请求”时的上下文环境，组装成本对象，供各种约束调用
 *
 */
public interface IElecContext
{
    /** 免修申请课程 */
    public static final String APPLY_FOR_DROP_COURSES = "ApplyForDropCourses";
    /** 个人替代课程 */
    public static final String REPLACE_COURSES = "replaceCourses";
    /** 本学期已选择课程 */
    public static final String SELECTED_COURSES = "SelectedCourses";

    /**
     * 保存到redis中
     * 
     */
    public abstract void saveToCache();
    
    public abstract void saveResponse();
    
    public abstract void clear();
    
    public abstract void setRequest(ElecRequest request);
    
    public abstract ElecRespose getRespose();
    
    public Long getCalendarId();
    
}
