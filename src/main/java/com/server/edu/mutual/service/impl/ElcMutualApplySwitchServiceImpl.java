package com.server.edu.mutual.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.mutual.dao.ElcMutualApplySwitchDao;
import com.server.edu.mutual.entity.ElcMutualApplyTurns;
import com.server.edu.mutual.service.ElcMutualApplySwitchService;

import tk.mybatis.mapper.entity.Example;
@Service
public class ElcMutualApplySwitchServiceImpl implements ElcMutualApplySwitchService {
	@Autowired
	private ElcMutualApplySwitchDao elcMutualApplySwitchDao;
	
	@Override
	public ElcMutualApplyTurns getElcMutualApplyTurns(Long calendarId,String projectId,Integer category) {
		ElcMutualApplyTurns elcMutualApplyTurns = getMutualApplySwitch(calendarId, projectId,category);
		return elcMutualApplyTurns;
	}

	private ElcMutualApplyTurns getMutualApplySwitch(Long calendarId, String projectId,Integer category) {
		Example example = new Example(ElcMutualApplyTurns.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("calendarId", calendarId);
		criteria.andEqualTo("projectId", projectId);
		criteria.andEqualTo("category", category);
		ElcMutualApplyTurns elcMutualApplyTurns  = elcMutualApplySwitchDao.selectOneByExample(example);
		return elcMutualApplyTurns;
	}

	@Override
	@Transactional
	public void save(ElcMutualApplyTurns elcMutualApplyTurns) {
        if (elcMutualApplyTurns.getFail().intValue() == 1 && elcMutualApplyTurns.getGpa() == null) {
        	throw new ParameterValidateException(I18nUtil.getMsg("elcMutualApplySwitch.gpa.notNUll")); 
		}
		
		if(elcMutualApplyTurns.getBeginAt().getTime()>elcMutualApplyTurns.getEndAt().getTime()) {
			throw new ParameterValidateException(I18nUtil.getMsg("elcMutualApplySwitch.timeError")); 
		}
		ElcMutualApplyTurns mutualApplyTurns = getMutualApplySwitch(elcMutualApplyTurns.getCalendarId(),elcMutualApplyTurns.getProjectId(),elcMutualApplyTurns.getCategory());
		int result = Constants.ZERO;
		if(mutualApplyTurns!=null) {
			//BeanUtil.copyProperties(mutualApplyTurns, elcMutualApplyTurns);
			mutualApplyTurns.setProjectId(elcMutualApplyTurns.getProjectId());
			mutualApplyTurns.setOpen(elcMutualApplyTurns.getOpen());
			mutualApplyTurns.setGpa(elcMutualApplyTurns.getGpa());
			mutualApplyTurns.setFail(elcMutualApplyTurns.getFail());
			mutualApplyTurns.setBeginAt(elcMutualApplyTurns.getBeginAt());
			mutualApplyTurns.setEndAt(elcMutualApplyTurns.getEndAt());
			mutualApplyTurns.setCategory(elcMutualApplyTurns.getCategory());
			mutualApplyTurns.setAppLimit(elcMutualApplyTurns.getAppLimit());
			mutualApplyTurns.setCalendarId(elcMutualApplyTurns.getCalendarId());
			result = elcMutualApplySwitchDao.updateByPrimaryKey(mutualApplyTurns);
		}else {
			result = elcMutualApplySwitchDao.insertSelective(elcMutualApplyTurns);
		}
		
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("elcMutualApplySwitch.addError",I18nUtil.getMsg("elcMutualApplySwitch.addError")));
		}
	}

}
