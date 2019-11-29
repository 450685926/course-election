package com.server.edu.mutual.service;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
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
	 * 获取课程名单列表
	 * @param condition
	 * @return
	 */
	PageInfo<ElcMutualListVo> getMutualCourseList(PageCondition<ElcMutualListDto> condition);
	
}
