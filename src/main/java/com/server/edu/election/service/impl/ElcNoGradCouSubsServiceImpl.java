package com.server.edu.election.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElcNoGradCouSubsDao;
import com.server.edu.election.entity.ElcNoGradCouSubs;
import com.server.edu.election.service.ElcNoGradCouSubsService;
import com.server.edu.election.vo.ElcNoGradCouSubsVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;
@Service
public class ElcNoGradCouSubsServiceImpl implements ElcNoGradCouSubsService {
	@Autowired
	private ElcNoGradCouSubsDao elcNoGradCouSubsDao;
	@Override
	public PageInfo<ElcNoGradCouSubsVo> page(PageCondition<ElcNoGradCouSubs> condition) {
		ElcNoGradCouSubs dto = condition.getCondition();
		dto.setProjectId(dto.getProjectId());
		PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
		List<ElcNoGradCouSubsVo> list = elcNoGradCouSubsDao.selectElcNoGradCouSubs(dto);
		if(CollectionUtil.isNotEmpty(list)) {
			for(ElcNoGradCouSubsVo vo:list) {
				vo.setOrigsCourseInfo(vo.getOrigsCourseId()+vo.getOrigsCourseName()+"("+vo.getOrigsCredits()+")");
				vo.setSubCourseInfo(vo.getSubCourseId()+vo.getSubCourseName()+"("+vo.getSubCredits()+")");
			}
		}
		PageInfo<ElcNoGradCouSubsVo> pageInfo = new PageInfo<>(list);
		return pageInfo;
	}

	@Override
	@Transactional
	public int add(ElcNoGradCouSubs elcNoGradCouSubs) {
		Example example = new Example(ElcNoGradCouSubs.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("projectId", elcNoGradCouSubs.getProjectId());
		criteria.andEqualTo("calendarId", elcNoGradCouSubs.getCalendarId());
		criteria.andEqualTo("origsCourseId", elcNoGradCouSubs.getOrigsCourseId());
		criteria.andEqualTo("subCourseId", elcNoGradCouSubs.getSubCourseId());
		ElcNoGradCouSubs subs = elcNoGradCouSubsDao.selectOneByExample(example);
		if(subs!=null) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.exist",I18nUtil.getMsg("election.elcNoGradCouSubs")));
		}
		elcNoGradCouSubs.setCreatedAt(new Date());
		int result = elcNoGradCouSubsDao.insertSelective(elcNoGradCouSubs);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.saveError",I18nUtil.getMsg("election.elcNoGradCouSubs")));
		}
		return result;
	}

	@Override
	@Transactional
	public int update(ElcNoGradCouSubs elcNoGradCouSubs) {
		if(elcNoGradCouSubs.getId()==null) {
			throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
		}
		List<Long> ids = new ArrayList<>();
		ids.add(elcNoGradCouSubs.getId());
		Example example = new Example(ElcNoGradCouSubs.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andNotIn("id", ids);
		criteria.andEqualTo("calendarId", elcNoGradCouSubs.getCalendarId());
		criteria.andEqualTo("origsCourseId", elcNoGradCouSubs.getOrigsCourseId());
		criteria.andEqualTo("subCourseId", elcNoGradCouSubs.getSubCourseId());
		ElcNoGradCouSubs subs = elcNoGradCouSubsDao.selectOneByExample(example);
		elcNoGradCouSubs.setUpdatedAt(new Date());
		if(subs!=null) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.exist",I18nUtil.getMsg("election.elcNoGradCouSubs")));
		}
		int result = elcNoGradCouSubsDao.updateByPrimaryKeySelective(elcNoGradCouSubs);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.editError",I18nUtil.getMsg("election.elcNoGradCouSubs")));
		}
		return result;
	}
	
	@Override
	@Transactional
	public int delete(List<Long> ids) {
		Example example = new Example(ElcNoGradCouSubs.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andIn("id", ids);
		List<ElcNoGradCouSubs> list = elcNoGradCouSubsDao.selectByExample(example);
		if(CollectionUtil.isEmpty(list)) {
			throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
		}
		int result = elcNoGradCouSubsDao.deleteByExample(example);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.failSuccess",I18nUtil.getMsg("election.elcNoGradCouSubs")));
		}
		return result;
	}

}
