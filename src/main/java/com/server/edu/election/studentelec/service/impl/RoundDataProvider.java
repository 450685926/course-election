package com.server.edu.election.studentelec.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
import com.server.edu.election.studentelec.context.ElecCourse;
import com.server.edu.election.studentelec.context.ElecCourseClass;
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
        for (ElectionRounds round : selectBeStart)
        {
            Date endTime = round.getEndTime();
            long endMinutes = TimeUnit.MILLISECONDS
                .toMinutes(endTime.getTime() - now.getTime()) + 3;
            
            Long roundId = round.getId();
            cacheRoundRule(ops, roundId, endMinutes);
            // 缓存轮次信息
            ops.set(String.format(Keys.ROUND_KEY, roundId),
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
                List<Long> teachClassIds = new ArrayList<>();
                cacheCourse(ops,
                    endMinutes,
                    roundId,
                    teachClasss,
                    teachClassIds);
                
                CourseOpenDto cour = teachClasss.get(0);
                cacheTeachClass(ops, endMinutes, roundId, teachClassIds, cour);
            }
        }
    }
    
    /**缓存教学班*/
    private void cacheTeachClass(ValueOperations<String, String> ops,
        long endMinutes, Long roundId, List<Long> teachClassIds,
        CourseOpenDto cour)
    {
        ElecCourse course = new ElecCourse();
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
    
    /**缓存课程*/
    private void cacheCourse(ValueOperations<String, String> ops,
        long endMinutes, Long roundId, List<CourseOpenDto> teachClasss,
        List<Long> teachClassIds)
    {
        for (CourseOpenDto lesson : teachClasss)
        {
            teachClassIds.add(lesson.getTeachingClassId());
            ElecCourseClass courseClass = new ElecCourseClass();
            courseClass.setCourseCode(lesson.getCourseCode());
            courseClass.setCourseName(lesson.getCourseName());
            courseClass.setCredits(lesson.getCredits());
            courseClass.setNameEn(lesson.getCourseNameEn());
            courseClass.setTeacherClassId(lesson.getTeachingClassId());
            courseClass.setTeacherClassCode(lesson.getTeachingClassCode());
            String classText = JSON.toJSONString(courseClass);
            String classKey = String
                .format(Keys.ROUND_CLASS, roundId, lesson.getTeachingClassId());
            ops.set(classKey, classText, endMinutes, TimeUnit.MINUTES);
        }
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
    
    public ElectionRounds getRound(Long roundId)
    {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String redisKey = String.format(Keys.ROUND_KEY, roundId);
        String string = ops.get(redisKey);
        
        ElectionRounds round = JSON.parseObject(string, ElectionRounds.class);
        return round;
    }
    
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
    
    public ElectionRuleVo getRule(Long roundId, String serviceName)
    {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        
        String text =
            ops.get(String.format(Keys.ROUND_RULE, roundId, serviceName));
        
        ElectionRuleVo vo = JSON.parseObject(text, ElectionRuleVo.class);
        return vo;
    }
    
    public ElecCourse getCourse(Long roundId, String courseCode)
    {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String courseKey =
            String.format(Keys.ROUND_COURSE, roundId, courseCode);
        String string = ops.get(courseKey);
        
        ElecCourse lesson = JSON.parseObject(string, ElecCourse.class);
        return lesson;
    }
    
    public List<ElecCourseClass> getTeachClasss(Long roundId, String courseCode)
    {
        ElecCourse course = this.getCourse(roundId, courseCode);
        List<Long> teachClassIds = course.getTeachClassIds();
        List<String> keys = new ArrayList<>();
        for (Long teachClassId : teachClassIds)
        {
            String classKey =
                String.format(Keys.ROUND_CLASS, roundId, teachClassId);
            keys.add(classKey);
        }
        List<ElecCourseClass> lessons = new ArrayList<>();
        
        if (CollectionUtil.isNotEmpty(keys))
        {
            ValueOperations<String, String> ops = redisTemplate.opsForValue();
            List<String> list = ops.multiGet(keys);
            if (CollectionUtil.isNotEmpty(list))
            {
                for (String json : list)
                {
                    ElecCourseClass lesson =
                        JSON.parseObject(json, ElecCourseClass.class);
                    lessons.add(lesson);
                }
            }
        }
        return lessons;
    }
    
    public ElecCourseClass getTeachClass(Long roundId, Long teachClassId)
    {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String classKey =
            String.format(Keys.ROUND_CLASS, roundId, teachClassId);
        String string = ops.get(classKey);
        
        ElecCourseClass lesson =
            JSON.parseObject(string, ElecCourseClass.class);
        return lesson;
    }
    
    /**
     * 得到轮次设置的所有教学班
     * 
     * @param roundId
     * @return
     * @see [类、类#方法、类#成员]
     */
    public List<ElecCourseClass> getAllClass(Long roundId)
    {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        
        Set<String> keys =
            redisTemplate.keys(String.format(Keys.ROUND_COURSE, roundId, "*"));
        
        List<String> list = ops.multiGet(keys);
        
        List<ElecCourseClass> lessons = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(list))
        {
            for (String json : list)
            {
                ElecCourseClass lesson =
                    JSON.parseObject(json, ElecCourseClass.class);
                lessons.add(lesson);
            }
        }
        return lessons;
    }
    
}
