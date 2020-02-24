package com.server.edu.mutual.service;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.entity.Department;
import com.server.edu.common.rest.RestResult;
import com.server.edu.mutual.dto.ElcMutualCrossStu;
import com.server.edu.mutual.dto.ElcMutualCrossStuDto;
import com.server.edu.mutual.vo.ElcMutualCrossStuVo;

import java.util.List;

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
	//全部删除改成以对象方式接收
//	int deleteAll(ElcMutualCrossStu dto);
	
	/**
	 * 获取当前所有启动的部门列表（包括虚拟部门）
	 * */
	RestResult<List<Department>> findDept(String virtualDept,Integer type,Integer manageDept);
}
