package com.server.edu.election.service;

import com.server.edu.election.entity.ElcNumberSet;
import com.server.edu.util.async.AsyncResult;

public interface ElcNumberSetService {
	
	int releaseAll(Long calendarId);
	
	int save(ElcNumberSet elcNumberSet);
	
	ElcNumberSet getElcNumberSetInfo(Long calendarId);

	AsyncResult releaseAllAsync(Long calendarId);
}
