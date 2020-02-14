package com.server.edu.election.dto;

import javax.validation.constraints.NotNull;

public class ElecRoundStuDto {
	@NotNull
	private Integer mode;
	@NotNull
	private Long roundId;
	
	public Long getRoundId() {
		return roundId;
	}

	public void setRoundId(Long roundId) {
		this.roundId = roundId;
	}

	public Integer getMode() {
		return mode;
	}

	public void setMode(Integer mode) {
		this.mode = mode;
	}
	
	
	
}
