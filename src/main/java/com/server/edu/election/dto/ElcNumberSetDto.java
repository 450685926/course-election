package com.server.edu.election.dto;

import com.server.edu.election.entity.ElcNumberSet;

import java.util.List;

public class ElcNumberSetDto extends ElcNumberSet {
	private static final long serialVersionUID = 1L;
	private List<Integer> turns;

	/**分库分表标识*/
	private Integer index;

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	private Integer mode;
	
	public Integer getMode() {
		return mode;
	}
	public void setMode(Integer mode) {
		this.mode = mode;
	}
	public List<Integer> getTurns() {
		return turns;
	}
	public void setTurns(List<Integer> turns) {
		this.turns = turns;
	}
	
	

}
