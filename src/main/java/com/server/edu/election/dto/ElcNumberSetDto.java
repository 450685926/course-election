package com.server.edu.election.dto;

import java.util.List;

import com.server.edu.election.entity.ElcNumberSet;

public class ElcNumberSetDto extends ElcNumberSet {
	private static final long serialVersionUID = 1L;
	private List<Integer> turns;
	public List<Integer> getTurns() {
		return turns;
	}
	public void setTurns(List<Integer> turns) {
		this.turns = turns;
	}
	
	

}
