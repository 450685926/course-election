package com.server.edu.election.dao;

import java.util.List;

import com.server.edu.election.dto.ElectionRuleDto;
import com.server.edu.election.entity.ElectionParameter;
import tk.mybatis.mapper.common.Mapper;

public interface ElectionParameterDao extends Mapper<ElectionParameter> {
	int batchUpdateStatus(List<ElectionParameter> list);
	
	int batchUpdate(ElectionRuleDto electionRuleDto);

	List<ElectionParameter> selectElectionParameter(Long ruleId);

}