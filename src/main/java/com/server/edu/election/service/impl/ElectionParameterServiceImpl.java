package com.server.edu.election.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElectionParameterDao;
import com.server.edu.election.entity.ElectionParameter;
import com.server.edu.election.service.ElectionParameterService;
import com.server.edu.exception.ParameterValidateException;

public class ElectionParameterServiceImpl implements ElectionParameterService {

	@Autowired
	private ElectionParameterDao electionParameterDao;
	public int updateParameter(ElectionParameter electionParameter) {
		int result = electionParameterDao.updateByPrimaryKeySelective(electionParameter);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.editError",I18nUtil.getMsg("electionRuleDto.parameter")));
		}
		return result;
	}
}
