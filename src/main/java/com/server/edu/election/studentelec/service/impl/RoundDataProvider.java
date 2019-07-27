package com.server.edu.election.studentelec.service.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.server.edu.common.validator.Assert;
import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElecRoundsDao;
import com.server.edu.election.entity.ElcRoundCondition;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.rpc.BaseresServiceInvoker;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.service.cache.RoundCacheService;
import com.server.edu.election.studentelec.service.cache.RuleCacheService;
import com.server.edu.election.studentelec.service.cache.TeachClassCacheService;
import com.server.edu.election.studentelec.utils.Keys;
import com.server.edu.election.vo.ElectionRuleVo;
import com.server.edu.util.CollectionUtil;

/**
 * 提供roundId 到 教学任务json的提供者<br>
 * 每隔10分钟秒刷新轮次的教学任务json<br>
 * 每隔10分钟，清理已经过期的轮次
 */
@Component
@Lazy(false)
public class RoundDataProvider
{
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private ElecRoundsDao roundsDao;
    
    @Autowired
    private RoundCacheService roundCacheService;
    
    @Autowired
    private RuleCacheService ruleCacheService;
    
    @Autowired
    private TeachClassCacheService classCacheService;
    
    @Autowired
    private StringRedisTemplate strTemplate;
    
    public RoundDataProvider()
    {
    }
    
    @Scheduled(cron = "0 0/5 * * * *")
    public void load()
    {
        /*
         * roundId -> lessonId -> json
         */
        ValueOperations<String, String> ops = strTemplate.opsForValue();
        String dataLoadKey =
            String.format(Keys.STD_STATUS_LOCK, "dataLoad", "");
        Boolean setIfAbsent = ops.setIfAbsent(dataLoadKey,
            String.valueOf(System.currentTimeMillis()));
        if (!Boolean.TRUE.equals(setIfAbsent))
        {
            return;
        }
        try
        {
            // 缓存所有选课规则
            ruleCacheService.cacheAllRule();
            /** 一小时后即将开始的选课参数 */
            List<ElectionRounds> selectBeStart = roundsDao.selectWillBeStart();
            Set<String> keys = this.roundCacheService.getRoundKeys();
            Date now = new Date();
            Set<Long> calendarIds = new HashSet<>();
            for (ElectionRounds round : selectBeStart)
            {
                String id = round.getId().toString();
                if (keys.contains(id))
                {
                    keys.remove(id);
                }
                calendarIds.add(round.getCalendarId());
                this.cacheData(round, now);
            }
            
            if (CollectionUtil.isNotEmpty(keys))
            {
                this.roundCacheService.deleteRounds(keys.toArray());
            }
            // 缓存所有教学班
            for (Long calendarId : calendarIds)
            {
                classCacheService.cacheAllTeachClass(calendarId);
            }
            
        }
        finally
        {
            strTemplate.delete(dataLoadKey);
        }
    }
    
    /**
     * 更新或删除轮次缓存数据， 当轮次没有查询到时将删除缓存中的数据
     * 
     * @param round 轮次信息(不能为null)
     * @see [类、类#方法、类#成员]
     */
    public void updateRoundCache(Long roundId)
    {
        Assert.notNull(roundId, "roundId can not be null");
        Date now = new Date();
        ElectionRounds round = roundsDao.selectByPrimaryKey(roundId);
        if (round != null
            && Objects.equals(Constants.IS_OPEN, round.getOpenFlag())
            && Objects.equals(Constants.DELETE_FALSE, round.getDeleteStatus())
            && now.after(round.getBeginTime())
            && now.before(round.getEndTime()))
        {
            this.cacheData(round, now);
        }
        else
        {
            this.roundCacheService.deleteRound(roundId);
        }
    }
    
    private void cacheData(ElectionRounds round, Date now)
    {
        Long roundId = round.getId();
        Long calendarId = round.getCalendarId();
        Date endTime = round.getEndTime();
        long timeout =
            TimeUnit.MILLISECONDS.toMinutes(endTime.getTime() - now.getTime())
                + 3;
        // 缓存轮次数据
        roundCacheService.cacheRound(round, timeout);
        
        // 缓存轮次规则数据
        ruleCacheService.cacheRoundRule(roundId, timeout);
        //缓存轮次条件
        roundCacheService.cacheRoundCondition(roundId, timeout);
        //缓存轮次学生
        roundCacheService.cacheRoundStu(roundId, timeout);
        //缓存轮次的上一学期
        String manageDptId = round.getProjectId();
        if (StringUtils.equals(manageDptId, Constants.PROJ_UNGRADUATE))
        {
            cachePreSemester(round, timeout);
        }
        // 缓存课程
        roundCacheService
            .cacheCourse(timeout, roundId, calendarId, manageDptId);
    }
    
    /**
     * 缓存上一学期
     * 
     * @param ops
     * @param round 轮次
     * @param timeout
     * @see [类、类#方法、类#成员]
     */
    public void cachePreSemester(ElectionRounds round, long timeout)
    {
        ValueOperations<String, String> ops = strTemplate.opsForValue();
        Long calendarId = round.getCalendarId();//当前学期
        SchoolCalendarVo preSemester =
            BaseresServiceInvoker.getPreSemester(calendarId);
        Long id = preSemester.getId();
        String roundPreSemester = Keys.getRoundPresemesterKey(round.getId());
        ops.set(roundPreSemester, Long.toString(id), timeout, TimeUnit.MINUTES);
    }
    
    /**
     *  获取轮次对应的上一个学期
     */
    public String getPreSemester(Long roundId)
    {
        String key = Keys.getRoundPresemesterKey(roundId);
        String preSemester = strTemplate.opsForValue().get(key);
        return preSemester;
    }
    
    /**
     * 获取所有将要开始的轮次
     * 
     * @return
     */
    public List<ElectionRounds> getAllRound()
    {
        return roundCacheService.getAllRound();
    }
    
    /**
     * 获取轮次
     * 
     * @param roundId
     * @return
     */
    public ElectionRounds getRound(Long roundId)
    {
        return roundCacheService.getRound(roundId);
    }
    
    /**
     * 获取轮次的条件
     * 
     * @param roundId
     * @return
     */
    public ElcRoundCondition getRoundCondition(Long roundId)
    {
        return roundCacheService.getRoundCondition(roundId);
    }
    
    /**
     * 获取选择规则列表
     * 
     * @param roundId 轮次
     * @return
     */
    public List<ElectionRuleVo> getRules(Long roundId)
    {
        return ruleCacheService.getRules(roundId);
    }
    
    /**
     * 获取选课规则
     * 
     * @param roundId 轮次
     * @param serviceName 服务名
     * @return
     */
    public ElectionRuleVo getRule(String serviceName)
    {
        ElectionRuleVo vo = this.ruleCacheService.getRule(serviceName);
        return vo;
    }
    
    /**
     * 轮次是否包含指定规则
     * 
     * @param roundId
     * @param serviceName
     * @param strTemplate
     * @return
     * @see [类、类#方法、类#成员]
     */
    public boolean containsRule(Long roundId, String serviceName)
    {
        return this.ruleCacheService.containsRule(roundId, serviceName);
    }
    
    /**
     * 
     * 通过轮次与课程代码获取教学班信息
     * @param roundId
     * @param courseCode
     * @return
     */
    public List<TeachingClassCache> getTeachClasss(Long roundId,
        String courseCode)
    {
        return classCacheService.getTeachClasss(roundId, courseCode);
    }
    
    /**
     * 
     * 通过学年学期与课程代码获取教学班信息
     * @param calendarId
     * @param courseCode
     * @return
     */
    public List<TeachingClassCache> getTeachClasssbyCalendarId(Long calendarId,
    		String courseCode)
    {
    	return classCacheService.getTeachClasssBycalendarId(calendarId, courseCode);
    }
    
    /**
     * 获取指定教学班信息
     * 
     * @param calendarId 校历
     * @param teachClassId 教学班ID
     * @return
     */
    public TeachingClassCache getTeachClass(Long roundId, String courseCode,
        Long teachClassId)
    {
        return classCacheService
            .getTeachClass(roundId, courseCode, teachClassId);
    }
    
    /**
     * 获取教学班选课人数
     * 
     * @param teachClassId 教学班ID
     * @return
     * @see [类、类#方法、类#成员]
     */
    public Integer getElecNumber(Long teachClassId)
    {
        return classCacheService.getElecNumber(teachClassId);
    }
    
    /**
     * 增加教学班人数
     * 
     * @param teachClassId 教学班ID
     * @return
     * @see [类、类#方法、类#成员]
     */
    public int incrementElecNumber(Long teachClassId)
    {
        return classCacheService.incrementElecNumber(teachClassId);
    }
    /**
     * 减少教学班人数
     * 
     * @param teachClassId
     * @return
     * @see [类、类#方法、类#成员]
     */
    public int decrElcNumber(Long teachClassId)
    {
        return classCacheService.decrElecNumber(teachClassId);
    }
    
    /**
     * 判断学生是否在指定轮次中
     * 
     * @param roundId
     * @param studentId
     * @return
     * @see [类、类#方法、类#成员]
     */
    public boolean containsStu(Long roundId, String studentId)
    {
        return this.roundCacheService.containsStu(roundId, studentId);
    }
    
    /**
     * 判断学生的校区、学院、年级、专业、培养层次是否匹配轮次条件
     * 
     * @param roundId
     * @param studentId
     * @param projectId
     * @return
     * @see [类、类#方法、类#成员]
     */
    public boolean containsStuCondition(Long roundId, String studentId,
        String projectId)
    {
        return roundCacheService
            .containsStuCondition(roundId, studentId, projectId);
    }
    
}
