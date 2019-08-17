package com.server.edu.election.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElectionConstantsDao;
import com.server.edu.election.dto.ElectionConstantsDto;
import com.server.edu.election.entity.ElectionConstants;
import com.server.edu.election.service.ElectionConstantsService;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
@Primary
public class ElectionConstantsServiceImpl implements ElectionConstantsService
{
    @Autowired
    private ElectionConstantsDao electionConstantsDao;
    
    @Override
    public PageInfo<ElectionConstants> list(
        PageCondition<ElectionConstantsDto> condition)
    {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Example example = new Example(ElectionConstants.class);
        ElectionConstantsDto quer = condition.getCondition();
        Criteria createCriteria = example.createCriteria();
        createCriteria.andEqualTo("managerDeptId", quer.getManagerDeptId());
        if (StringUtils.isNotBlank(quer.getName()))
        {
            createCriteria.andLike("name", "%" + quer.getName() + "%");
        } 
        if (StringUtils.isNotBlank(quer.getTrainingLevel()))
        {
        	createCriteria.andEqualTo("trainingLevel", quer.getTrainingLevel());
        } 
        
        List<ElectionConstants> list =
            electionConstantsDao.selectByExample(example);
        PageInfo<ElectionConstants> pageInfo = new PageInfo<>(list);
        return pageInfo;
        
    }
    
    @Override
    public int add(ElectionConstantsDto dto)
    {
        Example example = new Example(ElectionConstants.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("key", dto.getKey());
        criteria.andEqualTo("managerDeptId", dto.getManagerDeptId());
        List<ElectionConstants> list =
            electionConstantsDao.selectByExample(example);
        if (CollectionUtil.isNotEmpty(list))
        {
            throw new ParameterValidateException(I18nUtil.getMsg("common.exist",
                I18nUtil.getMsg("electionRuleDto.constants")));
        }
        ElectionConstants electionConstants = new ElectionConstants();
        BeanUtils.copyProperties(dto, electionConstants);
        int result = electionConstantsDao.insert(electionConstants);
        if (result <= Constants.ZERO)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.saveError",
                    I18nUtil.getMsg("electionRuleDto.constants")));
        }
        return result;
    }
    
    @Override
    public int update(ElectionConstantsDto dto)
    {
        if (dto.getId() == null)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("baseresservice.parameterError"));
        }
        Example example = new Example(ElectionConstants.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("managerDeptId", dto.getManagerDeptId());
        criteria.andEqualTo("key", dto.getKey());
        criteria.andNotEqualTo("id", dto.getId());
        List<ElectionConstants> list =
            electionConstantsDao.selectByExample(example);
        if (CollectionUtil.isNotEmpty(list))
        {
            throw new ParameterValidateException(I18nUtil.getMsg("common.exist",
                I18nUtil.getMsg("electionRuleDto.constants")));
        }
        ElectionConstants electionConstants = new ElectionConstants();
        BeanUtils.copyProperties(dto, electionConstants);
        int result =
            electionConstantsDao.updateByPrimaryKeySelective(electionConstants);
        if (result <= Constants.ZERO)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.editError",
                    I18nUtil.getMsg("electionRuleDto.constants")));
        }
        return result;
    }
    
    @Override
    public ElectionConstants getConstants(Long id)
    {
        ElectionConstants electionConstants =
            electionConstantsDao.selectByPrimaryKey(id);
        return electionConstants;
    }

	@Override
	public List<ElectionConstants> getAllGraduateConstants(String projectId) {
		List<ElectionConstants> list = electionConstantsDao.getAllGraduateConstants(projectId);
		return list;
	}
    
}
