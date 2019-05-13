package com.server.edu.election.studentelec.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.server.edu.common.validator.Assert;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElecRoundCourseDao;
import com.server.edu.election.dao.ElecRoundsDao;
import com.server.edu.election.dao.ElectionRuleDao;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.dto.CourseOpenDto;
import com.server.edu.election.entity.ElcRoundCondition;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.entity.ElectionRule;
import com.server.edu.election.entity.Student;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.utils.Keys;
import com.server.edu.election.studentelec.utils.RoundDataCacheUtil;
import com.server.edu.election.vo.ElectionRuleVo;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;

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
    private RedisTemplate<String, String> redisTemplate;
    
    @Autowired
    private RedisTemplate<String, Integer> elecNumRedis;
    
    @Autowired
    private ElecRoundsDao roundsDao;
    
    @Autowired
    private ElecRoundCourseDao roundCourseDao;
    
    @Autowired
    private RoundDataCacheUtil dataUtil;
    
    @Autowired
    private StudentDao studentDao;
    
    @Autowired 
    private ElectionRuleDao electionRuleDao;
    
    public RoundDataProvider()
    {
    }
    
    @Scheduled(cron = "0 0/5 * * * *")
    public void load()
    {
        /*
         * roundId -> lessonId -> json
         */
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        
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
            /** 一小时后即将开始的选课参数 */
            List<ElectionRounds> selectBeStart = roundsDao.selectWillBeStart();
            
            Date now = new Date();
            Set<String> keys = redisTemplate.keys(Keys.getRoundKeyPattern());
            Set<String> deleteKeys = new HashSet<>();
            for (ElectionRounds round : selectBeStart)
            {
                deleteKeys.addAll(cacheData(round, now, keys));
            }
            deleteKeys.addAll(keys);
            
            if (CollectionUtil.isNotEmpty(deleteKeys))
            {
                redisTemplate.delete(deleteKeys);
            }
        }
        finally
        {
            redisTemplate.delete(dataLoadKey);
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
        Set<String> keys = new HashSet<>();
        ElectionRounds round = roundsDao.selectByPrimaryKey(roundId);
        if (round != null
            && Objects.equals(Constants.IS_OPEN, round.getOpenFlag())
            && now.after(round.getBeginTime())
            && now.before(round.getEndTime())
            )
        {
            Set<String> deleteKeys = cacheData(round, now, keys);
            if (CollectionUtil.isNotEmpty(deleteKeys))
            {
                redisTemplate.delete(deleteKeys);
            }
        }
        else
        {
            String key = Keys.getRoundKey(roundId);
            redisTemplate.delete(key);
            key = Keys.getRoundConditionOne(roundId);
            redisTemplate.delete(key);
        }
    }
    
    private Set<String> cacheData(ElectionRounds round, Date now,
        Set<String> keys)
    {
        Set<String> deleteKeys = new HashSet<>();
        
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        Long roundId = round.getId();
        Long calendarId = round.getCalendarId();
        
        String key = Keys.getRoundKey(roundId);
        // 移除掉有效的key剩下的就是无效数据
        if (keys.contains(key))
        {
            keys.remove(key);
        }
        
        Date endTime = round.getEndTime();
        long endMinutes =
            TimeUnit.MILLISECONDS.toMinutes(endTime.getTime() - now.getTime())
                + 3;
        
        // 缓存轮次信息
        ops.set(key, JSON.toJSONString(round), endMinutes, TimeUnit.MINUTES);
        // 缓存轮次规则数据
//        Set<String> ruleKeys =
//            redisTemplate.keys(Keys.getRoundRuleKeyPattern(roundId));
        dataUtil.cacheRoundRule(ops, roundId, endMinutes);
        //缓存轮次学生
        Set<String> stuKeys =
            redisTemplate.keys(Keys.getRoundStuPattern(roundId));
        dataUtil.cacheRoundStu(ops, roundId, endMinutes, stuKeys);
        //缓存轮次的上一学期
        dataUtil.cachePreSemester(ops, round, endMinutes);
        
        // 加载所有教学班与课程数据到缓存中
        List<CourseOpenDto> lessons =
            roundCourseDao.selectTeachingClassByRoundId(roundId, calendarId);
        //缓存轮次条件
        Set<String> conKeys =
            redisTemplate.keys(Keys.getRoundCondition());
        dataUtil.cacheRoundCondition(ops,roundId, endMinutes,conKeys);
        Map<String, List<CourseOpenDto>> collect = lessons.stream()
            .collect(Collectors.groupingBy(CourseOpenDto::getCourseCode));
        Set<String> keySet = collect.keySet();
        
        Set<String> courseKeys =
            redisTemplate.keys(Keys.getRoundCoursePattern(roundId));
        for (String courseCode : keySet)
        {
            List<CourseOpenDto> teachClasss = collect.get(courseCode);
            Set<Long> teachClassIds = dataUtil.cacheTeachClass(ops,
                endMinutes,
                calendarId,
                teachClasss);
            
            dataUtil.cacheCourse(ops,
                endMinutes,
                roundId,
                courseCode,
                teachClassIds,
                courseKeys);
        }
        deleteKeys.addAll(stuKeys);
        deleteKeys.addAll(conKeys);
        return deleteKeys;
    }
    
    /**
     * 获取所有将要开始的轮次
     * 
     * @return
     */
    public List<ElectionRounds> getAllRound()
    {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        Set<String> keys = redisTemplate.keys(Keys.getRoundKeyPattern());
        
        List<String> ks = new ArrayList<>(keys);
        Collections.sort(ks);
        
        List<String> texts = ops.multiGet(ks);
        
        List<ElectionRounds> list = new ArrayList<>();
        for (String str : texts)
        {
            ElectionRounds round = JSON.parseObject(str, ElectionRounds.class);
            list.add(round);
        }
        
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
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String redisKey = Keys.getRoundKey(roundId);
        String string = ops.get(redisKey);
        
        ElectionRounds round = JSON.parseObject(string, ElectionRounds.class);
        return round;
    }
    
    /**
     * 获取所有将要开始的轮次的条件
     * 
     * @return
     */
    public List<ElcRoundCondition> getAllRoundCondition()
    {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        Set<String> keys = redisTemplate.keys(Keys.getRoundCondition());
        
        List<String> ks = new ArrayList<>(keys);
        Collections.sort(ks);
        
        List<String> texts = ops.multiGet(ks);
        
        List<ElcRoundCondition> list = new ArrayList<>();
        for (String str : texts)
        {
        	ElcRoundCondition round = JSON.parseObject(str, ElcRoundCondition.class);
            list.add(round);
        }
        
        return list;
    }
    
    /**
     * 获取轮次的条件
     * 
     * @param roundId
     * @return
     */
    public ElcRoundCondition getRoundCondition(Long roundId)
    {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String redisKey = Keys.getRoundConditionOne(roundId);
        String value = ops.get(redisKey);
        
        ElcRoundCondition round = JSON.parseObject(value, ElcRoundCondition.class);
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
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        
//        Set<String> keys =
//            redisTemplate.keys(Keys.getRoundRuleKeyPattern(roundId));
        String key  = Keys.getRoundRuleServiceKey(roundId);
        List<String> list = JSON.parseArray(ops.get(key), String.class);
        List<ElectionRuleVo> lessons = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(list))
        {
//            for (String json : list)
//            {
//                ElectionRuleVo lesson =
//                    JSON.parseObject(json, ElectionRuleVo.class);
//                lessons.add(lesson);
//            }
        	Example example = new Example(ElectionRule.class);
        	Example.Criteria criteria = example.createCriteria();
        	criteria.andIn("serviceName", list);
        	List<ElectionRule> rules =electionRuleDao.selectByExample(example);
        	for(ElectionRule electionRule:rules) {
        		ElectionRuleVo vo = new ElectionRuleVo();
        		BeanUtils.copyProperties(electionRule, vo);
        		lessons.add(vo);
        	}
        }
        return lessons;
    }
    
    /**
     * 获取选课规则
     * 
     * @param roundId 轮次
     * @param serviceName 服务名
     * @return
     */
    public ElectionRuleVo getRule(Long roundId, String serviceName)
    {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        
        String text = ops.get(Keys.getRoundRuleKey(roundId, serviceName));
        
        ElectionRuleVo vo = JSON.parseObject(text, ElectionRuleVo.class);
        return vo;
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
        String roundCourseKey = Keys.getRoundCourseKey(roundId, courseCode);
        
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        
        String text = ops.get(roundCourseKey);
        if (StringUtils.isEmpty(text))
        {
            return lessons;
        }
        
        List<Long> teachClassIds = JSON.parseArray(text, Long.class);
        if (CollectionUtil.isEmpty(teachClassIds))
        {
            return lessons;
        }
        List<String> keys = new ArrayList<>();
        //得到轮次
        ElectionRounds round = this.getRound(roundId);
        Long calendarId = round.getCalendarId();
        for (Long teachClassId : teachClassIds)
        {
            String classKey = Keys.getClassKey(calendarId, teachClassId);
            keys.add(classKey);
        }
        
        if (CollectionUtil.isNotEmpty(keys))
        {
            Collections.sort(keys);
            
            List<String> list = ops.multiGet(keys);
            if (CollectionUtil.isNotEmpty(list))
            {
                for (String json : list)
                {
                    if (StringUtils.isNotBlank(json))
                    {
                        TeachingClassCache lesson =
                            JSON.parseObject(json, TeachingClassCache.class);
                        lessons.add(lesson);
                    }
                }
            }
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
    public TeachingClassCache getTeachClass(Long calendarId, Long teachClassId)
    {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String classKey = Keys.getClassKey(calendarId, teachClassId);
        String string = ops.get(classKey);
        
        TeachingClassCache lesson =
            JSON.parseObject(string, TeachingClassCache.class);
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
        Integer num = this.elecNumRedis.opsForValue()
            .get(Keys.getClassElecNumberKey(teachClassId));
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
        return this.elecNumRedis.opsForValue()
            .increment(Keys.getClassElecNumberKey(teachClassId), 1)
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
        String roundStuKey = Keys.getRoundStuKey(roundId, studentId);
        String stuId = redisTemplate.opsForValue().get(roundStuKey);
        return StringUtils.isNotBlank(stuId);
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
    	Student student = studentDao.selectByPrimaryKey(studentId);
    	ElcRoundCondition roundsCondition=getRoundCondition(roundId);
        if(roundsCondition!=null) {
        	if(roundsCondition.getCampus().contains(student.getCampus())
        			&&roundsCondition.getFacultys().contains(student.getFaculty())
        			&&roundsCondition.getGrades().contains(student.getGrade().toString())
        			&&roundsCondition.getMajors().contains(student.getProfession())
        			&&roundsCondition.getTrainingLevels().contains(student.getTrainingLevel())) {
        		return true;
        	}
        }
        return false;
    }
    
}
