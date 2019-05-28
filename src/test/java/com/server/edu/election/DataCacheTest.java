package com.server.edu.election;

import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.studentelec.service.impl.RoundDataProvider;
import com.server.edu.election.studentelec.utils.Keys;

@ActiveProfiles("dev")
public class DataCacheTest extends ApplicationTest
{
    @Autowired
    RoundDataProvider dataProvider;
    
    @Test
    public void test()
    {
        dataProvider.load();
        ElectionRounds round = dataProvider.getRound(44L);
        System.out.println(round);
    }
    
    @Autowired
    RedisTemplate<String, ElectionRounds> redisTemplate;
    
    @Test
    public void test2()
    {
        HashOperations<String, Long, ElectionRounds> hash =
            redisTemplate.opsForHash();
        
        //ElectionRounds object = hash.get(Keys.getRoundKey(), "1");
        //System.out.println(object);
        ElectionRounds s = new ElectionRounds();
        s.setId(1L);
        s.setCalendarId(1L);
        s.setCreatedAt(new Date());
        s.setElectionObj("1");
        s.setEndTime(new Date());
        s.setMode(1);
        s.setName("xxx");
        hash.put(Keys.getRoundKey(), 1L, s);
    }
}
