package com.server.edu.election.studentelec.service.cache;

import java.util.ArrayList;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.controller.ElecAgentController;
import com.server.edu.election.dao.ElcRoundConditionDao;
import com.server.edu.election.dao.ElecRoundCourseDao;
import com.server.edu.election.dao.ElecRoundStuDao;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.dto.CourseOpenDto;
import com.server.edu.election.entity.ElcRoundCondition;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.entity.Student;
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
	Logger log = LoggerFactory.getLogger(RoundCacheService.class);
	
    @Autowired
    private ElcRoundConditionDao elcRoundConditionDao;
    
    @Autowired
    private ElecRoundCourseDao roundCourseDao;
    
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
    
    /**<ul>
     * 	 <li>判断学生的校区、学院、年级、专业、培养层次是否匹配轮次条件(本科生)
     * 	 <li>判断学生的校区、学院、年级、专业、培养层次、培养类别、学位类型、学习形式是否匹配轮次条件（研究生）
     * <ul>
     * @param roundId
     * @param studentId
     * @return
     * @see [类、类#方法、类#成员]
     */
    public boolean containsStuCondition(Long roundId, String studentId,String projectId)
    {
    	log.info("============================containsStuCondition=====匹配轮次信息开始=======================:");
    	
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
        	log.info("========================containsStuCondition=====数据库查询student信息=======================:");
        	
            student = studentDao.selectByPrimaryKey(studentId);
            
            log.info("========================student=====AAAAAAAAAAAAAAstudentAAAAAAAAAAAAAAA=======================:"+student);
            
            if (null == student)
            {
            	log.info("==========student is null==============student is null=====student is null==========student is null=========student is null====:");
                return false;
            }
            ops.put(key, studentId, JSON.toJSONString(student));
            strTemplate.expire(key, 1, TimeUnit.HOURS);
            
            log.info("========================containsStuCondition=====数据库查询student信息放入缓存中=======================:");
            log.info("========================containsStuCondition============================:"+ student);
        }
        else
        {
        	log.info("========================containsStuCondition=====直接从缓存中取student信息=======================:");
        	
            student = JSON.parseObject(text, Student.class);
        }
        ElcRoundCondition con = getRoundCondition(roundId);
        if (con != null)
        {
        	boolean matchConditionFlag = contains(con.getCampus(), student.getCampus())
        			&& contains(con.getFacultys(), student.getFaculty())
        			&& contains(con.getGrades(), student.getGrade().toString())
        			&& contains(con.getMajors(), student.getProfession())
        			&& contains(con.getTrainingLevels(),student.getTrainingLevel());
            
        	log.info("========================containsStuCondition=====matchConditionFlag=======================:"+matchConditionFlag);
        	
        	if (StringUtils.equals(projectId, Constants.PROJ_UNGRADUATE)) {
        		if (!matchConditionFlag) {
					return false;
				}
        	}else {
        		boolean matchConditionGraduteFlag = contains(con.getTrainingCategorys(), student.getTrainingCategory())
        				&& contains(con.getDegreeTypes(), student.getDegreeType())
        				&& contains(con.getFormLearnings(), student.getFormLearning());
        		
        		log.info("========================containsStuCondition=====matchConditionGraduteFlag=======================:"+matchConditionGraduteFlag);
        		
        		if (!matchConditionFlag || !matchConditionGraduteFlag) {
					return false;
				}
			}
        	return true;
        }
        return true;
    }
    
    boolean contains(String source, String taget)
    {
        if (StringUtils.isBlank(source))
        {
            return true;
        }
        if (StringUtils.isBlank(taget)) {
			return false;
		}
        return source.contains(taget);
    }
    
    /**
     * 缓存课程与教学班的关系
     * @param redisTemplate
     * @param timeout 缓存结束时间分钟
     * @param roundId 轮次ID
     */
    public void cacheCourse(long timeout, Long roundId, Long calendarId,String manageDptId)
    {
        // 加载所有教学班与课程数据到缓存中
    	List<CourseOpenDto> lessons = new ArrayList<CourseOpenDto>();
    	if (StringUtils.equals(manageDptId, Constants.PROJ_UNGRADUATE)) {
    		lessons = roundCourseDao.selectCorseRefTeachClassByRoundId(roundId, calendarId);
		}else {
			lessons = roundCourseDao.selectCorseRefTeachClassGraduteByRoundId(roundId, calendarId);
		}
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
        HashOperations<String, String, String> ops = strTemplate.opsForHash();
        String key = Keys.getRoundCourseKey(roundId);
        Set<String> existKeys = ops.keys(key);
        for (Entry<String, Set<Long>> entry : courseClassMap.entrySet())
        {
            String courseCode = entry.getKey();
            Set<Long> teachClassIds = entry.getValue();
            
            ops.put(key, courseCode, JSON.toJSONString(teachClassIds));
            
            // 移除存在的
            existKeys.remove(courseCode);
        }
        if (null != existKeys && !existKeys.isEmpty())
        {
            // 删除掉没有关联的课程
            ops.delete(key, existKeys.toArray());
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
    public List<Long> getTeachClassIds(Long roundId, String courseCode)
    {
        HashOperations<String, String, String> ops = strTemplate.opsForHash();
        
        String key = Keys.getRoundCourseKey(roundId);
        
        String text = ops.get(key, courseCode);
        List<Long> teachClassIds = null;
        if (StringUtils.isEmpty(text))
        {
            return teachClassIds;
        }
        teachClassIds = JSON.parseArray(text, Long.class);
        
        Collections.sort(teachClassIds);
        
        return teachClassIds;
    }
    
    /**
     * 得到学年学期课程所对应的教学班ID
     * 
     * @param calendarId
     * @param courseCode
     * @param ops
     * @return
     * @see [类、类#方法、类#成员]
     */
    public List<Long> getTeachClassIdsByCalendarId(Long calendarId, String courseCode)
    {
    	HashOperations<String, String, String> ops = strTemplate.opsForHash();
    	
    	String key = Keys.getCalendarCourseKey(calendarId);
    	
    	String text = ops.get(key, courseCode);
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
