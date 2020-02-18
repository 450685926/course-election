package com.server.edu.mutual.service;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.RestResult;
import com.server.edu.mutual.dto.ElcMutualCrossStuDto;
import com.server.edu.mutual.dto.ElcMutualListDto;
import com.server.edu.mutual.vo.ElcMutualListVo;

public interface ElcMutualListService {

	/**
	 * 获取研究生或本科生名单列表
	 * @param dto
	 * @return
	 */
	PageInfo<ElcMutualListVo> getMutualStuList(PageCondition<ElcMutualListDto> condition);

	
	
	/**
	 * 导出本研互选名单列表
	 * @returnType: RestResult<String>
	 */
	RestResult<String> exportelcMutualStuList(PageCondition<ElcMutualListDto> dto);
	/**
	 * 获取课程名单列表
	 * @param condition
	 * @return
	 */
	PageInfo<ElcMutualListVo> getMutualCourseList(PageCondition<ElcMutualListDto> condition);

	
}
