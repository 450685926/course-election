package com.server.edu.election.service.impl;

import com.server.edu.util.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.dao.ElcRebuildChargeTimeSetDao;
import com.server.edu.election.entity.ElcRebuildChargeTimeSet;
import com.server.edu.election.service.ElcRebuildChargeTimeSetService;
import com.server.edu.exception.ParameterValidateException;

import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

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

	/**
	 * @Description: 检查缴费时间是否在正常缴费时间内
	 * @author kan yuanfeng
	 * @date 2019/11/19 14:56
	 */
	@Override
	public void checkTime(Long calendarId) {
		Example example = new Example(ElcRebuildChargeTimeSet.class);
		Date date = new Date();
		example.createCriteria().andEqualTo("calendarId",calendarId)
				.andEqualTo("status",1)
				.andLessThanOrEqualTo("strattime",date)
				.andGreaterThanOrEqualTo("endtime",date);
		List<ElcRebuildChargeTimeSet> elcRebuildChargeTimeSets = elcRebuildChargeTimeSetDao.selectByExample(example);
		if (CollectionUtil.isEmpty(elcRebuildChargeTimeSets)){
			throw new ParameterValidateException("超过了规定的缴费时间，请在规定时间内缴费！");
		}
	}

}
