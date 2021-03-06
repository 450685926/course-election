package com.server.edu.election.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElectionParameterDao;
import com.server.edu.election.entity.ElectionParameter;
import com.server.edu.election.service.ElectionParameterService;
import com.server.edu.exception.ParameterValidateException;

@Service
public class ElectionParameterServiceImpl implements ElectionParameterService {

	@Autowired
	private ElectionParameterDao electionParameterDao;
	@Override
	public int updateParameter(ElectionParameter electionParameter) {
		ElectionParameter parameter = electionParameterDao.selectByPrimaryKey(electionParameter.getId());
		if(parameter==null) {
			throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
		}
		if(parameter.getValue().equals("true")||parameter.getValue().equals("false")) {
			if(electionParameter.getStatus().equals(1)) {
				electionParameter.setValue("true");
			}else if (electionParameter.getStatus().equals(0)) {
				electionParameter.setValue("false");
			}
		}
		int result = electionParameterDao.updateByPrimaryKeySelective(electionParameter);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.editError",I18nUtil.getMsg("electionRuleDto.parameter")));
		}
		return result;
	}
}
