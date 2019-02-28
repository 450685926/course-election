package com.server.edu.election.studentelec.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.server.edu.election.studentelec.utils.ElecStatus;

/**
 * 学生选课返回<br>
 * 两个用途<br>
 * 1. 登录选课界面时预加载数据的请求，只需要studentId status，前端会定时执行请求直到status变为ready 即加载完成
 * 2. 选课，两个接口
 *  2.1. 选课请求,选课时发送一次，此时应该返回status=processing
 *  2.2  获取选课结果的请求 未完成时status为processing， 前端会定时执行请求直到status变为ready，此时应返回所有选课结果
 */
public class ElecRespose
{
    private ElecStatus status;
    
    /** 选课成功集合 教学班id*/
    private List<Long> successCourses;
    
    /** 选课失败集合 教学班id*/
    private List<Long> failedCourses;
    
    /** 选课失败原因 key为教学班id value为原因说明*/
    private Map<String, String> failedReasons;
    
    public ElecRespose(ElecStatus status)
    {
        this.status = status;
    }
    
    public List<Long> getSuccessCourses()
    {
        if (successCourses == null)
        {
            successCourses = new ArrayList<>();
        }
        return successCourses;
    }
    
    public void setSuccessCourses(List<Long> successCourses)
    {
        this.successCourses = successCourses;
    }
    
    public List<Long> getFailedCourses()
    {
        if (failedCourses == null)
        {
            failedCourses = new ArrayList<>();
        }
        return failedCourses;
    }
    
    public void setFailedCourses(List<Long> failedCourses)
    {
        this.failedCourses = failedCourses;
    }
    
    public Map<String, String> getFailedReasons()
    {
        if (failedReasons == null)
        {
            failedReasons = new HashMap<>();
        }
        return failedReasons;
    }
    
    public void setFailedReasons(Map<String, String> failedReasons)
    {
        this.failedReasons = failedReasons;
    }
    
    public ElecStatus getStatus()
    {
        return status;
    }
    
    public void setStatus(ElecStatus status)
    {
        this.status = status;
    }
}
