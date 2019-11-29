package com.server.edu.mutual.service;

import com.server.edu.mutual.entity.ElcMutualApplyTurns;

public interface ElcMutualApplySwitchService {
	/**
	 * @param calendarId
	 * @param projectId
	 * @return
	 */
	ElcMutualApplyTurns getElcMutualApplyTurns(Long calendarId,String projectId,Integer category);
	
	/**
	 * @param elcMutualApplyTurns
	 * @return
	 */
	void save(ElcMutualApplyTurns elcMutualApplyTurns);

}
