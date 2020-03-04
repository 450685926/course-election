package com.server.edu.election.studentelec.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;

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
    
    /** 选课失败原因 key为教学班id value为原因说明*/
    private Map<String, String> failedReasons;
    
    private Map<String, String> data;
    
    private Integer isPlan;

    private Integer isLimit;

    /**只允许选重修课*/
    private Integer onlyRetakeFilter;

    /**不允许选重修课*/
    private Integer noRetakeRule;

    private Integer semester;

    private Integer turn;
    
    /**是否留降转*/
    private Integer isDetainedStudent;
    
    /**
     * 是否留学生 0：否  1：是
     */
    private String isOverseas;
    
    public String getIsOverseas() {
		return isOverseas;
	}

	public void setIsOverseas(String isOverseas) {
		this.isOverseas = isOverseas;
	}

	public Integer getIsLimit() {
        return isLimit;
    }

    public void setIsLimit(Integer isLimit) {
        this.isLimit = isLimit;
    }

    public Integer getIsPlan() {
		return isPlan;
	}

	public void setIsPlan(Integer isPlan) {
		this.isPlan = isPlan;
	}

	public ElecRespose()
    {
        
    }
    
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
    
    public Map<String, String> getData()
    {
        if (data == null)
        {
            data = new HashMap<>();
        }
        return data;
    }
    
    public void setData(Map<String, String> data)
    {
        this.data = data;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public Integer getTurn() {
        return turn;
    }

    public void setTurn(Integer turn) {
        this.turn = turn;
    }

    public Integer getIsDetainedStudent() {
        return isDetainedStudent;
    }

    public void setIsDetainedStudent(Integer isDetainedStudent) {
        this.isDetainedStudent = isDetainedStudent;
    }

    public Integer getOnlyRetakeFilter() {
        return onlyRetakeFilter;
    }

    public void setOnlyRetakeFilter(Integer onlyRetakeFilter) {
        this.onlyRetakeFilter = onlyRetakeFilter;
    }

    public Integer getNoRetakeRule() {
        return noRetakeRule;
    }

    public void setNoRetakeRule(Integer noRetakeRule) {
        this.noRetakeRule = noRetakeRule;
    }
}
