package com.server.edu.election.studentelec.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.server.edu.common.validator.Assert;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElecRoundCourseDao;
import com.server.edu.election.dao.ElecRoundsDao;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.dto.CourseOpenDto;
import com.server.edu.election.entity.ElcRoundCondition;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.entity.Student;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.utils.Keys;
import com.server.edu.election.studentelec.utils.RoundDataCacheUtil;
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
    private StringRedisTemplate strTemplate;
    
    @Autowired
    private ElecRoundsDao roundsDao;
    
    @Autowired
    private ElecRoundCourseDao roundCourseDao;
    
    @Autowired
    private RoundDataCacheUtil dataUtil;
    
    @Autowired
    private StudentDao studentDao;
    
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
            dataUtil.cacheAllRule(strTemplate);
            
            /** 一小时后即将开始的选课参数 */
            List<ElectionRounds> selectBeStart = roundsDao.selectWillBeStart();
            
            HashOperations<String, String, ElectionRounds> hash =
                RoundDataCacheUtil.opsRound();
            Set<String> keys = hash.keys(Keys.getRoundKey());
            
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
                hash.delete(Keys.getRoundKey(), keys.toArray());
            }
            // 缓存所有教学班
            for (Long calendarId : calendarIds)
            {
                cacheAllTeachClass(calendarId);
            }
            
        }
        finally
        {
            strTemplate.delete(dataLoadKey);
        }
    }
    
    private void cacheAllTeachClass(Long calendarId)
    {
        PageInfo<CourseOpenDto> page = new PageInfo<>();
        page.setNextPage(1);
        page.setHasNextPage(true);
        while (page.isHasNextPage())
        {
            PageHelper.startPage(page.getNextPage(), 300);
            List<CourseOpenDto> lessons =
                roundCourseDao.selectTeachingClassByCalendarId(calendarId);
            dataUtil.cacheTeachClass(100, lessons);
            
            page = new PageInfo<>(lessons);
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
            && now.after(round.getBeginTime())
            && now.before(round.getEndTime()))
        {
            this.cacheData(round, now);
        }
        else if (null != roundId)
        {
            HashOperations<String, String, ElectionRounds> hash =
                RoundDataCacheUtil.opsRound();
            hash.delete(Keys.getRoundKey(), roundId);
        }
    }
    
    private void cacheData(ElectionRounds round, Date now)
    {
        ValueOperations<String, String> ops = strTemplate.opsForValue();
        Long roundId = round.getId();
        Long calendarId = round.getCalendarId();
        
        Date endTime = round.getEndTime();
        long timeout =
            TimeUnit.MILLISECONDS.toMinutes(endTime.getTime() - now.getTime())
                + 3;
        // 缓存轮次数据
        dataUtil.cacheRound(round, timeout);
        // 缓存轮次规则数据
        dataUtil.cacheRoundRule(ops, roundId, timeout);
        //缓存轮次条件
        dataUtil.cacheRoundCondition(ops, roundId, timeout);
        //缓存轮次学生
        dataUtil.cacheRoundStu(strTemplate, roundId, timeout);
        //缓存轮次的上一学期
        dataUtil.cachePreSemester(ops, round, timeout);
        
        // 加载所有教学班与课程数据到缓存中
        List<CourseOpenDto> lessons = roundCourseDao
            .selectCorseRefTeachClassByRoundId(roundId, calendarId);
        
        Map<String, Set<Long>> courseClassMap = new HashMap<>();
        for (CourseOpenDto teachClasss : lessons)
        {
            String courseCode = teachClasss.getCourseCode();
            Long teachingClassId = teachClasss.getTeachingClassId();
            if (courseClassMap.containsKey(courseCode))
            {
                courseClassMap.get(courseCode).add(teachingClassId);
            }
            else
            {
                Set<Long> ids = new HashSet<>();
                ids.add(teachingClassId);
                courseClassMap.put(courseCode, ids);
            }
        }
        // 缓存课程
        dataUtil.cacheCourse(strTemplate, timeout, roundId, courseClassMap);
        
    }
    
    /**
     * 获取所有将要开始的轮次
     * 
     * @return
     */
    public List<ElectionRounds> getAllRound()
    {
        HashOperations<String, String, ElectionRounds> hash =
            RoundDataCacheUtil.opsRound();
        List<ElectionRounds> list = hash.values(Keys.getRoundKey());
        return list;
    }
    
    /**
     * 获取轮次
     * 
     * @param roundId
     * @return
     */
    public ElectionRounds getRound(Long roundId)
    {
        if (null == roundId)
        {
            return null;
        }
        HashOperations<String, String, ElectionRounds> hash =
            RoundDataCacheUtil.opsRound();
        ElectionRounds round = hash.get(Keys.getRoundKey(), roundId.toString());
        return round;
    }
    
    /**
     * 获取轮次的条件
     * 
     * @param roundId
     * @return
     */
    public ElcRoundCondition getRoundCondition(Long roundId)
    {
        ValueOperations<String, String> ops = strTemplate.opsForValue();
        String redisKey = Keys.getRoundConditionOne(roundId);
        String value = ops.get(redisKey);
        
        ElcRoundCondition round =
            JSON.parseObject(value, ElcRoundCondition.class);
        return round;
    }
    
    /**
     * 获取选择规则列表
     * 
     * @param roundId 轮次
     * @return
     */
    public List<ElectionRuleVo> getRules(Long roundId)
    {
        List<ElectionRuleVo> rules =
            this.dataUtil.getRules(roundId, strTemplate);
        return rules;
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
        ElectionRuleVo vo = this.dataUtil.getRule(serviceName, strTemplate);
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
        return this.dataUtil.containsRule(roundId, serviceName, strTemplate);
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
        List<TeachingClassCache> lessons = new ArrayList<>();
        
        ValueOperations<String, String> ops = strTemplate.opsForValue();
        
        List<Long> teachClassIds =
            this.dataUtil.getTeachClassIds(roundId, courseCode, ops);
        if (CollectionUtil.isEmpty(teachClassIds))
        {
            return lessons;
        }
        
        if (CollectionUtil.isNotEmpty(teachClassIds))
        {
            Collections.sort(teachClassIds);
            
            List<String> keys = teachClassIds.stream()
                .map(String::valueOf)
                .collect(Collectors.toList());
            
            HashOperations<String, String, TeachingClassCache> hash =
                RoundDataCacheUtil.opsTeachClass();
            lessons = hash.multiGet(Keys.getClassKey(), keys);
        }
        return lessons;
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
        if (roundId == null || StringUtils.isBlank(courseCode)
            || teachClassId == null)
        {
            logger.warn(
                "---- roundId, courseCode and teachClassId can not be null ----");
            return null;
        }
        
        ValueOperations<String, String> ops = strTemplate.opsForValue();
        List<Long> teachClassIds =
            this.dataUtil.getTeachClassIds(roundId, courseCode, ops);
        
        if (CollectionUtil.isEmpty(teachClassIds)
            || !teachClassIds.contains(teachClassId))
        {
            return null;
        }
        HashOperations<String, String, TeachingClassCache> hash =
            RoundDataCacheUtil.opsTeachClass();
        
        TeachingClassCache lesson =
            hash.get(Keys.getClassKey(), teachClassId.toString());
        return lesson;
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
        if (teachClassId == null)
        {
            return 0;
        }
        HashOperations<String, String, Integer> opsClassNum =
            RoundDataCacheUtil.opsClassNum();
        Integer num = opsClassNum.get(Keys.getClassElecNumberKey(),
            teachClassId.toString());
        return num;
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
        if (teachClassId == null)
        {
            return 0;
        }
        HashOperations<String, String, Integer> opsClassNum =
            RoundDataCacheUtil.opsClassNum();
        return opsClassNum
            .increment(Keys.getClassElecNumberKey(), teachClassId.toString(), 1)
            .intValue();
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
        return this.dataUtil.containsStu(strTemplate, roundId, studentId);
    }
    
    /**
     * 判断学生的校区、学院、年级、专业、培养层次是否匹配轮次条件
     * 
     * @param roundId
     * @param studentId
     * @return
     * @see [类、类#方法、类#成员]
     */
    public boolean containsStuCondition(Long roundId, String studentId)
    {
        if (null == roundId || StringUtils.isBlank(studentId))
        {
            return false;
        }
        Student student = null;
        
        HashOperations<String, String, String> ops = strTemplate.opsForHash();
        String key = "elecStudentTempRedisKey";
        String text = ops.get(key, studentId);
        if (StringUtils.isBlank(text))
        {
            student = studentDao.selectByPrimaryKey(studentId);
            if (null == student)
            {
                return false;
            }
            ops.put(key, studentId, JSON.toJSONString(student));
            strTemplate.expire(key, 1, TimeUnit.HOURS);
        }
        else
        {
            student = JSON.parseObject(text, Student.class);
        }
        
        ElcRoundCondition roundsCondition = getRoundCondition(roundId);
        if (roundsCondition != null)
        {
            if (roundsCondition.getCampus().contains(student.getCampus())
                && roundsCondition.getFacultys().contains(student.getFaculty())
                && roundsCondition.getGrades()
                    .contains(student.getGrade().toString())
                && roundsCondition.getMajors().contains(student.getProfession())
                && roundsCondition.getTrainingLevels()
                    .contains(student.getTrainingLevel()))
            {
                return true;
            }
        }
        return false;
    }
    
}
