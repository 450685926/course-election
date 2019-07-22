package com.server.edu.election.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElcNumberSetDao;
import com.server.edu.election.dao.TeachingClassDao;
import com.server.edu.election.dto.ElcNumberSetDto;
import com.server.edu.election.entity.ElcNumberSet;
import com.server.edu.election.service.ElcNumberSetService;
import com.server.edu.election.vo.TeachingClassVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.util.CollectionUtil;
import com.server.edu.util.DateUtil;

import tk.mybatis.mapper.entity.Example;
@Service
@Primary
public class ElcNumberSetServiceImpl implements ElcNumberSetService {
	@Autowired
	private TeachingClassDao teachingClassDao;
	@Autowired
	private ElcNumberSetDao elcNumberSetDao;
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
		int result = 0;
		if(elcNumberSet.getId()!=null) {
			result = elcNumberSetDao.updateByPrimaryKeySelective(elcNumberSet);
	        if (result <= Constants.ZERO)
	        {
	            throw new ParameterValidateException(
	                I18nUtil.getMsg("common.editError",I18nUtil.getMsg("election.elcNumberSet")));
	        }
		}else {
			result = elcNumberSetDao.insertSelective(elcNumberSet);
	        if (result <= Constants.ZERO)
	        {
	            throw new ParameterValidateException(
	                I18nUtil.getMsg("common.saveError",I18nUtil.getMsg("election.elcNumberSet")));
	        }
		}
        if(Constants.IS_OPEN==elcNumberSet.getStatus()) {
    		Runnable runnable = new Runnable() {
	  		    @Override
	  			public void run() {
	  		    	  releaseAll(elcNumberSet.getCalendarId());
	  		    }
    		};
  		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
  		executeEightAtNightPerDay(runnable,df.format(elcNumberSet.getFirstTime()));
  		executeEightAtNightPerDay(runnable,df.format(elcNumberSet.getSecondTime()));
        }
		return result;
	}
	
	/** 
	 * 每天指定时间执行
	 * 每天定时安排任务进行执行 
	 */  
	public static void executeEightAtNightPerDay(Runnable runnable,String time) { 
	    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);  
	    long oneDay = 24 * 60 * 60 * 1000;  
	    long initDelay  =DateUtil.getTimeMillis(time) - System.currentTimeMillis();  
	    initDelay = initDelay > 0 ? initDelay : oneDay + initDelay;  
	    executor.scheduleAtFixedRate(  
	    		runnable,  
	            initDelay,  
	            oneDay,  
	            TimeUnit.MILLISECONDS);  
	}  

	@Override
	public ElcNumberSet getElcNumberSetInfo(Long calendarId) {
		Example example = new Example(ElcNumberSet.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("calendarId", calendarId);
		ElcNumberSet elcNumberSet =elcNumberSetDao.selectOneByExample(example);
		return elcNumberSet;
	}
	

}
