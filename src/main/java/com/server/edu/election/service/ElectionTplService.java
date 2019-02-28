package com.server.edu.election.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.election.dto.ElectionTplDto;
import com.server.edu.election.vo.ElectionTplVo;

public interface ElectionTplService {
	PageInfo<ElectionTplVo> list(PageCondition<ElectionTplDto> condition);
	
	int add(ElectionTplDto dto);
	
	int update(ElectionTplDto dto);
	
	ElectionTplVo getTpl(Long id,String managerDeptId);
	
	int updateStatus(ElectionTplDto dto);
	
	int delete(List<Long> ids);
}
