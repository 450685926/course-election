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
import com.server.edu.election.dao.ElectionRoundsDao;
import com.server.edu.election.dto.ElectionRoundsDto;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.entity.ElectionRule;
import com.server.edu.election.service.ElectionRoundService;
import com.server.edu.util.CollectionUtil;

@Service
public class ElectionRoundServiceImpl implements ElectionRoundService
{
    @Autowired
    private ElectionRoundsDao roundsDao;
    
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
        
        if (CollectionUtil.isNotEmpty(dto.getElectionRules()))
        {
            for (ElectionRule rule : dto.getElectionRules())
            {
                roundsDao.saveRoundRefRule(dto.getId(), rule.getId());
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
        for (ElectionRule rule : dto.getElectionRules())
        {
            roundsDao.saveRoundRefRule(dto.getId(), rule.getId());
        }
    }
    
    @Override
    public void delete(List<Long> ids)
    {
        for (Long id : ids)
        {
            roundsDao.deleteByPrimaryKey(ids);
            roundsDao.deleteAllRefRule(id);
        }
    }
    
}
