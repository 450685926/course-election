package com.server.edu.election.service.impl;

import java.util.Date;

import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
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
    	    .andEqualTo("projectId",resultSwitch.getProjectId());

		ElcResultSwitch elcResultSwitch = elecResultSwitchDao.selectOneByExample(example);
		int result;
		if (elcResultSwitch != null) {
			elcResultSwitch.setStatus(resultSwitch.getStatus());
			elcResultSwitch.setOpenTimeStart(resultSwitch.getOpenTimeStart());
			elcResultSwitch.setOpenTimeEnd(resultSwitch.getOpenTimeEnd());
			elcResultSwitch.setOpenTimeEnd(resultSwitch.getOpenTimeEnd());
			result = elecResultSwitchDao.updateByPrimaryKey(elcResultSwitch);
		} else {
			Date date = new Date();
			resultSwitch.setCreateAt(date);
			result = elecResultSwitchDao.insertSelective(resultSwitch);
		}
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

	@Override
	public boolean getSwitchStatus(Long calendarId, String projectId) {
		Example example = new Example(ElcResultSwitch.class);
		Criteria createCriteria = example.createCriteria();
		createCriteria.andEqualTo("calendarId",calendarId);
		createCriteria.andEqualTo("projectId",projectId);
		ElcResultSwitch elcResultSwitch = elecResultSwitchDao.selectOneByExample(example);
		if (elcResultSwitch != null && elcResultSwitch.getStatus().intValue() == 1) {
			Date start = elcResultSwitch.getOpenTimeStart();
			Date end = elcResultSwitch.getOpenTimeEnd();
			if (start != null && end != null) {
				long startTime = start.getTime();
				long endTime = end.getTime();
				long nowTime = System.currentTimeMillis();
				if (startTime < nowTime && nowTime < endTime ) {
					return true;
				}
			}
		}
		return false;
	}
	
}
