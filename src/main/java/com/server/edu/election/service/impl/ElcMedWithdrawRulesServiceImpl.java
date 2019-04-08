package com.server.edu.election.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElcMedWithdrawRulesDao;
import com.server.edu.election.entity.ElcMedWithdrawRules;
import com.server.edu.election.service.ElcMedWithdrawRulesService;
import com.server.edu.election.vo.ElcMedWithdrawRulesVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;
@Service
public class ElcMedWithdrawRulesServiceImpl implements ElcMedWithdrawRulesService {
	@Autowired
	private ElcMedWithdrawRulesDao elcMedWithdrawRulesDao;
	@Override
	public PageInfo<ElcMedWithdrawRules> list(PageCondition<ElcMedWithdrawRules> condition){
		PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
		Example example = new Example(ElcMedWithdrawRules.class);
		example.setOrderByClause("CREATED_AT_ DESC");
		Example.Criteria criteria = example.createCriteria();
		if(condition.getCondition().getProjectId()!=null) {
			criteria.andEqualTo("projectId",condition.getCondition().getProjectId());
		}
		if(condition.getCondition().getCalendarId()!=null) {
			criteria.andEqualTo("calendarId",condition.getCondition().getCalendarId());
		}
		if(StringUtils.isNotBlank(condition.getCondition().getName())) {
			criteria.andLike("name",condition.getCondition().getName());
		}
		List<ElcMedWithdrawRules> list = elcMedWithdrawRulesDao.selectByExample(example);
		PageInfo<ElcMedWithdrawRules> pageInfo =new PageInfo<>(list);
		return pageInfo;
	}
	
	@Override
	public int add(ElcMedWithdrawRules elcMedWithdrawRules) {
		if(elcMedWithdrawRules.getBeginTime().getTime()>elcMedWithdrawRules.getEndTime().getTime()) {
			throw new ParameterValidateException(I18nUtil.getMsg("elcMedWithdraw.ruleTimeError"));
		}
		Example example = new Example(ElcMedWithdrawRules.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("projectId",elcMedWithdrawRules.getProjectId());
		criteria.andEqualTo("calendarId",elcMedWithdrawRules.getCalendarId());
		criteria.andEqualTo("name",elcMedWithdrawRules.getName());
		List<ElcMedWithdrawRules> list = elcMedWithdrawRulesDao.selectByExample(example);
		if(CollectionUtil.isNotEmpty(list)) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.exist",I18nUtil.getMsg("elcMedWithdraw.rule")));
		}
		int result = elcMedWithdrawRulesDao.insertSelective(elcMedWithdrawRules);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.saveError",I18nUtil.getMsg("elcMedWithdraw.rule")));
		}
		return result;
	}
	
	@Override
	public int update(ElcMedWithdrawRules elcMedWithdrawRules) {
		if(elcMedWithdrawRules.getId()==null) {
			throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
		}
		if(elcMedWithdrawRules.getBeginTime().getTime()>elcMedWithdrawRules.getEndTime().getTime()) {
			throw new ParameterValidateException(I18nUtil.getMsg("elcMedWithdraw.ruleTimeError"));
		}
		Example example = new Example(ElcMedWithdrawRules.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andNotEqualTo("id", elcMedWithdrawRules.getId());
		criteria.andEqualTo("name",elcMedWithdrawRules.getName());
		List<ElcMedWithdrawRules> list = elcMedWithdrawRulesDao.selectByExample(example);
		if(CollectionUtil.isNotEmpty(list)) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.exist",I18nUtil.getMsg("elcMedWithdraw.rule")));
		}
		int result = elcMedWithdrawRulesDao.updateByPrimaryKeySelective(elcMedWithdrawRules);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.editError",I18nUtil.getMsg("elcMedWithdraw.rule")));
		}
		return result;
	}
	
	@Override
	public int delete(List<Long> ids) {
		Example example = new Example(ElcMedWithdrawRules.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andIn("id", ids);
		int result = elcMedWithdrawRulesDao.deleteByExample(example);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.failSuccess",I18nUtil.getMsg("elcMedWithdraw.rule")));
		}
		return result;
	}
	
	@Override
	public ElcMedWithdrawRulesVo getRule(Long id) {
		ElcMedWithdrawRulesVo elcMedWithdrawRulesVo = new ElcMedWithdrawRulesVo();
		ElcMedWithdrawRules elcMedWithdrawRules = elcMedWithdrawRulesDao.selectByPrimaryKey(id);
		if(elcMedWithdrawRules!=null) {
			BeanUtils.copyProperties(elcMedWithdrawRules, elcMedWithdrawRulesVo);
			elcMedWithdrawRulesVo.setBeginTimeL(elcMedWithdrawRulesVo.getBeginTime().getTime());
			elcMedWithdrawRulesVo.setEndTimeL(elcMedWithdrawRulesVo.getEndTime().getTime());
		}
		return elcMedWithdrawRulesVo;
	}
}
