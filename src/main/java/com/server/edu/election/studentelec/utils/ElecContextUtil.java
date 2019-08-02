package com.server.edu.election.studentelec.utils;

import static com.server.edu.election.studentelec.utils.Keys.STD_STATUS;
import static com.server.edu.election.studentelec.utils.Keys.STD_STATUS_LOCK;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.server.edu.dictionary.utils.SpringUtils;
import com.server.edu.election.entity.ElectionApply;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.vo.ElcNoGradCouSubsVo;
import com.server.edu.util.CollectionUtil;

import redis.clients.jedis.Jedis;

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
    
    static StringRedisTemplate stringRedisTemplate;
    
    static StringRedisTemplate getRedisTemplate()
    {
        if (stringRedisTemplate == null)
        {
            stringRedisTemplate =
                SpringUtils.getBean(StringRedisTemplate.class);
        }
        return stringRedisTemplate;
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
    public static ElecStatus getElecStatus(Long calendarId, String studentId)
    {
        String value = getRedisTemplate().opsForValue()
            .get(String.format(STD_STATUS, calendarId, studentId));
        if (StringUtils.isBlank(value))
        {
            logger.warn(
                "----- elecStatus not find use Init [calendarId:{}, studentId:{}] -----",
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
    public static void setElecStatus(Long calendarId, String studentId,
        ElecStatus status)
    {
        int timeout = 1;
        if (status == ElecStatus.Ready)
        {
            timeout = 10;
        }
        getRedisTemplate().opsForValue()
            .set(String.format(STD_STATUS, calendarId, studentId),
                status.toString(),
                timeout,
                TimeUnit.HOURS);
    }
    
    /**
     * 更新锁的生存时间，防止生存时间到了任务还没做完的情况
     * 
     */
    public static void updateLockTime()
    {
        if (CollectionUtil.isNotEmpty(lockKeys))
        {
            for (String key : lockKeys)
            {
                getRedisTemplate().expire(key, 30, TimeUnit.MINUTES);
            }
        }
    }
    
    static final List<String> lockKeys = new ArrayList<>();
    
    /**
     * 给status 加锁，默认生存时间为30分钟
     * @return true 成功 false 失败
     */
    public static boolean tryLock(Long calendarId, String studentId)
    {
        long value = System.currentTimeMillis();
        String redisKey = String.format(STD_STATUS_LOCK, calendarId, studentId);
        
        RedisConnectionFactory factory =
            getRedisTemplate().getConnectionFactory();
        RedisConnection conn = factory.getConnection();
        try
        {
            Jedis connection = (Jedis)conn.getNativeConnection();
            
            String statusCode = connection.set(redisKey,
                String.valueOf(value),
                "NX",
                "EX",
                TimeUnit.MINUTES.toSeconds(30));
            
            if ("OK".equals(statusCode))
            {
                lockKeys.add(redisKey);
                return true;
            }
            
            return false;
        }
        finally
        {
            RedisConnectionUtils.releaseConnection(conn, factory);
        }
    }
    
    /**
     * 解锁，只能解除在当前线程加的锁，否则什么也不会发生
     */
    public static void unlock(Long calendarId, String studentId)
    {
        String redisKey = String.format(STD_STATUS_LOCK, calendarId, studentId);
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
    public static List<ElcNoGradCouSubsVo> getNoGradCouSubs(String studentId)
    {
        ValueOperations<String, String> opsForValue =
            getRedisTemplate().opsForValue();
        String redisKey = Keys.getReplaceCourseKey(studentId);
        String value = opsForValue.get(redisKey);
        if (StringUtils.isEmpty(value))
        {
            return new ArrayList<>();
        }
        return JSON.parseArray(value, ElcNoGradCouSubsVo.class);
    }
    
    /**
     * 设置替代课程
     */
    public static void setNoGradCouSubs(String studentId,
        List<ElcNoGradCouSubsVo> list)
    {
        ValueOperations<String, String> opsForValue =
            getRedisTemplate().opsForValue();
        String redisKey = Keys.getReplaceCourseKey(studentId);
        if (CollectionUtil.isNotEmpty(list))
        {
            opsForValue.set(redisKey, JSON.toJSONString(list));
        }
    }
    
}
