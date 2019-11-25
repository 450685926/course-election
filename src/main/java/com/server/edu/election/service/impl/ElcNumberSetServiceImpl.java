package com.server.edu.election.service.impl;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.util.RedisLockUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElcNumberSetDao;
import com.server.edu.election.dao.TeachingClassDao;
import com.server.edu.election.dto.ElcNumberSetDto;
import com.server.edu.election.entity.ElcNumberSet;
import com.server.edu.election.service.ElcNumberSetService;
import com.server.edu.election.studentelec.service.cache.TeachClassCacheService;
import com.server.edu.election.util.TableIndexUtil;
import com.server.edu.election.vo.ElcLogVo;
import com.server.edu.election.vo.TeachingClassVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.util.CollectionUtil;
import com.server.edu.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Primary
@Lazy(false)
public class ElcNumberSetServiceImpl implements ElcNumberSetService
{
    private static final Logger log =
        LoggerFactory.getLogger(ElcNumberSetServiceImpl.class);
    
    @Autowired
    private TeachingClassDao teachingClassDao;
    
    @Autowired
    private ElcNumberSetDao elcNumberSetDao;
    
    @Autowired
    private ThreadPoolTaskExecutor poolTaskExecutor;
    
    @Autowired
    private TeachClassCacheService teachClassCacheService;
    
    @Override
    @Transactional
    public int releaseAll(Long calendarId)
    {
        List<Integer> turns = new ArrayList<>();
        turns.add(Constants.THIRD_TURN);
        turns.add(Constants.FOURTH_TURN);
        ElcNumberSetDto elcNumberSetDto = new ElcNumberSetDto();
        elcNumberSetDto.setCalendarId(calendarId);
        elcNumberSetDto.setTurns(turns);
        elcNumberSetDto.setType(ElcLogVo.TYPE_2);
        int result = 0;
        log.info("start select list");
		elcNumberSetDto.setIndex(TableIndexUtil.getIndex(calendarId));
        List<TeachingClassVo> list =
            teachingClassDao.selectDrawClasss(elcNumberSetDto);
        if (CollectionUtil.isNotEmpty(list))
        { 
        	List<TeachingClassVo> decrElcNumberList =list.stream().filter(c->c.getId()!=null).collect(Collectors.toList());
        	if(CollectionUtil.isNotEmpty(decrElcNumberList)) {
                result = teachingClassDao.batchClearDrawNumber(decrElcNumberList);
                log.info("end clear data sucesess");
                if(result>0) {
                	Map<String, Integer> drawMap = new HashMap<>();
                	for(TeachingClassVo vo:decrElcNumberList) {
                		// 释放第三、四轮退课人数
                        teachClassCacheService.updateDrawNumber(vo.getId());
                        drawMap.put(vo.getId().toString(), Constants.ZERO);
                	}
                	teachClassCacheService.clearWitdhDrawNumber(drawMap);
                }
        	}
        }
        if (result < Constants.ZERO)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("elcNumberSet.releaseFail"));
        }
        return result;
    }
    
    @Override
    public int save(ElcNumberSet elcNumberSet)
    {
        int result = 0;
        if (elcNumberSet.getId() != null)
        {
            result = elcNumberSetDao.updateByPrimaryKeySelective(elcNumberSet);
            if (result <= Constants.ZERO)
            {
                throw new ParameterValidateException(
                    I18nUtil.getMsg("common.editError",
                        I18nUtil.getMsg("election.elcNumberSet")));
            }
        }
        else
        {
            result = elcNumberSetDao.insertSelective(elcNumberSet);
            if (result <= Constants.ZERO)
            {
                throw new ParameterValidateException(
                    I18nUtil.getMsg("common.saveError",
                        I18nUtil.getMsg("election.elcNumberSet")));
            }
        }
        return result;
    }
    
    /** 
     * 每天指定时间执行
     * 每天定时安排任务进行执行 
     */
    @Scheduled(cron = "0 0/1 * * * *")
    public void executeEightAtNightPerDay()
    {
        String key = "ElcNumberSetTimer";
        if(RedisLockUtil.tryAcquire(key)) {
            try
            {
                List<ElcNumberSet> selectAll = elcNumberSetDao.selectAll();
                
                if (CollectionUtil.isNotEmpty(selectAll))
                {
                    String timeStr = DateUtil.format(new Date(), "HH:mm");
                    for (ElcNumberSet elcNumberSet : selectAll)
                    {
                        if (Constants.IS_OPEN == elcNumberSet.getStatus()
                            && (timeStr.equals(elcNumberSet.getFirstTime())
                                || timeStr.equals(elcNumberSet.getSecondTime())))
                        {
                            poolTaskExecutor.execute(() -> {
                                releaseAll(elcNumberSet.getCalendarId());
                            });
                        }
                    }
                }
            }
            finally
            {
                RedisLockUtil.release(key);
            }
        }
    }
    
    @Override
    public ElcNumberSet getElcNumberSetInfo(Long calendarId)
    {
        Example example = new Example(ElcNumberSet.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("calendarId", calendarId);
        ElcNumberSet elcNumberSet = elcNumberSetDao.selectOneByExample(example);
        return elcNumberSet;
    }
    
}
