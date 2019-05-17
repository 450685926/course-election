package com.server.edu.election.studentelec.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.server.edu.common.entity.Teacher;
import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElcRoundConditionDao;
import com.server.edu.election.dao.ElecRoundStuDao;
import com.server.edu.election.dao.ElectionParameterDao;
import com.server.edu.election.dao.ElectionRuleDao;
import com.server.edu.election.dto.CourseOpenDto;
import com.server.edu.election.entity.ElcRoundCondition;
import com.server.edu.election.entity.ElectionParameter;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.rpc.BaseresServiceInvoker;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ClassTimeUnit;
import com.server.edu.election.studentelec.preload.CourseGradeLoad;
import com.server.edu.election.vo.ElectionRuleVo;
import com.server.edu.util.CollectionUtil;

/**
 * 轮次数据缓存类
 * 
 * 
 * @author  OuYangGuoDong
 * @version  [版本号, 2019年4月26日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Component
@Lazy(false)
public class RoundDataCacheUtil
{
    @Autowired
    private RedisTemplate<String, Integer> elecNumRedis;
    
    @Autowired
    private ElectionRuleDao ruleDao;
    
    @Autowired
    private ElectionParameterDao parameterDao;
    
    @Autowired
    private ElcRoundConditionDao elcRoundConditionDao;
    
    public void cacheRound(ValueOperations<String, String> ops,
        ElectionRounds round, long timeout)
    {
        String key = Keys.getRoundKey(round.getId());
        ops.set(key, JSON.toJSONString(round));
    }
    
    /**
     * 缓存轮次选课规则
     * 
     * @param ops
     * @param roundId 轮次ID
     * @param timeout 缓存失效时间分钟
     * @see [类、类#方法、类#成员]
     */
    public void cacheRoundRule(ValueOperations<String, String> ops,
        Long roundId, long timeout)
    {
        List<ElectionRuleVo> rules = ruleDao.selectByRoundId(roundId);
        List<String> serviceNames = null;
        String key = Keys.getRoundRuleKey(roundId);
        if (null != rules)
        {
            serviceNames = rules.stream()
                .map(ElectionRuleVo::getServiceName)
                .collect(Collectors.toList());
        }
        else
        {
            serviceNames = new ArrayList<>();
            
        }
        ops.set(key,
            JSON.toJSONString(serviceNames),
            timeout,
            TimeUnit.MINUTES);
    }
    
    @Autowired
    private CourseGradeLoad gradeLoad;
    
    /**
     * 缓存教学班
     * 
     * @param ops
     * @param timeout 缓存过期时间分钟
     * @param teachClasss 教学班
     * @param classKeys redis已经存在的教学班KEY
     * @return
     * @see [类、类#方法、类#成员]
     */
    public void cacheTeachClass(ValueOperations<String, String> ops,
        long timeout, List<CourseOpenDto> teachClasss)
    {
        List<Long> classIds = teachClasss.stream()
            .map(temp -> temp.getTeachingClassId())
            .collect(Collectors.toList());
        //按周数拆分的选课数据集合
        Map<Long, List<ClassTimeUnit>> collect =
            gradeLoad.groupByTime(classIds);
        Map<String, Teacher> teacherMap = new HashMap<>();
        
        for (CourseOpenDto lesson : teachClasss)
        {
            Long teachingClassId = lesson.getTeachingClassId();
            TeachingClassCache courseClass = new TeachingClassCache();
            
            courseClass.setCourseCode(lesson.getCourseCode());
            courseClass.setCourseName(lesson.getCourseName());
            courseClass.setCredits(lesson.getCredits());
            courseClass.setNameEn(lesson.getCourseNameEn());
            courseClass.setTeachClassId(teachingClassId);
            courseClass.setTeachClassCode(lesson.getTeachingClassCode());
            courseClass.setCampus(lesson.getCampus());
            courseClass.setTeachClassType(lesson.getTeachClassType());
            courseClass.setMaxNumber(lesson.getMaxNumber());
            courseClass.setCurrentNumber(lesson.getCurrentNumber());
            courseClass.setPublicElec(
                lesson.getIsElective() == Constants.ONE ? true : false);
            
            List<ClassTimeUnit> times =
                gradeLoad.concatTime(collect, teacherMap, courseClass);
            courseClass.setTimes(times);
            
            String classText = JSON.toJSONString(courseClass);
            String classKey = Keys.getClassKey(teachingClassId);
            setElecNumberToRedis(timeout,
                teachingClassId,
                courseClass.getCurrentNumber());
            // 保存教学班信息
            ops.set(classKey, classText, timeout, TimeUnit.MINUTES);
        }
    }
    
    /**
     * 缓存课程与教学班的关系
     * @param redisTemplate
     * @param timeout 缓存结束时间分钟
     * @param roundId 轮次ID
     * @param courseClassMap 课程代码-课程对应的教学班ID
     */
    public void cacheCourse(RedisTemplate<String, String> redisTemplate,
        long timeout, Long roundId, Map<String, Set<Long>> courseClassMap)
    {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        Set<String> existCourseCodes =
            redisTemplate.keys(Keys.getRoundCoursePattern(roundId));
        
        for (Entry<String, Set<Long>> entry : courseClassMap.entrySet())
        {
            String courseCode = entry.getKey();
            Set<Long> teachClassIds = entry.getValue();
            
            String courseKey = Keys.getRoundCourseKey(roundId, courseCode);
            String text = JSON.toJSONString(teachClassIds);
            ops.set(courseKey, text, timeout, TimeUnit.MINUTES);
            
            // 移除存在的
            existCourseCodes.remove(courseCode);
        }
        
        if (null != existCourseCodes && !existCourseCodes.isEmpty())
        {
            // 删除掉没有关联的课程
            redisTemplate.delete(existCourseCodes);
        }
    }
    
    /**
     * 得到轮次课程所对应的教学班ID
     * 
     * @param roundId
     * @param courseCode
     * @param ops
     * @return
     * @see [类、类#方法、类#成员]
     */
    public List<Long> getTeachClassIds(Long roundId, String courseCode,
        ValueOperations<String, String> ops)
    {
        String roundCourseKey = Keys.getRoundCourseKey(roundId, courseCode);
        
        String text = ops.get(roundCourseKey);
        List<Long> teachClassIds = null;
        if (StringUtils.isEmpty(text))
        {
            return teachClassIds;
        }
        teachClassIds = JSON.parseArray(text, Long.class);
        return teachClassIds;
    }
    
    private void setElecNumberToRedis(long timeout, Long teachingClassId,
        Integer currentNumber)
    {
        // 保存教学班已选课人数
        currentNumber = currentNumber == null ? 0 : currentNumber;
        elecNumRedis.opsForValue()
            .set(Keys.getClassElecNumberKey(teachingClassId),
                currentNumber,
                timeout,
                TimeUnit.MINUTES);
    }
    
    @Autowired
    private ElecRoundStuDao roundStuDao;
    
    /**
          *  缓存轮次学生
     * @param ops
     * @param roundId
     * @param timeout缓存的保持时间分钟
     * @param roundStuKeys redis已存在的Key
     * 
     * */
    public void cacheRoundStu(RedisTemplate<String, String> redisTemplate,
        Long roundId, long timeout)
    {
        List<String> stuIds = roundStuDao.findStuByRoundId(roundId);
        
        String key = Keys.getRoundStuKey(roundId);
        
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        if (CollectionUtil.isNotEmpty(stuIds))
        {
            Map<String, String> collect = stuIds.stream()
                .collect(Collectors.toMap(String::toString, String::toString));
            Set<String> allFields = ops.keys(key);
            if (null != allFields)
            {
                List<String> invalidStuIds = allFields.stream()
                    .filter(stdId -> !stuIds.contains(stdId))
                    .collect(Collectors.toList());
                // 删除已经去掉的缓存
                if (CollectionUtil.isNotEmpty(invalidStuIds))
                {
                    ops.delete(key, invalidStuIds.toArray());
                }
            }
            
            ops.putAll(key, collect);
            redisTemplate.expire(key, timeout, TimeUnit.MINUTES);
        }
        else
        {
            redisTemplate.delete(key);
        }
    }
    
    /**
     *  轮次是否包含学生
     * 
     * @param redisTemplate
     * @param roundId
     * @param studentId
     * @return
     * @see [类、类#方法、类#成员]
     */
    public boolean containsStu(RedisTemplate<String, String> redisTemplate,
        Long roundId, String studentId)
    {
        if (roundId == null || StringUtils.isBlank(studentId))
        {
            return false;
        }
        String roundStuKey = Keys.getRoundStuKey(roundId);
        HashOperations<String, Object, Object> ops = redisTemplate.opsForHash();
        return ops.hasKey(roundStuKey, studentId);
    }
    
    /**
         *  缓存轮次条件
    * @param ops
    * @param roundId
    * @param timeout缓存的保持时间分钟
    * @param roundConKeys redis已存在的Key
    * 
    * */
    public void cacheRoundCondition(ValueOperations<String, String> ops,
        Long roundId, long timeout)
    {
        String roundConKey = Keys.getRoundConditionOne(roundId);
        ElcRoundCondition elcRoundCondition =
            elcRoundConditionDao.selectByPrimaryKey(roundId);
        ops.set(roundConKey,
            JSONArray.toJSONString(elcRoundCondition),
            timeout,
            TimeUnit.MINUTES);
    }
    
    /**
     * 缓存上一学期
     * 
     * @param ops
     * @param round 轮次
     * @param timeout
     * @see [类、类#方法、类#成员]
     */
    public void cachePreSemester(ValueOperations<String, String> ops,
        ElectionRounds round, long timeout)
    {
        Long calendarId = round.getCalendarId();//当前学期
        SchoolCalendarVo preSemester =
            BaseresServiceInvoker.getPreSemester(calendarId);
        Long id = preSemester.getId();
        String roundPreSemester =
            String.format(Keys.ROUND_PRESEMESTER, round.getId());
        ops.set(roundPreSemester, Long.toString(id), timeout, TimeUnit.MINUTES);
    }
    
    /**
     * 设置选课規則，把所有规则都放到缓存中本科生26个，研究生七八个
     */
    public void cacheAllRule(RedisTemplate<String, String> redisTemplate)
    {
        HashOperations<String, Object, Object> ops = redisTemplate.opsForHash();
        String key = Keys.getRuleKey();
        
        List<ElectionRuleVo> selectAll = ruleDao.listAll();
        List<ElectionParameter> parameters = parameterDao.selectAll();
        for (ElectionRuleVo vo : selectAll)
        {
            List<ElectionParameter> list = parameters.stream()
                .filter(c -> vo.getId().equals(c.getRuleId()))
                .collect(Collectors.toList());
            vo.setList(list);
            ops.put(key, vo.getServiceName(), JSON.toJSONString(vo));
        }
    }
    
    /**
     * 获取选课规则
     * 
     * @param roundId 轮次
     * @param serviceName 服务名
     * @return
     */
    public ElectionRuleVo getRule(String serviceName,
        RedisTemplate<String, String> redisTemplate)
    {
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        
        String text = ops.get(Keys.getRuleKey(), serviceName);
        
        ElectionRuleVo vo = JSON.parseObject(text, ElectionRuleVo.class);
        return vo;
    }
    
    /**
     * 获取选择规则列表
     * 
     * @param roundId 轮次
     * @return
     */
    public List<ElectionRuleVo> getRules(Long roundId,
        RedisTemplate<String, String> redisTemplate)
    {
        ValueOperations<String, String> opsForValue =
            redisTemplate.opsForValue();
        
        String key = Keys.getRoundRuleKey(roundId);
        List<String> list = JSON.parseArray(opsForValue.get(key), String.class);
        
        List<ElectionRuleVo> rules = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(list))
        {
            HashOperations<String, String, String> ops =
                redisTemplate.opsForHash();
            List<String> multiGet = ops.multiGet(Keys.getRuleKey(), list);
            for (String text : multiGet)
            {
                rules.add(JSON.parseObject(text, ElectionRuleVo.class));
            }
        }
        return rules;
    }
    
    /**
     * 轮次是否包含指定规则
     * 
     * @param roundId
     * @param serviceName
     * @param redisTemplate
     * @return
     * @see [类、类#方法、类#成员]
     */
    public boolean containsRule(Long roundId, String serviceName,
        RedisTemplate<String, String> redisTemplate)
    {
        ValueOperations<String, String> opsForValue =
            redisTemplate.opsForValue();
        
        String key = Keys.getRoundRuleKey(roundId);
        List<String> list = JSON.parseArray(opsForValue.get(key), String.class);
        
        return CollectionUtil.isNotEmpty(list) && list.contains(serviceName);
    }
    
}
