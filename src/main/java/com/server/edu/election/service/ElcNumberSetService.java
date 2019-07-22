package com.server.edu.election.service;

import com.server.edu.election.entity.ElcNumberSet;

public interface ElcNumberSetService {
	
	int releaseAll(Long calendarId);
	
	int save(ElcNumberSet elcNumberSet);
	
	ElcNumberSet getElcNumberSetInfo(Long calendarId);

}
