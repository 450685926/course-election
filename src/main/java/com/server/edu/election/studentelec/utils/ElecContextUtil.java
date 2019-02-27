package com.server.edu.election.studentelec.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.alibaba.fastjson.JSON;
import com.server.edu.dictionary.utils.SpringUtils;

public class ElecContextUtil
{
    private ElecContextUtil()
    {
        
    }
    
    private String studentId;
    
    public static ElecContextUtil create(String studentId)
    {
        ElecContextUtil u = new ElecContextUtil();
        u.studentId = studentId;
        return u;
    }
    
    StringRedisTemplate getRedisTemplate()
    {
        return SpringUtils.getBean(StringRedisTemplate.class);
    }
    
    public <T> T getObject(String key, Class<T> clazz)
    {
        String value = getByKey(key);
        return JSON.parseObject(value, clazz);
    }
    
    public <T> List<T> getList(String key, Class<T> clazz)
    {
        String value = getByKey(key);
        
        if (StringUtils.isEmpty(value))
        {
            return new ArrayList<>();
        }
        return JSON.parseArray(value, clazz);
    }
    
    public String getByKey(String key)
    {
        ValueOperations<String, String> opsForValue =
            getRedisTemplate().opsForValue();
        String value = opsForValue.get(Keys.STD + key + "-" + studentId);
        return value;
    }
    
    public void save(String key, Object value)
    {
        ValueOperations<String, String> opsForValue =
            getRedisTemplate().opsForValue();
        opsForValue.set(Keys.STD + key + "-" + studentId,
            JSON.toJSONString(value));
    }
}
