package com.server.edu.election;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands.SetOption;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.test.context.ActiveProfiles;

import redis.clients.jedis.Jedis;

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
    
    @Test
    public void test1()
    {
        String statusCode =
            redisTemplate.execute(new RedisCallback<String>()
            {
                @Override
                public String doInRedis(RedisConnection connection)
                {
                    Jedis conn = (Jedis)connection.getNativeConnection();
                    return conn.set("test",  "test",
                        "NX",
                        "EX",
                        TimeUnit.MINUTES.toSeconds(30));
                }
            }, true);
        
        if("OK".equals(statusCode)) {
            System.out.println("============================== success");
        }
        System.out.println("============================== code = "+statusCode);
    }
}
