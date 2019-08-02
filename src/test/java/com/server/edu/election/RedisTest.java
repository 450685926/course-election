package com.server.edu.election;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands.SetOption;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("dev")
public class RedisTest extends ApplicationTest
{
    @Autowired
    StringRedisTemplate redisTemplate;
    
    @Test
    public void test()
    {
        StringRedisSerializer keySerializer =
            (StringRedisSerializer)redisTemplate.getKeySerializer();
        redisTemplate.getConnectionFactory()
            .getConnection()
            .set(keySerializer.serialize("test"),
                keySerializer.serialize("test"),
                Expiration.seconds(30),
                SetOption.SET_IF_ABSENT);
    }
}
