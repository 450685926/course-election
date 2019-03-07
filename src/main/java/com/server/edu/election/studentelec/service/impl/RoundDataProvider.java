package com.server.edu.election.studentelec.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.server.edu.election.dao.ElecRoundCourseDao;
import com.server.edu.election.dao.ElecRoundsDao;
import com.server.edu.election.dao.ElectionParameterDao;
import com.server.edu.election.dao.ElectionRuleDao;
import com.server.edu.election.dto.CourseOpenDto;
import com.server.edu.election.entity.ElectionParameter;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.studentelec.cache.CourseCache;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
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
    private RedisTemplate<String, String> redisTemplate;
    
    @Autowired
    private ElecRoundsDao roundsDao;
    
    @Autowired
    private ElectionRuleDao ruleDao;
    
    @Autowired
    private ElectionParameterDao parameterDao;
    
    @Autowired
    private ElecRoundCourseDao roundCourseDao;
    
    public RoundDataProvider()
    {
    }
    
    @Scheduled(cron = "0 0/2 * * * *")
    public void load()
    {
        /*
         * roundId -> lessonId -> json
         */
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        /** 一小时后即将开始的选课参数 */
        List<ElectionRounds> selectBeStart = roundsDao.selectWillBeStart();
        
        Date now = new Date();
        Set<String> keys =
            redisTemplate.keys(String.format(Keys.ROUND_KEY, "*"));
        for (ElectionRounds round : selectBeStart)
        {
            Long roundId = round.getId();
            String key = String.format(Keys.ROUND_KEY, roundId);
            // 不存在的数据需要删除的Key
            if (keys.contains(key))
            {
                keys.remove(key);
            }
            
            Date endTime = round.getEndTime();
            long endMinutes = TimeUnit.MILLISECONDS
                .toMinutes(endTime.getTime() - now.getTime()) + 3;
            // 删除之前的数据
            deleteCache(roundId);
            
            // 缓存轮次规则数据
            cacheRoundRule(ops, roundId, endMinutes);
            // 缓存轮次信息
            ops.set(key,
                JSON.toJSONString(round),
                endMinutes,
                TimeUnit.MINUTES);
            
            // 加载所有教学班与课程数据到缓存中
            List<CourseOpenDto> lessons =
                roundCourseDao.selectTeachingClassByRoundId(roundId);
            
            Map<String, List<CourseOpenDto>> collect = lessons.stream()
                .collect(Collectors.groupingBy(CourseOpenDto::getCourseCode));
            Set<String> keySet = collect.keySet();
            for (String courseCode : keySet)
            {
                List<CourseOpenDto> teachClasss = collect.get(courseCode);
                Set<Long> teachClassIds =
                    cacheTeachClass(ops, endMinutes, roundId, teachClasss);
                
                CourseOpenDto cour = teachClasss.get(0);
                cacheCourse(ops, endMinutes, roundId, teachClassIds, cour);
            }
        }
        
        if (CollectionUtil.isNotEmpty(keys))
        {
            for (String key : keys)
            {
                String roundId =
                    key.replace(String.format(Keys.ROUND_KEY, ""), "");
                if (StringUtils.isNumeric(roundId))
                {
                    deleteCache(Long.valueOf(roundId));
                }
            }
        }
    }
    
    private void deleteCache(Long roundId)
    {
        // 删除旧数据
        redisTemplate.delete(String.format(Keys.ROUND_KEY, roundId));
        
        Set<String> keys1 =
            redisTemplate.keys(String.format(Keys.ROUND_RULE, roundId, "*"));
        redisTemplate.delete(keys1);
        
        keys1 =
            redisTemplate.keys(String.format(Keys.ROUND_CLASS, roundId, "*"));
        redisTemplate.delete(keys1);
        
        keys1 =
            redisTemplate.keys(String.format(Keys.ROUND_COURSE, roundId, "*"));
        redisTemplate.delete(keys1);
    }
    
    /**缓存课程*/
    private void cacheCourse(ValueOperations<String, String> ops,
        long endMinutes, Long roundId, Set<Long> teachClassIds,
        CourseOpenDto cour)
    {
        CourseCache course = new CourseCache();
        course.setCourseCode(cour.getCourseCode());
        course.setCourseName(cour.getCourseName());
        course.setCredits(cour.getCredits());
        course.setNameEn(cour.getCourseNameEn());
        course.setTeachClassIds(teachClassIds);
        
        String courseKey =
            String.format(Keys.ROUND_COURSE, roundId, cour.getCourseCode());
        String text = JSON.toJSONString(course);
        if (!redisTemplate.hasKey(courseKey))
        {
            ops.set(courseKey, text, endMinutes, TimeUnit.MINUTES);
        }
    }
    
    /**缓存教学班*/
    private Set<Long> cacheTeachClass(ValueOperations<String, String> ops,
        long endMinutes, Long roundId, List<CourseOpenDto> teachClasss)
    {
        Set<Long> teachClassIds = new HashSet<>();
        for (CourseOpenDto lesson : teachClasss)
        {
            teachClassIds.add(lesson.getTeachingClassId());
            TeachingClassCache courseClass = new TeachingClassCache();
            
            courseClass.setCourseCode(lesson.getCourseCode());
            courseClass.setCourseName(lesson.getCourseName());
            courseClass.setCredits(lesson.getCredits());
            courseClass.setNameEn(lesson.getCourseNameEn());
            courseClass.setTeachClassId(lesson.getTeachingClassId());
            courseClass.setTeachClassCode(lesson.getTeachingClassCode());
            
            String classText = JSON.toJSONString(courseClass);
            String classKey = String
                .format(Keys.ROUND_CLASS, roundId, lesson.getTeachingClassId());
            ops.set(classKey, classText, endMinutes, TimeUnit.MINUTES);
        }
        return teachClassIds;
    }
    
    /**缓存轮次选课规则*/
    private void cacheRoundRule(ValueOperations<String, String> ops,
        Long roundId, long timeout)
    {
        List<ElectionRuleVo> rules = ruleDao.selectByRoundId(roundId);
        List<ElectionParameter> params = parameterDao.selectAll();
        for (ElectionRuleVo rule : rules)
        {
            rule.setList(new ArrayList<>());
            for (ElectionParameter param : params)
            {
                if (param.getRuleId().equals(rule.getId()))
                {
                    rule.getList().add(param);
                }
            }
            ops.set(
                String.format(Keys.ROUND_RULE, roundId, rule.getServiceName()),
                JSON.toJSONString(rule),
                timeout,
                TimeUnit.MINUTES);
        }
    }
    
    /**
     * 获取所有将要开始的轮次
     * 
     * @return
     */
    public List<ElectionRounds> getAllRound()
    {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String redisKey = String.format(Keys.ROUND_KEY, "*");
        Set<String> keys = redisTemplate.keys(redisKey);
        List<String> texts = ops.multiGet(keys);
        
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
        String redisKey = String.format(Keys.ROUND_KEY, roundId);
        String string = ops.get(redisKey);
        
        ElectionRounds round = JSON.parseObject(string, ElectionRounds.class);
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
        
        Set<String> keys =
            redisTemplate.keys(String.format(Keys.ROUND_RULE, roundId, "*"));
        
        List<String> list = ops.multiGet(keys);
        
        List<ElectionRuleVo> lessons = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(list))
        {
            for (String json : list)
            {
                ElectionRuleVo lesson =
                    JSON.parseObject(json, ElectionRuleVo.class);
                lessons.add(lesson);
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
        
        String text =
            ops.get(String.format(Keys.ROUND_RULE, roundId, serviceName));
        
        ElectionRuleVo vo = JSON.parseObject(text, ElectionRuleVo.class);
        return vo;
    }
    
    /**
     * 获取课程信息
     * 
     * @param roundId 轮次
     * @param courseCode 课程代码
     * @return
     */
    public CourseCache getCourse(Long roundId, String courseCode)
    {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String courseKey =
            String.format(Keys.ROUND_COURSE, roundId, courseCode);
        String string = ops.get(courseKey);
        
        CourseCache lesson = JSON.parseObject(string, CourseCache.class);
        return lesson;
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
        CourseCache course = this.getCourse(roundId, courseCode);
        if (course == null)
        {
            return lessons;
        }
        Set<Long> teachClassIds = course.getTeachClassIds();
        if (CollectionUtil.isEmpty(teachClassIds))
        {
            return lessons;
        }
        List<String> keys = new ArrayList<>();
        for (Long teachClassId : teachClassIds)
        {
            String classKey =
                String.format(Keys.ROUND_CLASS, roundId, teachClassId);
            keys.add(classKey);
        }
        
        if (CollectionUtil.isNotEmpty(keys))
        {
            ValueOperations<String, String> ops = redisTemplate.opsForValue();
            List<String> list = ops.multiGet(keys);
            if (CollectionUtil.isNotEmpty(list))
            {
                for (String json : list)
                {
                    TeachingClassCache lesson =
                        JSON.parseObject(json, TeachingClassCache.class);
                    lessons.add(lesson);
                }
            }
        }
        return lessons;
    }
    
    /**
     * 获取指定教学班信息
     * 
     * @param roundId 轮次
     * @param teachClassId 教学班ID
     * @return
     */
    public TeachingClassCache getTeachClass(Long roundId, Long teachClassId)
    {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String classKey =
            String.format(Keys.ROUND_CLASS, roundId, teachClassId);
        String string = ops.get(classKey);
        
        TeachingClassCache lesson =
            JSON.parseObject(string, TeachingClassCache.class);
        return lesson;
    }
    
    /**
     * 得到轮次设置的所有教学班
     * 
     * @param roundId
     * @return
     * @see [类、类#方法、类#成员]
     */
    public List<TeachingClassCache> getAllClass(Long roundId)
    {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        
        Set<String> keys =
            redisTemplate.keys(String.format(Keys.ROUND_COURSE, roundId, "*"));
        
        List<String> list = ops.multiGet(keys);
        
        List<TeachingClassCache> lessons = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(list))
        {
            for (String json : list)
            {
                TeachingClassCache lesson =
                    JSON.parseObject(json, TeachingClassCache.class);
                lessons.add(lesson);
            }
        }
        return lessons;
    }
    
}
