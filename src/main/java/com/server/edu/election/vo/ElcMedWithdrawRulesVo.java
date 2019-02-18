package com.server.edu.election.vo;

import com.server.edu.election.entity.ElcMedWithdrawRules;

public class ElcMedWithdrawRulesVo extends ElcMedWithdrawRules {
    private static final long serialVersionUID = 1L;
    private Long beginTimeL;
    private Long endTimeL;
	public Long getBeginTimeL() {
		return beginTimeL;
	}
	public void setBeginTimeL(Long beginTimeL) {
		this.beginTimeL = beginTimeL;
	}
	public Long getEndTimeL() {
		return endTimeL;
	}
	public void setEndTimeL(Long endTimeL) {
		this.endTimeL = endTimeL;
	}
    
    
}
