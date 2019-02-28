package com.server.edu.election.studentelec.context;

import java.util.List;

/**
 * 学生选课请求<br>
 * 两个用途<br>
 * 1. 登录选课界面时预加载数据的请求，只需要 roundId和studentId
 * 2. 选课请求 要包括选择的教学班
 */
public class ElecRequest
{
    private Long roundId;
    
    private String studentId;
    
    /** 选课集合 教学班ID*/
    private List<Long> elecTeachingClasses;
    
    public String getStudentId()
    {
        return studentId;
    }
    
    public void setStudentId(String studentId)
    {
        this.studentId = studentId;
    }
    
    public List<Long> getElecTeachingClasses()
    {
        return elecTeachingClasses;
    }
    
    public void setElecTeachingClasses(List<Long> elecTeachingClasses)
    {
        this.elecTeachingClasses = elecTeachingClasses;
    }
    
    public Long getRoundId()
    {
        return roundId;
    }
    
    public void setRoundId(Long roundId)
    {
        this.roundId = roundId;
    }
}
