package com.server.edu.election.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.server.edu.election.dao.ElectionParameterDao;
import com.server.edu.election.dao.ElectionRuleDao;
import com.server.edu.election.dto.ElectionRuleDto;
import com.server.edu.election.entity.ElectionParameter;
import com.server.edu.election.entity.ElectionRule;
import com.server.edu.election.service.ElectionRuleService;
import com.server.edu.election.vo.ElectionRuleVo;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;

@Service
@Primary
public class ElectionRuleServiceImpl implements ElectionRuleService
{
    @Autowired
    private ElectionRuleDao electionRuleDao;
    
    @Autowired
    private ElectionParameterDao electionParameterDao;
    
    @Override
    public List<ElectionRule> list(ElectionRuleDto electionRuleDto)
    {
        Example example = new Example(ElectionRule.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(electionRuleDto.getType()))
        {
            criteria.andEqualTo("type", electionRuleDto.getType());
        }
        if (electionRuleDto.getStatus() != null)
        {
            criteria.andEqualTo("status", electionRuleDto.getStatus());
        }
        List<ElectionRule> list = electionRuleDao.selectByExample(example);
        return list;
    }
    
    @Override
    public ElectionRuleVo selectRuleDeatil(ElectionRuleDto electionRuleDto)
    {
        ElectionRuleVo electionRuleVo = new ElectionRuleVo();
        return electionRuleVo;
    }
    
    @Override
    public List<ElectionRuleVo> listAll(String projectId)
    {
        List<ElectionRuleVo> rules =
            electionRuleDao.listAllByProjectId(projectId);
        
        List<ElectionParameter> parameters = electionParameterDao.selectAll();
        for (ElectionRuleVo rule : rules)
        {
            if (CollectionUtil.isNotEmpty(parameters))
            {
                List<ElectionParameter> params = parameters.stream()
                    .filter(param -> param.getRuleId().equals(rule.getId()))
                    .collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(params))
                {
                    rule.setParameters(params);
                }
            }
        }
        return rules;
    }
    
}
