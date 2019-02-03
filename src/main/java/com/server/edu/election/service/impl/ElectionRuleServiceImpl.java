package com.server.edu.election.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.dao.ElectionParameterDao;
import com.server.edu.election.dao.ElectionRuleDao;
import com.server.edu.election.dto.ElectionRuleDto;
import com.server.edu.election.entity.ElectionParameter;
import com.server.edu.election.entity.ElectionRule;
import com.server.edu.election.service.ElectionRuleService;
import com.server.edu.election.vo.ElectionRuleVo;
import com.server.edu.exception.ParameterValidateException;
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
	
	public ElectionRuleVo selectRuleDeatil(ElectionRuleDto electionRuleDto) {
		ElectionRuleVo electionRuleVo = new ElectionRuleVo();
		ElectionRule electionRule = electionRuleDao.selectByPrimaryKey(electionRuleDto.getId());
		BeanUtils.copyProperties(electionRule, electionRuleVo);
		Example example = new Example(ElectionParameter.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("ruleId",electionRuleVo.getId());
		List<ElectionParameter> list = electionParameterDao.selectByExample(example);
		electionRuleVo.setList(list);
		return electionRuleVo;
	}
	
	public RestResult<Integer> updateRule(ElectionRuleDto electionRuleDto) {
		int result = 0;
		ElectionRule electionRule = new ElectionRule();
		if(StringUtils.isNotBlank(electionRuleDto.getRemark())) {
			electionRule.setRemark(electionRuleDto.getRemark());
		}
		electionRule.setId(electionRuleDto.getId());
		electionRule.setStatus(electionRuleDto.getStatus());
		result = electionRuleDao.updateByPrimaryKeySelective(electionRule);
		if(result<=0) {
			if(StringUtils.isNotBlank(electionRuleDto.getRemark())) {
				RestResult.fail(I18nUtil.getMsg("common.editError",I18nUtil.getMsg("electionRuleDto.rule")));
			}else {
				RestResult.fail(I18nUtil.getMsg("electionRuleDto.statusNull"));
			}
		}
		return RestResult.success();
	}
	
	public RestResult<Integer> updateRuleParameter(ElectionRuleDto electionRuleDto) {
		int result = 0;
		ElectionRule electionRule = new ElectionRule();
		electionRule.setId(electionRuleDto.getId());
		electionRule.setStatus(electionRuleDto.getStatus());
		if(StringUtils.isNotBlank(electionRuleDto.getRemark())) {
			electionRule.setRemark(electionRuleDto.getRemark());
		}
		result = electionRuleDao.updateByPrimaryKeySelective(electionRule);
		if(result<=0) {
			if(StringUtils.isNotBlank(electionRuleDto.getRemark())) {
				RestResult.fail(I18nUtil.getMsg("common.editError",I18nUtil.getMsg("electionRuleDto.rule")));
			}else {
				RestResult.fail(I18nUtil.getMsg("electionRuleDto.statusNull"));
			}
		}
		List<ElectionParameter> list = electionRuleDto.getList();
		Example example = new Example(ElectionParameter.class);
		Example.Criteria criteria = example.createCriteria();
		result = electionParameterDao.batchUpdateStatus(list);
		return RestResult.success();
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
                    rule.setList(params);
                }
            }
        }
        return rules;
    }
    
}
