package com.server.edu.election.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElectionParameterDao;
import com.server.edu.election.dao.ElectionRuleDao;
import com.server.edu.election.dao.ElectionTplDao;
import com.server.edu.election.dao.ElectionTplRefRuleDao;
import com.server.edu.election.dto.ElectionTplDto;
import com.server.edu.election.entity.ElectionParameter;
import com.server.edu.election.entity.ElectionRule;
import com.server.edu.election.entity.ElectionTpl;
import com.server.edu.election.entity.ElectionTplRefRule;
import com.server.edu.election.service.ElectionTplService;
import com.server.edu.election.vo.ElectionRuleVo;
import com.server.edu.election.vo.ElectionTplVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;

@Service
public class ElectionTplServiceImpl implements ElectionTplService
{
    @Autowired
    private ElectionTplDao electionTplDao;
    
    @Autowired
    private ElectionRuleDao electionRuleDao;
    
    @Autowired
    private ElectionParameterDao electionParameterDao;
    
    @Autowired
    private ElectionTplRefRuleDao electionTplRefRuleDao;
    
    @Override
    public PageInfo<ElectionTplVo> list(PageCondition<ElectionTplDto> condition)
    {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        List<ElectionTplVo> tplList = new ArrayList<>();
        Example example = new Example(ElectionTpl.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(condition.getCondition().getName()))
        {
            criteria.andEqualTo("name", condition.getCondition().getName());
        }
        if (condition.getCondition().getStatus() != null)
        {
            criteria.andEqualTo("status", condition.getCondition().getStatus());
        }
        //选课方案模板
        List<ElectionTpl> list = electionTplDao.selectByExample(example);
        if (CollectionUtil.isNotEmpty(list))
        {
            list.forEach(temp -> {
                ElectionTplVo electionTplVo = new ElectionTplVo();
                BeanUtils.copyProperties(temp, electionTplVo);
                //查询选课方案对应的规则
                List<ElectionRule> ruleList =
                    electionRuleDao.selectTplOfRule(temp.getId());
                StringBuilder stringBuilder = new StringBuilder();
                ruleList.forEach(c -> {
                    stringBuilder.append(c.getName());
                    stringBuilder.append("\n");
                });
                electionTplVo.setRules(stringBuilder.toString());
                tplList.add(electionTplVo);
            });
        }
        PageInfo<ElectionTplVo> pageInfo = new PageInfo<>(tplList);
        return pageInfo;
    }
    
    @Override
    public int add(ElectionTplDto dto)
    {
        int result = Constants.ZERO;
        //判断模板名称是否存在
        Example example = new Example(ElectionTpl.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("name", dto.getName());
        List<ElectionTpl> existList = electionTplDao.selectByExample(example);
        if (CollectionUtil.isNotEmpty(existList))
        {
            throw new ParameterValidateException(I18nUtil.getMsg("common.exist",
                I18nUtil.getMsg("electionRuleDto.tpl")));
        }
        ElectionTpl electionTpl = new ElectionTpl();
        BeanUtils.copyProperties(dto, electionTpl);
        result = electionTplDao.insertSelective(electionTpl);
        if (result <= Constants.ZERO)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.saveError",
                    I18nUtil.getMsg("electionRuleDto.tpl")));
        }
        List<Long> list = dto.getRuleIds();
        if (CollectionUtil.isEmpty(list))
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("electionRuleDto.ruleIsNotNull"));
        }
        List<ElectionTplRefRule> refList = new ArrayList<>();
        list.forEach(temp -> {
            ElectionTplRefRule electionTplRefRule = new ElectionTplRefRule();
            electionTplRefRule.setTplId(electionTpl.getId());
            electionTplRefRule.setRuleId(temp);
            refList.add(electionTplRefRule);
        });
        result = electionTplRefRuleDao.batchInsert(refList);
        if (result <= Constants.ZERO)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.saveError",
                    I18nUtil.getMsg("electionRuleDto.tplRefRule")));
        }
        return result;
    }
    
    @Override
    public int update(ElectionTplDto dto)
    {
        if (dto.getId() == null)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.parameterError"));
        }
        //判断模板名称是否存在
        Example example = new Example(ElectionTpl.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("name", dto.getName());
        List<ElectionTpl> existList = electionTplDao.selectByExample(example);
        if (CollectionUtil.isNotEmpty(existList)
            && !dto.getId().equals(existList.get(Constants.ZERO).getId()))
        {
            throw new ParameterValidateException(I18nUtil.getMsg("common.exist",
                I18nUtil.getMsg("electionRuleDto.tpl")));
        }
        int result = Constants.ZERO;
        ElectionTpl electionTpl = new ElectionTpl();
        BeanUtils.copyProperties(dto, electionTpl);
        result = electionTplDao.updateByPrimaryKeySelective(electionTpl);
        if (result <= Constants.ZERO)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.editError",
                    I18nUtil.getMsg("electionRuleDto.tpl")));
        }
        List<Long> list = dto.getRuleIds();
        if (CollectionUtil.isEmpty(list))
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("electionRuleDto.ruleIsNotNull"));
        }
        Example refExample = new Example(ElectionTplRefRule.class);
        Example.Criteria refCriteria = refExample.createCriteria();
        refCriteria.andEqualTo("tplId", dto.getId());
        result = electionTplRefRuleDao.deleteByExample(refExample);
        if (result <= Constants.ZERO)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.editError",
                    I18nUtil.getMsg("electionRuleDto.tpl")));
        }
        List<ElectionTplRefRule> refList = new ArrayList<ElectionTplRefRule>();
        list.forEach(temp -> {
            ElectionTplRefRule electionTplRefRule = new ElectionTplRefRule();
            electionTplRefRule.setRuleId(temp);
            electionTplRefRule.setTplId(dto.getId());
            refList.add(electionTplRefRule);
        });
        result = electionTplRefRuleDao.batchInsert(refList);
        if (result <= Constants.ZERO)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.editError",
                    I18nUtil.getMsg("electionRuleDto.tplRefRule")));
        }
        return result;
    }
    
    @Override
    public int updateStatus(ElectionTplDto dto)
    {
        if (dto.getId() == null || dto.getStatus() == null)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.parameterError"));
        }
        ElectionTpl electionTpl = new ElectionTpl();
        BeanUtils.copyProperties(dto, electionTpl);
        int result = electionTplDao.updateByPrimaryKeySelective(electionTpl);
        if (result <= Constants.ZERO)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.editError",
                    I18nUtil.getMsg("electionRuleDto.tpl")));
        }
        return result;
    }
    
    @Override
    public int delete(List<Long> list)
    {
        Example example = new Example(ElectionTpl.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", list);
        int result = electionTplDao.deleteByExample(example);
        if (result <= Constants.ZERO)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.failSuccess",
                    I18nUtil.getMsg("electionRuleDto.tpl")));
        }
        Example electionTplRefRule = new Example(ElectionTplRefRule.class);
        Example.Criteria eleCriteria = electionTplRefRule.createCriteria();
        eleCriteria.andIn("tplId", list);
        result = electionTplRefRuleDao.deleteByExample(electionTplRefRule);
        if (result <= Constants.ZERO)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.failSuccess",
                    I18nUtil.getMsg("electionRuleDto.tpl")));
        }
        return result;
    }
    
    @Override
    public ElectionTplVo getTpl(Long id)
    {
        ElectionTplVo electionTplVo = new ElectionTplVo();
        ElectionTpl electionTpl = electionTplDao.selectByPrimaryKey(id);
        //选中的规则参数
        BeanUtils.copyProperties(electionTpl, electionTplVo);
        List<ElectionRule> checkRuleList = electionRuleDao.selectTplOfRule(id);
        List<Long> checkRuleIds = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(checkRuleList))
        {
            checkRuleList.forEach(temp -> {
                checkRuleIds.add(temp.getId());
            });
        }
        //所有的规则参数
        Example ruleExample = new Example(ElectionRule.class);
        Example.Criteria criteria = ruleExample.createCriteria();
        criteria.andEqualTo("status", ElectionRuleVo.enable);
        List<ElectionRule> ruleList =
            electionRuleDao.selectByExample(ruleExample);
        List<ElectionRuleVo> copyList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(ruleList))
        {
            //选中加钩子
            ruleList.forEach(temp -> {
                ElectionRuleVo electionRuleVo = new ElectionRuleVo();
                BeanUtils.copyProperties(temp, electionRuleVo);
                Example example = new Example(ElectionParameter.class);
                Example.Criteria parameterCriteria = example.createCriteria();
                parameterCriteria.andEqualTo("ruleId", electionRuleVo.getId());
                List<ElectionParameter> parameters =
                    electionParameterDao.selectByExample(example);
                electionRuleVo.setList(parameters);
                electionRuleVo.setCheckIndex(ElectionRuleVo.unCheck);
                if (CollectionUtil.isNotEmpty(checkRuleIds))
                {
                    if (checkRuleIds.contains(electionRuleVo.getId()))
                    {
                        electionRuleVo.setCheckIndex(ElectionRuleVo.check);
                    }
                }
                copyList.add(electionRuleVo);
            });
        }
        electionTplVo.setList(copyList);
        return electionTplVo;
    }
    
}
