package com.server.edu.election.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.server.edu.election.dao.ElectionRuleDao;
import com.server.edu.election.dto.ElectionRuleDto;
import com.server.edu.election.entity.ElectionRule;
import com.server.edu.election.service.ElectionRuleService;
import com.server.edu.election.vo.ElectionRuleVo;

import tk.mybatis.mapper.entity.Example;

@Service
public class ElectionRuleServiceImpl implements ElectionRuleService
{
	@Autowired
	private ElectionRuleDao electionRuleDao;
	public List<ElectionRule> list(ElectionRuleDto electionRuleDto){
		Example example = new Example(ElectionRule.class);
		Example.Criteria criteria = example.createCriteria();
		if(StringUtils.isNotBlank(electionRuleDto.getType())) {
			criteria.andEqualTo("type",electionRuleDto.getType());
		}
		if(electionRuleDto.getStatus()!=null) {
			criteria.andEqualTo("status",electionRuleDto.getStatus());
		}
		List<ElectionRule> list = electionRuleDao.selectByExample(example);
		return list;
	}
    
}
