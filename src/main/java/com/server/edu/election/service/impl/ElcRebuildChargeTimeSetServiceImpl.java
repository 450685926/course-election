package com.server.edu.election.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.dao.ElcRebuildChargeTimeSetDao;
import com.server.edu.election.entity.ElcRebuildChargeTimeSet;
import com.server.edu.election.service.ElcRebuildChargeTimeSetService;
import com.server.edu.exception.ParameterValidateException;

import tk.mybatis.mapper.entity.Example;
@Service
public class ElcRebuildChargeTimeSetServiceImpl implements ElcRebuildChargeTimeSetService{

	@Autowired
	private ElcRebuildChargeTimeSetDao elcRebuildChargeTimeSetDao;
	@Override
	public int add(ElcRebuildChargeTimeSet timeSet) {
		Example example = new Example(ElcRebuildChargeTimeSet.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("calendarId", timeSet.getCalendarId());
		criteria.andEqualTo("projId", timeSet.getProjId());
		ElcRebuildChargeTimeSet elcRebuildChargeTimeSet = elcRebuildChargeTimeSetDao.selectOneByExample(example);
		if(elcRebuildChargeTimeSet !=null) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.exist",I18nUtil.getMsg("election.elcRebuildChargeTimeSet")));
		}
		int result = elcRebuildChargeTimeSetDao.insert(timeSet);
		return result;
	}

	@Override
	public int update(ElcRebuildChargeTimeSet timeSet) {
		if(timeSet.getCalendarId()==null) {
			throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
		}
		int result = elcRebuildChargeTimeSetDao.updateByPrimaryKeySelective(timeSet);
		return result;
	}

	@Override
	public ElcRebuildChargeTimeSet getRebuildChargeTimeSet(Long calendarId,String projId) {
		Example example = new Example(ElcRebuildChargeTimeSet.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("calendarId", calendarId);
		criteria.andEqualTo("projId", projId);
		ElcRebuildChargeTimeSet elcRebuildChargeTimeSet = elcRebuildChargeTimeSetDao.selectOneByExample(example);
		return elcRebuildChargeTimeSet;
	}

}
