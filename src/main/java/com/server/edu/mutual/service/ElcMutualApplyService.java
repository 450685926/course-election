package com.server.edu.mutual.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.mutual.dto.ElcMutualApplyDto;
import com.server.edu.mutual.entity.ElcMutualApply;
import com.server.edu.mutual.vo.ElcMutualApplyVo;

public interface ElcMutualApplyService {
	PageInfo<ElcMutualApplyVo> getElcMutualApplyList(PageCondition<ElcMutualApplyDto> dto);
	
	PageInfo<ElcMutualApplyVo> getElcMutualCoursesForStu(PageCondition<ElcMutualApplyDto> dto);
	
	int apply(ElcMutualApplyDto dto);
	
	int cancel(Long id);
	
	int delete(List<Long> ids);
	
	ElcMutualApply getElcMutualApplyById(Long id);
}
