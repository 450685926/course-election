package com.server.edu.election.dao;

import java.util.List;
import java.util.Map;

import com.server.edu.election.entity.ElectionTplRefRule;
import tk.mybatis.mapper.common.Mapper;

public interface ElectionTplRefRuleDao extends Mapper<ElectionTplRefRule> {
	int batchInsert(List<ElectionTplRefRule> list);
	int batchUpdate(Map<String,Object> map);
}