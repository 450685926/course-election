package com.server.edu.mutual.dao;

import tk.mybatis.mapper.common.Mapper;

import java.util.List;

import com.github.pagehelper.Page;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.vo.ElectionRoundsVo;

public interface ElecMutualRoundsDao extends Mapper<ElectionRounds>{

	List<Long> listAllRefRuleId(Long roundId);

	Page<ElectionRoundsVo> listPage(ElectionRounds param);

	/**
	 * 获取轮次可选课程数
	 * @param id
	 * @return
	 */
	Integer getElcCourseNum(Long roundId);


}
