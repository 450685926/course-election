package com.server.edu.election.studentelec.context;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;

import org.hibernate.validator.constraints.NotBlank;

import com.server.edu.election.studentelec.dto.ElecTeachClassDto;
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
    
    @NotBlank(groups = {AgentElcGroup.class})
    private String studentId;
    
    /** 选课集合 教学班ID*/
    private List<ElecTeachClassDto> elecClassList;
    
    /**退课集合 教学班ID*/
    private List<ElecTeachClassDto> withdrawClassList;
    
    private String createBy;
    
    private String requestIp;
    
    /** 学年学期 */
    private Long calendarId;
    
    /**管理部门*/
    private String projectId;
    
    public String getStudentId()
    {
        return studentId;
    }
    
    public void setStudentId(String studentId)
    {
        this.studentId = studentId;
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
    
    public List<ElecTeachClassDto> getElecClassList()
    {
        return elecClassList;
    }
    
    public void setElecClassList(List<ElecTeachClassDto> elecClassList)
    {
        this.elecClassList = elecClassList;
    }
    
    public List<ElecTeachClassDto> getWithdrawClassList()
    {
        return withdrawClassList;
    }
    
    public void setWithdrawClassList(List<ElecTeachClassDto> withdrawClassList)
    {
        this.withdrawClassList = withdrawClassList;
    }
    
    public String getCreateBy()
    {
        return createBy;
    }
    
    public void setCreateBy(String createBy)
    {
        this.createBy = createBy;
    }
    
    public String getRequestIp()
    {
        return requestIp;
    }
    
    public void setRequestIp(String requestIp)
    {
        this.requestIp = requestIp;
    }

	public Long getCalendarId() {
		return calendarId;
	}

	public void setCalendarId(Long calendarId) {
		this.calendarId = calendarId;
	}

    public String getProjectId()
    {
        return projectId;
    }

    public void setProjectId(String projectId)
    {
        this.projectId = projectId;
    }
    
}
