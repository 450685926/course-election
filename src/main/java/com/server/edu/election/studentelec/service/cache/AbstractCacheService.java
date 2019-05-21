package com.server.edu.election.studentelec.service.cache;

import org.springframework.data.redis.core.RedisTemplate;

import com.server.edu.dictionary.utils.SpringUtils;

public abstract class AbstractCacheService
{
    @SuppressWarnings({"unchecked"})
    static <T> RedisTemplate<String, T> redisTemplate(Class<T> clazz)
    {
        RedisTemplate<String, T> redisTemplate =
            SpringUtils.getBean("redisTemplate2", RedisTemplate.class);
        return redisTemplate;
    }
}
