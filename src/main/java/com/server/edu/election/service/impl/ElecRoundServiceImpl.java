package com.server.edu.election.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dao.ElecRoundStuDao;
import com.server.edu.election.dao.ElecRoundsDao;
import com.server.edu.election.dto.ElectionRoundsDto;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.service.ElecRoundService;
import com.server.edu.util.CollectionUtil;

/**
 * 选课轮次管理
 * 
 * 
 * @author  OuYangGuoDong
 * @version  [版本号, 2019年2月1日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Service
public class ElecRoundServiceImpl implements ElecRoundService
{
    @Autowired
    private ElecRoundsDao roundsDao;
    
    @Autowired
    private ElecRoundStuDao elecRoundStuDao;
    
    @Override
    public PageResult<ElectionRounds> listPage(
        PageCondition<ElectionRounds> condition)
    {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        
        ElectionRounds param = condition.getCondition();
        Page<ElectionRounds> page = this.roundsDao.listPage(param);
        
        PageResult<ElectionRounds> result = new PageResult<>(page);
        return result;
    }
    
    @Override
    public ElectionRoundsDto get(Long roundId)
    {
        ElectionRounds round = roundsDao.selectByPrimaryKey(roundId);
        return null;
    }
    
    @Transactional
    @Override
    public void add(ElectionRoundsDto dto)
    {
        Date date = new Date();
        dto.setCreatedAt(date);
        dto.setUpdatedAt(date);
        roundsDao.insertSelective(dto);
        
        if (CollectionUtil.isNotEmpty(dto.getRuleIds()))
        {
            for (Long ruleId : dto.getRuleIds())
            {
                roundsDao.saveRoundRefRule(dto.getId(), ruleId);
            }
        }
    }
    
    @Transactional
    @Override
    public void update(ElectionRoundsDto dto)
    {
        Date date = new Date();
        dto.setUpdatedAt(date);
        roundsDao.updateByPrimaryKeySelective(dto);
        
        roundsDao.deleteAllRefRule(dto.getId());
        if (CollectionUtil.isNotEmpty(dto.getRuleIds()))
        {
            for (Long ruleId : dto.getRuleIds())
            {
                roundsDao.saveRoundRefRule(dto.getId(), ruleId);
            }
        }
    }
    
    @Transactional
    @Override
    public void delete(List<Long> ids)
    {
        for (Long id : ids)
        {
            roundsDao.deleteByPrimaryKey(id);//删除轮次
            roundsDao.deleteAllRefRule(id);//删除关联规则
            elecRoundStuDao.deleteByRoundId(id);//删除可选课名单
        }
    }
    
}
