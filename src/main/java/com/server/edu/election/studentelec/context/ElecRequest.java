package com.server.edu.election.studentelec.context;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;

import com.server.edu.election.validate.AgentElcGroup;

/**
 * 学生选课请求<br>
 * 两个用途<br>
 * 1. 登录选课界面时预加载数据的请求，只需要 roundId和studentId
 * 2. 选课请求 要包括选择的教学班
 */
public class ElecRequest
{
    @NotNull(groups = {AgentElcGroup.class, Default.class})
    private Long roundId;
    
    /**
     * 选课对象(1学生，2教务员，3管理员)
     */
    private Integer chooseObj;
    
    @NotNull(groups = {AgentElcGroup.class})
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
    
    public Integer getChooseObj()
    {
        return chooseObj;
    }
    
    public void setChooseObj(Integer chooseObj)
    {
        this.chooseObj = chooseObj;
    }
}
