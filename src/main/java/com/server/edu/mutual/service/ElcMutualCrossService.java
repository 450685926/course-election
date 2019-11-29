package com.server.edu.mutual.service;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.mutual.dto.ElcMutualCrossStu;
import com.server.edu.mutual.dto.ElcMutualCrossStuDto;
import com.server.edu.mutual.vo.ElcMutualCrossStuVo;

public interface ElcMutualCrossService {
	/**
	 * 获取互选跨学科名单列表
	 * @param dto
	 * @return
	 */
	PageInfo<ElcMutualCrossStuVo> getElcMutualCrossList(PageCondition<ElcMutualCrossStuDto> dto);
	
	/**
	 * @param calendarId
	 * @return
	 */
	int init(Long calendarId);
	/**
	 * 添加
	 * @param calendarId
	 * @param studentIds
	 * @param mode
	 * @return
	 */
	int add(Long calendarId,String studentIds,Integer mode);
	/**
	 * 批量添加
	 * @param dto
	 * @return
	 */
	int batchAdd(ElcMutualCrossStu dto);
	/**
	 * 添加全部
	 * @param dto
	 * @return
	 */
	int addAll(ElcMutualCrossStu dto);
	/**
	 * 删除
	 * @param dto
	 * @return
	 */
	int delete(ElcMutualCrossStuDto dto);
	/**
	 * 批量删除
	 * @param dto
	 * @return
	 */
	int batchDelete(ElcMutualCrossStu dto);
	
	/**
	 * 删除全部
	 * @param calendarId
	 * @param mode
	 * @return
	 */
	int deleteAll(Long calendarId,Integer mode);
	
	

}
