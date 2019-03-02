package com.server.edu.election.studentelec.utils;

import static com.server.edu.election.studentelec.utils.Keys.STD_STATUS;
import static com.server.edu.election.studentelec.utils.Keys.STD_STATUS_LOCK;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.alibaba.fastjson.JSON;
import com.server.edu.dictionary.utils.SpringUtils;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.context.ElecRespose;

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
    
    private ElecContextUtil()
    {
    }
    
    private Long roundId;
    
    private String studentId;
    
    public static ElecContextUtil create(Long roundId, String studentId)
    {
        ElecContextUtil u = new ElecContextUtil();
        u.roundId = roundId;
        u.studentId = studentId;
        return u;
    }
    
    static StringRedisTemplate getRedisTemplate()
    {
        return SpringUtils.getBean(StringRedisTemplate.class);
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
        return JSON.parseArray(value, clazz);
    }
    
    private String getByKey(String type)
    {
        ValueOperations<String, String> opsForValue =
            getRedisTemplate().opsForValue();
        String value =
            opsForValue.get(Keys.STD + type + "-" + roundId + "-" + studentId);
        return value;
    }
    
    public void save(String type, Object value)
    {
        ValueOperations<String, String> opsForValue =
            getRedisTemplate().opsForValue();
        opsForValue.set(Keys.STD + type + "-" + roundId + "-" + studentId,
            JSON.toJSONString(value),
            5,
            TimeUnit.DAYS);
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
        getRedisTemplate().opsForValue()
            .set(String.format(STD_STATUS, roundId, studentId),
                status.toString(),
                1,
                TimeUnit.HOURS);
    }
    
    /**
     * 给status 加锁，并返回key用于解锁
     * @return true 成功 false 失败
     */
    public static boolean tryLock(Long roundId, String studentId)
    {
        String value = UUID.randomUUID().toString();
        String redisKey = String.format(STD_STATUS_LOCK, roundId, studentId);
        if (getRedisTemplate().opsForValue().setIfAbsent(redisKey, value))
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
        if (getRedisTemplate().hasKey(redisKey))
        {
            getRedisTemplate().delete(redisKey);
        }
    }
}
