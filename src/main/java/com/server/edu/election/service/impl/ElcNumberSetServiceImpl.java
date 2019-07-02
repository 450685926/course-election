package com.server.edu.election.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.TeachingClassDao;
import com.server.edu.election.dto.ElcNumberSetDto;
import com.server.edu.election.entity.ElcNumberSet;
import com.server.edu.election.service.ElcNumberSetService;
import com.server.edu.election.vo.TeachingClassVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.util.CollectionUtil;
@Service
public class ElcNumberSetServiceImpl implements ElcNumberSetService {
	@Autowired
	private TeachingClassDao teachingClassDao;
	@Override
	@Transactional
	public int releaseAll(Long calendarId) {
		// TODO Auto-generated method stub
		List<Integer> turns = new ArrayList<>();
		turns.add(Constants.THIRD_TURN);
		turns.add(Constants.FOURTH_TURN);
		ElcNumberSetDto elcNumberSetDto = new ElcNumberSetDto();
		elcNumberSetDto.setCalendarId(calendarId);
		elcNumberSetDto.setTurns(turns);
		int result = 0;
		List<TeachingClassVo> list = teachingClassDao.selectDrawClasss(elcNumberSetDto);
		if(CollectionUtil.isNotEmpty(list)) {
			result =teachingClassDao.batchDecrElcNumber(list);
		}
        if (result <= Constants.ZERO)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("elcNumberSet.releaseFail"));
        }
		return result;
	}

	@Override
	public int save(ElcNumberSet elcNumberSet) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ElcNumberSet getElcNumberSetInfo(Long calendarId) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
