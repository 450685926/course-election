package com.server.edu.election.dto;

import com.server.edu.election.entity.ElcMedWithdraw;

public class ElcMedWithdrawDto extends ElcMedWithdraw {
	private static final long serialVersionUID = 1L;
	private Integer mode;

	@Override
	public Integer getMode() {
		return mode;
	}

	@Override
	public void setMode(Integer mode) {
		this.mode = mode;
	}
	
	
}
