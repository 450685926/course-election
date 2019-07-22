package com.server.edu.election.service;

import com.server.edu.election.entity.ElcClassEditAuthority;

public interface ElcClassEditAuthorityService {
	int save(ElcClassEditAuthority elcClassEditAuthority);
	
	ElcClassEditAuthority getElcClassEditAuthority(Long calendarId);

}
