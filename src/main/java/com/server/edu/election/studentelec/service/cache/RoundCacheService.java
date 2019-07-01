package com.server.edu.election.studentelec.service.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.server.edu.election.dao.ElcRoundConditionDao;
import com.server.edu.election.dao.ElecRoundStuDao;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.entity.ElcRoundCondition;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.entity.ElectionRoundsCour;
import com.server.edu.election.entity.Student;
import com.server.edu.election.service.ElecRoundCourseService;
import com.server.edu.election.studentelec.utils.Keys;
import com.server.edu.util.CollectionUtil;

/**
 * 轮次缓存服务类，缓存轮次、轮次规则、轮次条件、可选学生、可选教学任务
 * 
 * 
 * @author  OuYangGuoDong
 * @version  [版本号, 2019年5月21日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Service
public class RoundCacheService extends AbstractCacheService
{
    @Autowired
    private ElcRoundConditionDao elcRoundConditionDao;
    
    @Autowired
    private ElecRoundCourseService elecRoundCourseService;
    
    @Autowired
    private StringRedisTemplate strTemplate;
    
    public HashOperations<String, String, ElectionRounds> opsRound()
    {
        RedisTemplate<String, ElectionRounds> redisTemplate =
            redisTemplate(ElectionRounds.class);
        HashOperations<String, String, ElectionRounds> hash =
            redisTemplate.opsForHash();
        return hash;
    }
    
    /**
     * 缓存轮次数据
     * 
     * @param round
     * @param timeout
     * @see [类、类#方法、类#成员]
     */
    public void cacheRound(ElectionRounds round, long timeout)
    {
        HashOperations<String, String, ElectionRounds> hash = opsRound();
        
        String key = Keys.getRoundKey();
        hash.put(key, round.getId().toString(), round);
        
        strTemplate.expire(key, 1, TimeUnit.DAYS);
    }
    
    public Set<String> getRoundKeys()
    {
        HashOperations<String, String, ElectionRounds> hash = opsRound();
        Set<String> keys = hash.keys(Keys.getRoundKey());
        return keys;
    }
    
    public void deleteRound(Long roundId)
    {
        if (null != roundId)
        {
            HashOperations<String, String, ElectionRounds> hash = opsRound();
            hash.delete(Keys.getRoundKey(), roundId.toString());
        }
    }
    
    public void deleteRounds(Object[] roundIds)
    {
        HashOperations<String, String, ElectionRounds> hash = opsRound();
        hash.delete(Keys.getRoundKey(), roundIds);
    }
    
    /**
     * 获取所有将要开始的轮次
     * 
     * @return
     */
    public List<ElectionRounds> getAllRound()
    {
        HashOperations<String, String, ElectionRounds> hash = opsRound();
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
        HashOperations<String, String, ElectionRounds> hash = opsRound();
        ElectionRounds round = hash.get(Keys.getRoundKey(), roundId.toString());
        return round;
    }
    
    /**
     *  缓存轮次条件
    * @param roundId
    * @param timeout缓存的保持时间分钟
    * @param roundConKeys redis已存在的Key
    * 
    * */
    public void cacheRoundCondition(Long roundId, long timeout)
    {
        ValueOperations<String, String> ops = strTemplate.opsForValue();
        String roundConKey = Keys.getRoundConditionOne(roundId);
        ElcRoundCondition elcRoundCondition =
            elcRoundConditionDao.selectByPrimaryKey(roundId);
        ops.set(roundConKey,
            JSONArray.toJSONString(elcRoundCondition),
            timeout,
            TimeUnit.MINUTES);
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
    
    @Autowired
    private StudentDao studentDao;
    
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
        
        ElcRoundCondition con = getRoundCondition(roundId);
        if (con != null)
        {
            if (contains(con.getCampus(), student.getCampus())
                && contains(con.getFacultys(), student.getFaculty())
                && contains(con.getGrades(), student.getGrade().toString())
                && contains(con.getMajors(), student.getProfession())
                && contains(con.getTrainingLevels(),
                    student.getTrainingLevel()))
            {
                return true;
            }
            return false;
        }
        return true;
    }
    
    boolean contains(String source, String taget)
    {
        if (StringUtils.isBlank(source))
        {
            return true;
        }
        return source.contains(taget);
    }
    
    /**
     * 缓存轮次教学班的关系
     * @param redisTemplate
     * @param timeout 缓存结束时间分钟
     * @param roundId 轮次ID
     */
    public void cacheCourse(long timeout, Long roundId)
    {
        // 加载所有教学班与课程数据到缓存中
    	List<ElectionRoundsCour> lessons = elecRoundCourseService.getTeachingClassIds(roundId);
        Map<Long, Set<Long>> courseClassMap = new HashMap<>();
        for (ElectionRoundsCour teachClasss : lessons)
        {
            Long teachingClassId = teachClasss.getTeachingClassId();
            if (courseClassMap.containsKey(roundId))
            {
                courseClassMap.get(roundId).add(teachingClassId);
            }
            else
            {
                Set<Long> ids = new HashSet<>();
                ids.add(teachingClassId);
                courseClassMap.put(roundId, ids);
            }
        }
        
//        HashOperations<String, String, String> ops = strTemplate.opsForHash();
        ValueOperations<String, String> opsForValue =
        		strTemplate.opsForValue();
        String key = Keys.getRoundCourseKey(roundId);
        
//        Set<String> existKeys = ops.keys(key);
        
        String value= opsForValue.get(key);
        for (Entry<Long, Set<Long>> entry : courseClassMap.entrySet())
        {
            Long roundsId = entry.getKey();
            Set<Long> teachClassIds = entry.getValue();
            opsForValue.set(key, JSON.toJSONString(teachClassIds));
//            // 移除存在的
//            existKeys.remove(courseCode);
        }
        
//        if (null != existKeys && !existKeys.isEmpty())
//        {
//            // 删除掉没有关联的课程
//            ops.delete(key, existKeys.toArray());
//        }
    }
    
    /**
     * 得到轮次所对应的教学班ID
     * 
     * @param roundId
     * @param ops
     * @return
     * @see [类、类#方法、类#成员]
     */
    public List<Long> getTeachClassIds(Long roundId)
    {
//        HashOperations<String, String, String> ops = strTemplate.opsForHash();
        ValueOperations<String, String> opsForValue =
        		strTemplate.opsForValue();
        String key = Keys.getRoundCourseKey(roundId);
        
//        String text = ops.get(key, courseCode);
        String text = opsForValue.get(key);
        List<Long> teachClassIds = null;
        if (StringUtils.isEmpty(text))
        {
            return teachClassIds;
        }
        teachClassIds = JSON.parseArray(text, Long.class);
        
        Collections.sort(teachClassIds);
        
        return teachClassIds;
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
    public void cacheRoundStu(Long roundId, long timeout)
    {
        List<String> stuIds = roundStuDao.findStuByRoundId(roundId);
        
        String key = Keys.getRoundStuKey(roundId);
        
        HashOperations<String, String, String> ops = strTemplate.opsForHash();
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
            strTemplate.expire(key, timeout, TimeUnit.MINUTES);
        }
        else
        {
            strTemplate.delete(key);
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
    public boolean containsStu(Long roundId, String studentId)
    {
        if (roundId == null || StringUtils.isBlank(studentId))
        {
            return false;
        }
        String roundStuKey = Keys.getRoundStuKey(roundId);
        HashOperations<String, Object, Object> ops = strTemplate.opsForHash();
        return ops.hasKey(roundStuKey, studentId);
    }
}