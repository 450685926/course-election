package com.server.edu.election.studentelec.context;

/**
 * 执行“学生选课请求”时的上下文环境，组装成本对象，供各种约束调用
 *
 */
public interface IElecContext
{

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
