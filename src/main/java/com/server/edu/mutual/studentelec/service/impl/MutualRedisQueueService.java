package com.server.edu.mutual.studentelec.service.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.collections.DefaultRedisList;
import org.springframework.data.redis.support.collections.RedisList;
import org.springframework.stereotype.Service;

import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.service.AbstractElecQueue;
import com.server.edu.mutual.studentelec.service.ElecMutualQueueService;
import com.server.edu.mutual.studentelec.service.MutualElecQueueComsumerService;

/**
 * 本研互选 用 Redis 做消息队列
 */
@Service("mutualRedisQueue")
@Primary
public class MutualRedisQueueService extends AbstractElecQueue
    implements ElecMutualQueueService<ElecRequest>
{
    protected Logger LOG = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private RedisTemplate<String, ElecRequest> redisTemplate;
    
    private final ConcurrentHashMap<String, RedisList<ElecRequest>> groupQueueMap =
        new ConcurrentHashMap<>();
    
    @Override
    public boolean add(String group, ElecRequest data)
    {
        RedisList<ElecRequest> queue = getQueue(group);
        return queue.add(data);
    }
    
    private RedisList<ElecRequest> getQueue(String group)
    {
        RedisList<ElecRequest> queue;
        if ((queue = groupQueueMap.get(group)) == null)
        {
            queue = new DefaultRedisList<>(group, redisTemplate);
            groupQueueMap.putIfAbsent(group, queue);
        }
        return queue;
    }
    
    @Override
    public void consume(String group,
    		MutualElecQueueComsumerService<ElecRequest> comsumer)
    {
        try
        {
            RedisList<ElecRequest> queue = getQueue(group);
            // redis在集群模式下不能使用take，会柱塞其他的连接，这时修改为poll
            ElecRequest take = queue.poll();
            if (null == take)
            {
                TimeUnit.SECONDS.sleep(1);
            }
            else
            {
                super.execute(() -> {
                    comsumer.consume(take);
                });
            }
        }
        catch (InterruptedException e)
        {
            LOG.error(e.getMessage(), e);
        }
    }
    
}
