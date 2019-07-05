package com.server.edu.election.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElcClassEditAuthorityDao;
import com.server.edu.election.entity.ElcClassEditAuthority;
import com.server.edu.election.service.ElcClassEditAuthorityService;
import com.server.edu.exception.ParameterValidateException;

import tk.mybatis.mapper.entity.Example;
@Service
public class ElcClassEditAuthorityServiceImpl implements ElcClassEditAuthorityService {
	@Autowired
	private ElcClassEditAuthorityDao elcClassEditAuthorityDao;

	@Override
	public ElcClassEditAuthority getElcClassEditAuthority(Long calendarId) {
		Example example = new Example(ElcClassEditAuthority.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("calendarId", calendarId);
		ElcClassEditAuthority editAuthority = elcClassEditAuthorityDao.selectOneByExample(example);
		return editAuthority; 
	}
	
	@Override
	public int save(ElcClassEditAuthority elcClassEditAuthority) {
		int result = 0;
		if(elcClassEditAuthority.getId()!=null) {
			result = elcClassEditAuthorityDao.updateByPrimaryKeySelective(elcClassEditAuthority);
	        if (result <= Constants.ZERO)
	        {
	            throw new ParameterValidateException(
	                I18nUtil.getMsg("common.editError",I18nUtil.getMsg("election.elcNumberSwitch")));
	        }
		}else {
			result = elcClassEditAuthorityDao.insertSelective(elcClassEditAuthority);
	        if (result <= Constants.ZERO)
	        {
	            throw new ParameterValidateException(
	                I18nUtil.getMsg("common.editError",I18nUtil.getMsg("election.elcNumberSwitch")));
	        }
		}
		return result;
	}


}
