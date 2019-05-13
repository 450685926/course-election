package com.server.edu.election.studentelec.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import com.server.edu.common.entity.TeacherInfo;
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
    
    /**
     * 缓存轮次选课规则
     * 
     * @param ops
     * @param roundId 轮次ID
     * @param timeout 缓存失效时间分钟
     * @param ruleKeys 已存在的选课规则
     * @see [类、类#方法、类#成员]
     */
    public void cacheRoundRule(ValueOperations<String, String> ops,
        Long roundId, long timeout)
    {
        List<ElectionRuleVo> rules = ruleDao.selectByRoundId(roundId);
        List<String> serviceNames = rules.stream().map(ElectionRuleVo::getServiceName).collect(Collectors.toList());
        String key = Keys.getRoundRuleKey(roundId);
        ops.set(key, JSON.toJSONString(serviceNames), timeout, TimeUnit.MINUTES);
    }
    
    @Autowired
    private CourseGradeLoad gradeLoad;
    
    /**
     * 缓存教学班
     * 
     * @param ops
     * @param timeout 缓存过期时间分钟
     * @param calendarId 学期ID
     * @param teachClasss 教学班
     * @param classKeys redis已经存在的教学班KEY
     * @return
     * @see [类、类#方法、类#成员]
     */
    public Set<Long> cacheTeachClass(ValueOperations<String, String> ops,
        long timeout, Long calendarId, List<CourseOpenDto> teachClasss)
    {
        Set<Long> teachClassIds = new HashSet<>();
        
        List<Long> classIds = teachClasss.stream()
            .map(temp -> temp.getTeachingClassId())
            .collect(Collectors.toList());
        //按周数拆分的选课数据集合
        Map<Long, List<ClassTimeUnit>> collect =
            gradeLoad.groupByTime(classIds);
        Map<String, TeacherInfo> teacherMap = new HashMap<>();
        
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
            String classKey = Keys.getClassKey(calendarId, teachingClassId);
            setElecNumberToRedis(timeout,
                teachingClassId,
                courseClass.getCurrentNumber());
            // 保存教学班信息
            ops.set(classKey, classText, timeout, TimeUnit.MINUTES);
        }
        teachClassIds.addAll(classIds);
        return teachClassIds;
    }
    
    /**
     * 缓存课程与教学班的关系
     * @param ops
     * @param timeout缓存结束时间分钟
     * @param roundId 轮次ID
     * @param courseCode课程代码
     * @param teachClassIds课程对应的教学班ID
     * @param courseKeys已存在于redis的key
     */
    public void cacheCourse(ValueOperations<String, String> ops, long timeout,
        Long roundId, String courseCode, Set<Long> teachClassIds,
        Set<String> courseKeys)
    {
        String courseKey = Keys.getRoundCourseKey(roundId, courseCode);
        if (courseKeys.contains(courseKey))
        {
            courseKeys.remove(courseKey);
        }
        String text = JSON.toJSONString(teachClassIds);
        ops.set(courseKey, text, timeout, TimeUnit.MINUTES);
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
        redisTemplate.delete(key);
        
        HashOperations<String, Object, Object> ops = redisTemplate.opsForHash();
        if (CollectionUtil.isNotEmpty(stuIds))
        {
            Map<String, String> collect = stuIds.stream()
                .collect(Collectors.toMap(String::toString, String::toString));
            
            ops.putAll(key, collect);
            redisTemplate.expire(key, timeout, TimeUnit.MINUTES);
        }
    }
    
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
        Long roundId, long timeout, Set<String> roundConKeys)
    {
        String roundConKey = Keys.getRoundConditionOne(roundId);
        if (!roundConKey.contains(roundConKey))
        {
            ElcRoundCondition elcRoundCondition =
                elcRoundConditionDao.selectByPrimaryKey(roundId);
            ops.set(roundConKey,
                JSONArray.toJSONString(elcRoundCondition),
                timeout,
                TimeUnit.MINUTES);
        }
    }
    
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
     * 设置选课規則
     */
    public void setRule(RedisTemplate<String, String> redisTemplate) {
        HashOperations<String, Object, Object> ops = redisTemplate.opsForHash();
		 String redisKey = Keys.getRuleKey();
		 List<ElectionRuleVo> selectAll = ruleDao.listAll();
		 List<ElectionParameter> parameters = parameterDao.selectAll();
		 for (ElectionRuleVo vo : selectAll) {
			 List<ElectionParameter> list = parameters.stream().filter(c->vo.getId().equals(c.getRuleId())).collect(Collectors.toList());
			 vo.setList(list);
			 ops.put(redisKey, vo.getServiceName(), JSON.toJSONString(vo));
		}
    }
}
