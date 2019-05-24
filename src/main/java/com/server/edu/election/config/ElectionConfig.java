package com.server.edu.election.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableCaching
public class ElectionConfig
{
    Logger LOG = LoggerFactory.getLogger(ElectionConfig.class);
    
    @Bean("redisTemplate2")
    public RedisTemplate<String, ?> redisTemplate2(
        RedisConnectionFactory factory)
    {
        RedisTemplate<String, ?> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer =
            new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashKeySerializer(template.getKeySerializer());
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        
        if (factory.getClass() == JedisConnectionFactory.class)
        {
            JedisConnectionFactory con = (JedisConnectionFactory)factory;
            
            LOG.info("---- reids ip: {}, port: {}, database: {} ----",
                con.getHostName(),
                con.getPort(),
                con.getDatabase());
        }
        return template;
    }
    
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory)
    {
        RedisCacheManager cacheManager =
            new RedisCacheManager(redisTemplate2(factory));
        cacheManager.setDefaultExpiration(300);
        cacheManager.setLoadRemoteCachesOnStartup(true); // 启动时加载远程缓存
        cacheManager.setUsePrefix(true); //是否使用前缀生成器
        return cacheManager;
    }
}
