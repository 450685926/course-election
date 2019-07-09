package com.server.edu.election.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElecResultSwitchDao;
import com.server.edu.election.entity.ElcResultSwitch;
import com.server.edu.election.service.ElecResultSwitchService;
import com.server.edu.exception.ParameterValidateException;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class ElcResultSwitchServiceImpl implements ElecResultSwitchService{
	@Autowired
	ElecResultSwitchDao elecResultSwitchDao;
	
	@Override
	public void add(ElcResultSwitch resultSwitch) {
		Example example = new Example(ElcResultSwitch.class);
    	example.createCriteria()
    		.andEqualTo("calendarId", resultSwitch.getCalendarId())
    		.andEqualTo("status", resultSwitch.getStatus())
    		.andEqualTo("openTimeStart", resultSwitch.getOpenTimeStart())
    		.andEqualTo("openTimeEnd", resultSwitch.getOpenTimeEnd())
    	    .andEqualTo("projectId",resultSwitch.getProjectId());
    	
	    int count = elecResultSwitchDao.selectCountByExample(example);
	    if (count > 0)
	    {
	        throw new ParameterValidateException(
	            I18nUtil.getMsg("resultSwitch.exist"));
	    }
		
		int result = elecResultSwitchDao.insertSelective(resultSwitch);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("elecResultSwitch.addError",I18nUtil.getMsg("elecResultSwitch.addError")));
		}
	}

	@Override
	public ElcResultSwitch get(Long id) {
		return elecResultSwitchDao.selectByPrimaryKey(id);
	}

	@Override
	public ElcResultSwitch getSwitch(Long calendarId) {
		Example example = new Example(ElcResultSwitch.class);
		Criteria createCriteria = example.createCriteria();
		createCriteria.andEqualTo("calendarId",calendarId);
		return elecResultSwitchDao.selectOneByExample(example);
	}
	
}
