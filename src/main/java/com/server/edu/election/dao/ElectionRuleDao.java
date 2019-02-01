package com.server.edu.election.dao;

import java.util.List;

import com.server.edu.election.dto.ElectionRuleDto;
import com.server.edu.election.entity.ElectionRule;
import com.server.edu.election.vo.ElectionRuleVo;

import tk.mybatis.mapper.common.Mapper;

public interface ElectionRuleDao extends Mapper<ElectionRule> {
	List<ElectionRuleVo> selectRuleAndParameter(ElectionRuleDto dto);
}