package com.server.edu.election.service;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.election.dto.ElectionConstantsDto;
import com.server.edu.election.entity.ElectionConstants;

public interface ElectionConstantsService {
	PageInfo<ElectionConstants> list(PageCondition<ElectionConstantsDto> condition);
	
	int add(ElectionConstantsDto dto);
	
	int update(ElectionConstantsDto dto);
	
	ElectionConstants getConstants(Long id);

}
