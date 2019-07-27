package com.server.edu.election.studentelec.utils;

import static com.server.edu.election.studentelec.utils.Keys.STD_STATUS;
import static com.server.edu.election.studentelec.utils.Keys.STD_STATUS_LOCK;
import static com.server.edu.election.studentelec.utils.Keys.STD_ADIMN_STATUS;
import static com.server.edu.election.studentelec.utils.Keys.STD_STATUS_ADMIN_LOCK;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.server.edu.dictionary.utils.SpringUtils;
import com.server.edu.election.entity.ElcNoGradCouSubs;
import com.server.edu.election.entity.ElectionApply;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.util.CollectionUtil;

/**
 * 选课上下文工具类
 * 
 * 
 * @author  OuYangGuoDong
 * @version  [版本号, 2019年2月28日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class ElecContextUtil
{
    private static Logger logger =
        LoggerFactory.getLogger(ElecContextUtil.class);
    
    private ElecContextUtil(Long calendarId, String studentId)
    {
        this.calendarId = calendarId;
        this.studentId = studentId;
    }
    
    private Long calendarId;
    
    private String studentId;
    // 由于使用redis的hash来保存数据，为了能快速得到所有的数据使用map先保存起来
    public Map<String, String> cacheData;
    
    /**
     * 创建，此方法比较重量级，如果只是操作某个属性先找一下有没有static方法能满足
     * 
     * @param studentId
     * @param calendarId
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static ElecContextUtil create(String studentId, Long calendarId)
    {
        ElecContextUtil u = new ElecContextUtil(calendarId, studentId);
        
        HashOperations<String, String, String> ops =
            getRedisTemplate().opsForHash();
        
        Map<String, String> entries = ops.entries(u.getRedisKey());
        u.cacheData = entries;
        if (u.cacheData == null)
        {
            u.cacheData = new HashMap<>();
        }
        return u;
    }
    
    String getRedisKey()
    {
        return ElecContextUtil.getKey(studentId, calendarId);
    }
    
    static String getKey(String studentId, Long calendarId)
    {
        return Keys.STD + calendarId + "-" + studentId;
    }
    
    static StringRedisTemplate getRedisTemplate()
    {
        return SpringUtils.getBean(StringRedisTemplate.class);
    }
    
    public <T> T getObject(String type, Class<T> clazz)
    {
        String value = getByKey(type);
        return JSON.parseObject(value, clazz);
    }
    
    public <T> Set<T> getSet(String type, Class<T> clazz)
    {
        List<T> list = getList(type, clazz);
        return new HashSet<>(list);
    }
    
    public <T> List<T> getList(String type, Class<T> clazz)
    {
        String value = getByKey(type);
        
        if (StringUtils.isEmpty(value))
        {
            return new ArrayList<>();
        }
        try
        {
            return JSON.parseArray(value, clazz);
        }
        catch (JSONException e)
        {
            logger.error(e.getMessage(), e);
        }
        
        return new ArrayList<>();
    }
    
    private String getByKey(String type)
    {
        String value = null;
        if (null != this.cacheData)
        {
            value = this.cacheData.get(type);
        }
        return value;
    }
    
    /**
     * 更新内存中的缓存
     * 
     * @param type
     * @param value
     * @see [类、类#方法、类#成员]
     */
    public void updateMem(String type, Object value)
    {
        String jsonString = JSON.toJSONString(value);
        this.cacheData.put(type, jsonString);
    }
    
    /**
     * 保存单个值到redis
     * 
     * @param type
     * @param value
     * @see [类、类#方法、类#成员]
     */
    public void saveOne(String type, Object value)
    {
        if (null != value)
        {
            HashOperations<String, String, String> ops =
                getRedisTemplate().opsForHash();
            
            String key = getRedisKey();
            String jsonString = JSON.toJSONString(value);
            this.cacheData.put(type, jsonString);
            ops.put(key, type, jsonString);
            this.updateMem(type, jsonString);
            getRedisTemplate().expire(key, 1, TimeUnit.DAYS);
        }
    }
    
    /**
     * 保存所有数据到redis
     * 
     * @see [类、类#方法、类#成员]
     */
    public void saveAll()
    {
        HashOperations<String, String, String> ops =
            getRedisTemplate().opsForHash();
        String key = getRedisKey();
        ops.putAll(key, this.cacheData);
        getRedisTemplate().expire(key, 1, TimeUnit.DAYS);
    }
    
    public StudentInfoCache getStudentInfo()
    {
        StudentInfoCache studentInfo =
            getObject(StudentInfoCache.class.getSimpleName(),
                StudentInfoCache.class);
        if (null == studentInfo)
        {
            studentInfo = new StudentInfoCache();
            studentInfo.setStudentId(studentId);
        }
        return studentInfo;
    }
    
    public ElecRespose getElecRespose()
    {
        ElecRespose respose =
            getObject(ElecRespose.class.getSimpleName(), ElecRespose.class);
        if (null == respose)
        {
            respose = new ElecRespose(ElecStatus.Init);
        }
        return respose;
    }
    
    /**
     * 获取选课申请课程
     */
    public Set<ElectionApply> getElecApplyCourse()
    {
        return getSet("elecApplyCourses", ElectionApply.class);
    }
    
    /**
     *保存学生选课申请课程
     */
    public static void setElecApplyCourse(String studentId, Long calendarId,
        List<ElectionApply> electionApplys)
    {
        String key = getKey(studentId, calendarId);
        if (getRedisTemplate().hasKey(key))
        {
            HashOperations<String, String, String> ops =
                getRedisTemplate().opsForHash();
            String jsonString = JSON.toJSONString(electionApplys);
            ops.put(key, "elecApplyCourses", jsonString);
        }
    }
    
    /**
     * 得到学生选课响应
     * 
     * @param studentId
     * @param calendarId
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static ElecRespose getElecRespose(String studentId, Long calendarId)
    {
        HashOperations<String, String, String> ops =
            getRedisTemplate().opsForHash();
        String key = getKey(studentId, calendarId);
        
        String value = ops.get(key, ElecRespose.class.getSimpleName());
        ElecRespose respose = JSON.parseObject(value, ElecRespose.class);
        if (null == respose)
        {
            respose = new ElecRespose(ElecStatus.Init);
        }
        return respose;
    }
    
    /**
     * 获取学生指定轮次选课状态<br>
     * ElecStatus 的四种状态的转换关系<br>
     * Init->Loading->Ready->Processing->Ready<br>
     * 修改状态应该加锁
     * 
     * @param roundId
     * @param studentId
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static ElecStatus getElecStatus(Long roundId, String studentId)
    {
        String value = getRedisTemplate().opsForValue()
            .get(String.format(STD_STATUS, roundId, studentId));
        if (StringUtils.isBlank(value))
        {
            logger.warn(
                "----- elecStatus not find use Init [roundId:{}, studentId:{}] -----",
                roundId,
                studentId);
            return ElecStatus.Init;
        }
        
        return ElecStatus.valueOf(value);
    }
    
    /**
     * 获取学生指定轮次选课状态<br>
     * ElecStatus 的四种状态的转换关系<br>
     * Init->Loading->Ready->Processing->Ready<br>
     * 修改状态应该加锁
     * 
     * @param roundId
     * @param studentId
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static ElecStatus getElecAdminStatus(Long calendarId, String studentId)
    {
    	String value = getRedisTemplate().opsForValue()
    			.get(String.format(STD_ADIMN_STATUS, calendarId, studentId));
    	if (StringUtils.isBlank(value))
    	{
    		logger.warn(
    				"----- elecAdminStatus not find use Init [calendarId:{}, studentId:{}] -----",
    				calendarId,
    				studentId);
    		return ElecStatus.Init;
    	}
    	
    	return ElecStatus.valueOf(value);
    }
    
    /**
     * 设置学生指定轮次选课状态<br>
     * ElecStatus 的四种状态的转换关系<br>
     * Init->Loading->Ready->Processing->Ready<br>
     * 修改状态应该加锁, 最大给出1个小时的有效期，如果1个小时选课还没处理完说明有问题
     * 
     * @param roundId
     * @param studentId
     * @param status
     * @see [类、类#方法、类#成员]
     */
    public static void setElecStatus(Long roundId, String studentId,
        ElecStatus status)
    {
        int timeout = 1;
        if (status == ElecStatus.Ready)
        {
            timeout = 10;
        }
        getRedisTemplate().opsForValue()
            .set(String.format(STD_STATUS, roundId, studentId),
                status.toString(),
                timeout,
                TimeUnit.HOURS);
    }
    
    /**
     * 设置学生指定轮次选课状态(管理员代选)<br>
     * ElecStatus 的四种状态的转换关系<br>
     * Init->Loading->Ready->Processing->Ready<br>
     * 修改状态应该加锁, 最大给出1个小时的有效期，如果1个小时选课还没处理完说明有问题
     * 
     * @param roundId
     * @param studentId
     * @param status
     * @see [类、类#方法、类#成员]
     */
    public static void setElecAdminStatus(Long calendarId, String studentId,
    		ElecStatus status)
    {
    	int timeout = 1;
    	if (status == ElecStatus.Ready)
    	{
    		timeout = 10;
    	}
    	logger.info("444444444444444444---calendarId:" + calendarId);
    	getRedisTemplate().opsForValue()
    	.set(String.format(STD_ADIMN_STATUS, calendarId, studentId),
    			status.toString(),
    			timeout,
    			TimeUnit.HOURS);
    }
    
    /**
     * 需要处理加锁成功后程序挂了，这个时候值没有设置超时将会一直无法选课
     * 
     */
    public static void delDeadLock()
    {
        Set<String> keys =
            getRedisTemplate().keys(Keys.STD_STATUS_LOCK_PATTERN);
        if (CollectionUtil.isNotEmpty(keys))
        {
            Long now = new Date().getTime();
            List<String> values =
                getRedisTemplate().opsForValue().multiGet(keys);
            
            int index = 0;
            List<String> delKeys = new ArrayList<>();
            for (String key : keys)
            {
                String timeStr = values.get(index);
                if (StringUtils.isNumeric(timeStr))
                {
                    Long time = Long.valueOf(timeStr);
                    long hours = TimeUnit.MILLISECONDS.toHours(now - time);
                    if (hours >= 1)// 超过一个小时的锁认为是死锁，删除掉
                    {
                        delKeys.add(key);
                    }
                }
                index++;
            }
            getRedisTemplate().delete(delKeys);
        }
    }
    
    /**
     * 给status 加锁，并返回key用于解锁
     * @return true 成功 false 失败
     */
    public static boolean tryLock(Long roundId, String studentId)
    {
        long value = System.currentTimeMillis();
        String redisKey = String.format(STD_STATUS_LOCK, roundId, studentId);
        if (getRedisTemplate().opsForValue()
            .setIfAbsent(redisKey, String.valueOf(value)))
        {
            return true;
        }
        return false;
    }
    
    /**
     * 解锁，只能解除在当前线程加的锁，否则什么也不会发生
     */
    public static void unlock(Long roundId, String studentId)
    {
        String redisKey = String.format(STD_STATUS_LOCK, roundId, studentId);
        getRedisTemplate().delete(redisKey);
    }
    
    /**
     * 给status 加锁，并返回key用于解锁(管理员代选)
     * @return true 成功 false 失败
     */
    public static boolean tryLockAdmin(Long calendarId, String studentId)
    {
        long value = System.currentTimeMillis();
        String redisKey = String.format(STD_STATUS_ADMIN_LOCK, calendarId, studentId);
        if (getRedisTemplate().opsForValue()
            .setIfAbsent(redisKey, String.valueOf(value)))
        {
            return true;
        }
        return false;
    }
    
    /**
     * 解锁，只能解除在当前线程加的锁，否则什么也不会发生(管理员代选)
     */
    public static void unlockAdmin(Long calendarId, String studentId)
    {
        String redisKey = String.format(STD_STATUS_ADMIN_LOCK, calendarId, studentId);
        getRedisTemplate().delete(redisKey);
    }
    
    /**
     * 设置选课申请管理课程
     */
    public static void setApplyCourse(Long calendarId, Set<String> courses)
    {
        ValueOperations<String, String> opsForValue =
            getRedisTemplate().opsForValue();
        String redisKey = Keys.getApplyCourseKey(calendarId);
        String value = opsForValue.get(redisKey);
        if (CollectionUtil.isNotEmpty(courses))
        {
            if (StringUtils.isNotBlank(value))
            {
                List<String> result = JSON.parseArray(value, String.class);
                for (String course : courses)
                {
                    if (!value.contains(course))
                    {
                        result.add(course);
                    }
                    else
                    {
                        result.remove(course);
                    }
                }
            }
            opsForValue.set(redisKey, JSON.toJSONString(courses));
        }
    }
    
    /**
     * 获取选课申请管理课程
     */
    public static List<String> getApplyCourse(Long calendarId)
    {
        ValueOperations<String, String> opsForValue =
            getRedisTemplate().opsForValue();
        String redisKey = Keys.getApplyCourseKey(calendarId);
        ;
        String value = opsForValue.get(redisKey);
        if (StringUtils.isEmpty(value))
        {
            return new ArrayList<>();
        }
        return JSON.parseArray(value, String.class);
    }
    
    /**
     * 获取替代课程
     */
    public static List<ElcNoGradCouSubs> getNoGradCouSubs(String projectId,
        Long calendarId)
    {
        ValueOperations<String, String> opsForValue =
            getRedisTemplate().opsForValue();
        String redisKey = Keys.getReplaceCourseKey(projectId, calendarId);
        String value = opsForValue.get(redisKey);
        if (StringUtils.isEmpty(value))
        {
            return new ArrayList<>();
        }
        return JSON.parseArray(value, ElcNoGradCouSubs.class);
    }
    
    /**
     * 设置替代课程
     */
    public static void setNoGradCouSubs(String projectId, Long calendarId,
        List<ElcNoGradCouSubs> list)
    {
        ValueOperations<String, String> opsForValue =
            getRedisTemplate().opsForValue();
        String redisKey = Keys.getReplaceCourseKey(projectId, calendarId);
        if (CollectionUtil.isNotEmpty(list))
        {
            opsForValue.set(redisKey, JSON.toJSONString(list));
        }
    }
    
}
