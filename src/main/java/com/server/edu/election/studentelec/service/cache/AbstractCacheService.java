package com.server.edu.election.studentelec.service.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;

public abstract class AbstractCacheService
{
    @SuppressWarnings("rawtypes")
    @Autowired
    @Qualifier("redisTemplate2")
    RedisTemplate redisTemplate;
    
    @SuppressWarnings({"unchecked"})
    public <T> RedisTemplate<String, T> redisTemplate(Class<T> clazz)
    {
        return redisTemplate;
    }
}
