package com.server.edu.mutual.service;

import java.util.List;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dto.ElectionRoundsDto;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.vo.ElectionRoundsVo;

public interface ElecMutualRoundService {

	/**
	 * 新增互选轮次
	 * @param round
	 */
	void add(ElectionRoundsDto dto);

	/**
	 * 根据ID查询轮次信息
	 * @param roundId
	 * @return
	 */
	ElectionRoundsDto get(Long roundId);

	/**
	 * 修改轮次 
	 * @param round
	 */
	void update(ElectionRoundsDto dto);

	/**
	 * 分页查询轮次信息
	 * @param condition
	 * @return
	 */ 
	PageResult<ElectionRoundsVo> listPage(PageCondition<ElectionRounds> condition);

	/**
	 * 删除轮次
	 * @param ids
	 */
	void delete(List<Long> ids);

}
