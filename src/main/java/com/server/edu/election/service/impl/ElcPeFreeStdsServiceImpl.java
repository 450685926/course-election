package com.server.edu.election.service.impl;

import java.util.List;

import com.server.edu.common.enums.GroupDataEnum;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElcPeFreeStdsDao;
import com.server.edu.election.dto.ElcPeFreeStdsDto;
import com.server.edu.election.entity.ElcPeFreeStds;
import com.server.edu.election.service.ElcPeFreeStdsService;
import com.server.edu.election.vo.ElcPeFreeStdsVo;
import com.server.edu.exception.ParameterValidateException;

import tk.mybatis.mapper.entity.Example;

@Service
@Primary
public class ElcPeFreeStdsServiceImpl implements ElcPeFreeStdsService
{
    @Autowired
    private ElcPeFreeStdsDao elcPeFreeStdsDao;
    
    @Override
    public PageInfo<ElcPeFreeStdsVo> list(
        PageCondition<ElcPeFreeStdsDto> condition)
    {
        ElcPeFreeStdsDto elcPeFreeStdsDto = condition.getCondition();
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        List<ElcPeFreeStdsVo> list =
            elcPeFreeStdsDao.selectElcPeFreeStds(elcPeFreeStdsDto);
        PageInfo<ElcPeFreeStdsVo> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }
    
    @Override
    public int add(List<String> studentIds)
    {
        int result = elcPeFreeStdsDao.batchInsert(studentIds);
        if (result <= Constants.ZERO)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.saveError",
                    I18nUtil.getMsg("electionRuleDto.elcPeFreeStds")));
        }
        return result;
    }
    
    @Override
    public int delete(List<Long> ids)
    {
        Example example = new Example(ElcPeFreeStds.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", ids);
        int result = elcPeFreeStdsDao.deleteByExample(example);
        if (result <= Constants.ZERO)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.saveError",
                    I18nUtil.getMsg("electionRuleDto.elcPeFreeStds")));
        }
        return result;
    }
    
    @Override
    public PageInfo<ElcPeFreeStdsVo> addStudentInfo(
        PageCondition<ElcPeFreeStdsDto> condition)
    {
        ElcPeFreeStdsDto elcPeFreeStdsDto = condition.getCondition();
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        List<ElcPeFreeStdsVo> list =
            elcPeFreeStdsDao.selectElcStudents(elcPeFreeStdsDto);
        PageInfo<ElcPeFreeStdsVo> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }
    
}
