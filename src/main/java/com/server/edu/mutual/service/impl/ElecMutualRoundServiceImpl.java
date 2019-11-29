package com.server.edu.mutual.service.impl;

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
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElcRoundConditionDao;
import com.server.edu.election.dao.ElecRoundsDao;
import com.server.edu.election.dto.ElectionRoundsDto;
import com.server.edu.election.entity.ElcRoundCondition;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.studentelec.service.impl.RoundDataProvider;
import com.server.edu.election.vo.ElectionRoundsVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.mutual.dao.ElecMutualRoundsDao;
import com.server.edu.mutual.service.ElecMutualRoundService;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;

@Service
public class ElecMutualRoundServiceImpl implements ElecMutualRoundService{
	@Autowired
	private ElecMutualRoundsDao elecMutualRoundsDao;
	
	@Autowired
	private ElecRoundsDao elecRoundsDao;
	
    @Autowired
    private ElcRoundConditionDao roundConditionDao;
	
    @Autowired
    private RoundDataProvider dataProvider;
	
	@Transactional
	@Override
	public void add(ElectionRoundsDto dto) {
        Example example = new Example(ElectionRounds.class);
    	example.createCriteria()
    		.andEqualTo("calendarId", dto.getCalendarId())
    		.andEqualTo("turn", dto.getTurn())
    		.andEqualTo("mode", dto.getMode())
    		.andEqualTo("electionObj", dto.getElectionObj())
    	    .andEqualTo("projectId",dto.getProjectId())
    	    .andEqualTo("deleteStatus",String.valueOf(Constants.DELETE_FALSE));
    	
	    int count = elecRoundsDao.selectCountByExample(example);
	    if (count > 0)
	    {
	        throw new ParameterValidateException(
	            I18nUtil.getMsg("elec.round.exist"));
	    }
	    
	    Date date = new Date();
	    dto.setCreatedAt(date);
	    dto.setUpdatedAt(date);
	    dto.setDeleteStatus(Constants.DELETE_FALSE);
	    elecRoundsDao.insertSelective(dto);
	    
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
	        	elecRoundsDao.saveRoundRefRule(dto.getId(), ruleId);
	        }
	    }
	    
	    dataProvider.updateRoundCache(dto.getId());
	}
	
	@Override
	public ElectionRoundsDto get(Long roundId) {
        ElectionRoundsDto round = elecRoundsDao.getOne(roundId);
        List<Long> ruleIds = elecMutualRoundsDao.listAllRefRuleId(roundId);
        round.setRuleIds(ruleIds);
        ElcRoundCondition con = roundConditionDao.selectByPrimaryKey(roundId);
        round.setRoundCondition(con);
        return round;
	}
	
	@Transactional
	@Override
	public void update(ElectionRoundsDto dto) {
        Example example = new Example(ElectionRounds.class);
        example.createCriteria()
            .andEqualTo("calendarId", dto.getCalendarId())
            .andEqualTo("turn", dto.getTurn())
            .andEqualTo("mode", dto.getMode())
            .andEqualTo("electionObj", dto.getElectionObj())
            .andEqualTo("projectId",dto.getProjectId())
            .andEqualTo("deleteStatus",String.valueOf(Constants.DELETE_FALSE))
            .andNotEqualTo("id", dto.getId());
        int count = elecRoundsDao.selectCountByExample(example);
        if (count > 0){
            throw new ParameterValidateException(I18nUtil.getMsg("elec.round.exist"));
        }
		
        Date date = new Date();
        dto.setUpdatedAt(date);
        elecRoundsDao.updateByPrimaryKeySelective(dto);
        
        ElcRoundCondition roundCondition = dto.getRoundCondition();
        if (null != roundCondition){
            Example example1 = new Example(ElcRoundCondition.class);
            example1.createCriteria().andEqualTo("roundId", dto.getId());
            int count1 = roundConditionDao.selectCountByExample(example1);
            roundCondition.setRoundId(dto.getId());
            if (count1 == 0){
                roundConditionDao.insertSelective(roundCondition);
            }else{
                roundConditionDao.updateByPrimaryKeySelective(roundCondition);
            }
        }
        
        elecRoundsDao.deleteAllRefRule(dto.getId());
        if (CollectionUtil.isNotEmpty(dto.getRuleIds()))
        {
            for (Long ruleId : dto.getRuleIds())
            {
            	elecRoundsDao.saveRoundRefRule(dto.getId(), ruleId);
            }
        }
        
        dataProvider.updateRoundCache(dto.getId());
	}

	@Override
	public PageResult<ElectionRoundsVo> listPage(PageCondition<ElectionRounds> condition) {
		PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
		
        ElectionRounds param = condition.getCondition();
        Page<ElectionRoundsVo> page = elecMutualRoundsDao.listPage(param);
        
        List<ElectionRoundsVo> list = page.getResult();
        for (ElectionRoundsVo electionRoundsVo : list) {
        	Integer elcCourseNum = elecMutualRoundsDao.getElcCourseNum(electionRoundsVo.getId());
        	electionRoundsVo.setElcCourseNum(elcCourseNum);
		}
        
        PageResult<ElectionRoundsVo> result = new PageResult<>(page);
        return result;
	}
	
	@Transactional
	@Override
	public void delete(List<Long> ids) {
        for (Long id : ids)
        {
        	ElectionRoundsDto dto = new ElectionRoundsDto();
        	dto.setId(id);
        	dto.setDeleteStatus(Constants.DELETE_TRUE);
        	elecRoundsDao.updateByPrimaryKeySelective(dto);// 删除轮次

        	dataProvider.updateRoundCache(id);
        }
	}

}
