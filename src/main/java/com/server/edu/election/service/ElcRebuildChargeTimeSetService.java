package com.server.edu.election.service;

import com.server.edu.election.entity.ElcRebuildChargeTimeSet;

public interface ElcRebuildChargeTimeSetService {
	int add(ElcRebuildChargeTimeSet timeSet);
	
	int update(ElcRebuildChargeTimeSet timeSet);
	
	ElcRebuildChargeTimeSet getRebuildChargeTimeSet(Long calendarId,String projId);

}
