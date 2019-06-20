package com.server.edu.election.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dao.ElcRoundConditionDao;
import com.server.edu.election.dao.ElecRoundStuDao;
import com.server.edu.election.dao.ElecRoundsDao;
import com.server.edu.election.dto.ElectionRoundsDto;
import com.server.edu.election.entity.ElcRoundCondition;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.service.ElecRoundService;
import com.server.edu.election.studentelec.service.impl.RoundDataProvider;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;

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
    
    @Autowired
    private ElcRoundConditionDao roundConditionDao;
    
    @Autowired
    private RoundDataProvider dataProvider;
    
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
        ElectionRoundsDto round = roundsDao.getOne(roundId);
        List<Long> ruleIds = roundsDao.listAllRefRuleId(roundId);
        round.setRuleIds(ruleIds);
        ElcRoundCondition con = roundConditionDao.selectByPrimaryKey(roundId);
        round.setRoundCondition(con);
        return round;
    }
    
    @Transactional
    @Override
    public void add(ElectionRoundsDto dto)
    {
        Example example = new Example(ElectionRounds.class);
        	example.createCriteria()
        		.andEqualTo("calendarId", dto.getCalendarId())
        		.andEqualTo("turn", dto.getTurn())
        		.andEqualTo("mode", dto.getMode())
        		.andEqualTo("electionObj", dto.getElectionObj())
        	    .andEqualTo("projectId",dto.getProjectId());
        	
        int count = roundsDao.selectCountByExample(example);
        if (count > 0)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("elec.round.exist"));
        }
        Date date = new Date();
        dto.setCreatedAt(date);
        dto.setUpdatedAt(date);
        roundsDao.insertSelective(dto);
        ElcRoundCondition roundCondition = dto.getRoundCondition();
        if (null != roundCondition)
        {
            roundCondition.setRoundId(dto.getId());
            roundConditionDao.insertSelective(roundCondition);
        }
        
        if (CollectionUtil.isNotEmpty(dto.getRuleIds()))
        {
            for (Long ruleId : dto.getRuleIds())
            {
                roundsDao.saveRoundRefRule(dto.getId(), ruleId);
            }
        }
        
        dataProvider.updateRoundCache(dto.getId());
    }
    
    @Transactional
    @Override
    public void update(ElectionRoundsDto dto)
    {
        Example example = new Example(ElectionRounds.class);
        example.createCriteria()
            .andEqualTo("calendarId", dto.getCalendarId())
            .andEqualTo("turn", dto.getTurn())
            .andEqualTo("mode", dto.getMode())
            .andEqualTo("electionObj", dto.getElectionObj())
            .andNotEqualTo("id", dto.getId());
        int count = roundsDao.selectCountByExample(example);
        if (count > 0)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("elec.round.exist"));
        }
        Date date = new Date();
        dto.setUpdatedAt(date);
        roundsDao.updateByPrimaryKeySelective(dto);
        
        ElcRoundCondition roundCondition = dto.getRoundCondition();
        if (null != roundCondition)
        {
            Example example1 = new Example(ElcRoundCondition.class);
            example1.createCriteria().andEqualTo("roundId", dto.getId());
            int count1 = roundConditionDao.selectCountByExample(example1);
            roundCondition.setRoundId(dto.getId());
            if (count1 == 0)
            {
                roundConditionDao.insertSelective(roundCondition);
            }
            else
            {
                roundConditionDao.updateByPrimaryKeySelective(roundCondition);
            }
        }
        
        roundsDao.deleteAllRefRule(dto.getId());
        if (CollectionUtil.isNotEmpty(dto.getRuleIds()))
        {
            for (Long ruleId : dto.getRuleIds())
            {
                roundsDao.saveRoundRefRule(dto.getId(), ruleId);
            }
        }
        
        dataProvider.updateRoundCache(dto.getId());
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
            
            dataProvider.updateRoundCache(id);
        }
    }
    
}
