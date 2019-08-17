package com.server.edu.election.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.election.dto.ElectionConstantsDto;
import com.server.edu.election.entity.ElectionConstants;

public interface ElectionConstantsService {
	PageInfo<ElectionConstants> list(PageCondition<ElectionConstantsDto> condition);
	
	int add(ElectionConstantsDto dto);
	
	int update(ElectionConstantsDto dto);
	
	ElectionConstants getConstants(Long id);
	
	/**
	 * 获取研究生常量列表
	 * @return
	 */
	List<ElectionConstants> getAllGraduateConstants(String projectId);

}
