package com.server.edu.election.vo;

import java.util.List;

import com.server.edu.election.entity.ElectionRounds;

public class ElectionRoundsVo extends ElectionRounds
{
    private static final long serialVersionUID = 1L;
    
    private List<ElectionRuleVo> ruleVos;
    
    /**
     * 可选课程数量
     */
    private Integer elcCourseNum;

    private String grades;
    
    
    public ElectionRoundsVo()
    {
        
    }
    
    public ElectionRoundsVo(ElectionRounds round)
    {
        this.setBeginTime(round.getBeginTime());
        this.setCalendarId(round.getCalendarId());
        this.setCreatedAt(round.getCreatedAt());
        this.setElectionObj(round.getElectionObj());
        this.setEndTime(round.getEndTime());
        this.setId(round.getId());
        this.setMode(round.getMode());
        this.setName(round.getName());
        this.setOpenFlag(round.getOpenFlag());
        this.setProjectId(round.getProjectId());
        this.setRemark(round.getRemark());
        this.setTurn(round.getTurn());
        this.setCalendarName(round.getCalendarName());
    }
    
    public List<ElectionRuleVo> getRuleVos()
    {
        return ruleVos;
    }
    
    public void setRuleVos(List<ElectionRuleVo> ruleVos)
    {
        this.ruleVos = ruleVos;
    }

	public Integer getElcCourseNum() {
		return elcCourseNum;
	}

	public void setElcCourseNum(Integer elcCourseNum) {
		this.elcCourseNum = elcCourseNum;
	}

	public String getGrades() {
		return grades;
	}

	public void setGrades(String grades) {
		this.grades = grades;
	}
    
}
