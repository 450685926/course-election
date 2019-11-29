package com.server.edu.election.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElectionParameterDao;
import com.server.edu.election.dao.ElectionRuleDao;
import com.server.edu.election.dto.ElectionRuleDto;
import com.server.edu.election.entity.ElectionParameter;
import com.server.edu.election.entity.ElectionRule;
import com.server.edu.election.service.ElectionRuleService;
import com.server.edu.election.studentelec.service.cache.RuleCacheService;
import com.server.edu.election.vo.ElectionRuleVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;

/**
 * 选课规则在职研究生使用普研的，特此说明
 */
@Service
@Primary
public class ElectionRuleServiceImpl implements ElectionRuleService
{
    @Autowired
    private ElectionRuleDao electionRuleDao;
    
    @Autowired
    private ElectionParameterDao electionParameterDao;
    
    @Autowired
    private RuleCacheService cacheUtil;
    
    @Override
    public List<ElectionRule> list(ElectionRuleDto electionRuleDto)
    {
        Example example = new Example(ElectionRule.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(electionRuleDto.getType()))
        {
            criteria.andEqualTo("type", electionRuleDto.getType());
        }
        if (StringUtils.isNotBlank(electionRuleDto.getManagerDeptId()))
        {
            criteria.andEqualTo("managerDeptId",electionRuleDto.getManagerDeptId());
        }
        if (electionRuleDto.getStatus() != null)
        {
            criteria.andEqualTo("status", electionRuleDto.getStatus());
        }else { // 不查询本研互选数据
        	criteria.andNotEqualTo("status", Constants.TOW);
		}
        if (StringUtils.isNotBlank(electionRuleDto.getName()))
        {
            criteria.andLike("name", "%"+electionRuleDto.getName()+"%");
        }
        List<ElectionRule> list = electionRuleDao.selectByExample(example);
        return list;
    }
    
	@Override
	public List<ElectionRule> mutualRuleList(ElectionRuleDto electionRuleDto) {
		return electionRuleDao.select(electionRuleDto);
	}

    /**
     * 在职研究生使用普研规则，部门写死
     * @param managerDeptId
     * @return
     */
    @Override
    public List<ElectionRuleVo> retakeRuleList(String managerDeptId)
    {
        Example example = new Example(ElectionRule.class);
        Example.Criteria criteria = example.createCriteria();
        // 重修选课规则不会变，写死
        List<String> list = new ArrayList<>();
        list.add("按培养计划选课");
        list.add("免修申请期间不能选课");
        list.add("英语小课门数限制");
        list.add("按教学班选课");
        list.add("不允许时间冲突");
        list.add("不能跨校区选课");
        list.add("不能超出人数上限");
        criteria.andIn("name", list);
        criteria.andEqualTo("managerDeptId",managerDeptId);
        criteria.andNotEqualTo("status", Constants.TOW);
        List<ElectionRule> electionRules = electionRuleDao.selectByExample(example);
        List<ElectionRuleVo> ruleVos = new ArrayList<>(electionRules.size());
        for (ElectionRule electionRule : electionRules) {
            ElectionRuleVo electionRuleVo = new ElectionRuleVo();
            Long ruleId = electionRule.getId();
            electionRuleVo.setId(ruleId);
            electionRuleVo.setRemark(electionRule.getRemark());
            electionRuleVo.setName(electionRule.getName());
            List<ElectionParameter> electionParameters = electionParameterDao.selectElectionParameter(ruleId);
            electionRuleVo.setList(electionParameters);
            ruleVos.add(electionRuleVo);
        }
        return ruleVos;
    }
    
    @Override
	public PageInfo<ElectionRule> page(PageCondition<ElectionRuleDto> condition){
    	ElectionRuleDto electionRuleDto = condition.getCondition();
    	PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
    	List<ElectionRule> list =list(electionRuleDto);
    	PageInfo<ElectionRule> pageInfo = new PageInfo<>(list);
    	return pageInfo;
    }
    
    @Override
    public List<ElectionRuleVo> ruleParamers(ElectionRuleDto electionRuleDto)
    {
        List<ElectionRule> ruleList = list(electionRuleDto);
        List<ElectionParameter> parameters = electionParameterDao.selectAll();
        List<ElectionRuleVo> list = new ArrayList<>();
        ruleList.forEach(temp -> {
            if (CollectionUtil.isNotEmpty(parameters))
            {
                ElectionRuleVo electionRuleVo = new ElectionRuleVo();
                BeanUtils.copyProperties(temp, electionRuleVo);
                electionRuleVo.setCheckIndex(Constants.ZERO);
                List<ElectionParameter> params = parameters.stream()
                    .filter(param -> param.getRuleId().equals(temp.getId()))
                    .collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(params))
                {
                    electionRuleVo.setList(params);
                }
                list.add(electionRuleVo);
            }
        });
        return list;
    }
    
    @Override
    public ElectionRuleVo selectRuleDeatil(Long id)
    {
        ElectionRuleVo electionRuleVo = new ElectionRuleVo();
        ElectionRule electionRule = electionRuleDao.selectByPrimaryKey(id);
        BeanUtils.copyProperties(electionRule, electionRuleVo);
        Example example = new Example(ElectionParameter.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("ruleId", electionRuleVo.getId());
        List<ElectionParameter> list =
            electionParameterDao.selectByExample(example);
        electionRuleVo.setList(list);
        return electionRuleVo;
    }
    
    @Override
    public RestResult<Integer> updateRule(ElectionRuleDto electionRuleDto)
    {
        int result = 0;
        ElectionRule electionRule = new ElectionRule();
        if (StringUtils.isNotBlank(electionRuleDto.getRemark()))
        {
            electionRule.setRemark(electionRuleDto.getRemark());
        }
        electionRule.setId(electionRuleDto.getId());
        electionRule.setStatus(electionRuleDto.getStatus());
        result = electionRuleDao.updateByPrimaryKeySelective(electionRule);
        if (result <= 0)
        {
            if (StringUtils.isNotBlank(electionRuleDto.getRemark()))
            {
                RestResult.fail(I18nUtil.getMsg("common.editError",
                    I18nUtil.getMsg("electionRuleDto.rule")));
            }
            else
            {
                RestResult.fail(I18nUtil.getMsg("electionRuleDto.statusNull"));
            }
        }
        cacheUtil.cacheAllRule();
        return RestResult.success();
    }
    
    @Override
    public RestResult<Integer> updateRuleParameter(
        ElectionRuleDto electionRuleDto)
    {
        int result = 0;
        ElectionRule electionRule = new ElectionRule();
        electionRule.setId(electionRuleDto.getId());
        electionRule.setStatus(electionRuleDto.getStatus());
        if (StringUtils.isNotBlank(electionRuleDto.getRemark()))
        {
            electionRule.setRemark(electionRuleDto.getRemark());
        }
        result = electionRuleDao.updateByPrimaryKeySelective(electionRule);
        if (result <= 0)
        {
            if (StringUtils.isNotBlank(electionRuleDto.getRemark()))
            {
                RestResult.fail(I18nUtil.getMsg("common.editError",
                    I18nUtil.getMsg("electionRuleDto.rule")));
            }
            else
            {
                RestResult.fail(I18nUtil.getMsg("electionRuleDto.statusNull"));
            }
        }
        List<ElectionParameter> list = electionRuleDto.getList();
        result = electionParameterDao.batchUpdateStatus(list);
        cacheUtil.cacheAllRule();
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
    
    @Override
    public int batchUpdate(ElectionRuleDto electionRuleDto)
    {
        if (CollectionUtil.isEmpty(electionRuleDto.getRuleIds())
            || electionRuleDto.getStatus() == null)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("baseresservice.parameterError"));
        }
        int result = electionRuleDao.batchUpdate(electionRuleDto);
        if (result <= Constants.ZERO)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.editError",
                    I18nUtil.getMsg("electionRuleDto.rule")));
        }
        cacheUtil.cacheAllRule();
        return result;
    }

}
