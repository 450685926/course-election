package com.server.edu.election.studentelec.service.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.service.ElecQueueComsumerService;
import com.server.edu.election.studentelec.service.ElecQueueService;

/**
 * 用 LinkedBlockingDeque 做消息队列
 * TODO 临时使用 要用消息队列代替
 */
@Service("simple")
@Primary
public class LinkedBlockingQueueService implements ElecQueueService<ElecRequest>
{
    protected Logger LOG = LoggerFactory.getLogger(getClass());
    
    private final ConcurrentHashMap<String, LinkedBlockingDeque<ElecRequest>> groupQueueMap =
        new ConcurrentHashMap<>();
    
    /** 消费执行线程*/
    private final ExecutorService comsumerThreadPool =
        Executors.newFixedThreadPool(8);
    
    @Override
    public boolean add(String group, ElecRequest data)
    {
        LinkedBlockingDeque<ElecRequest> queue = getQueue(group);
        return queue.add(data);
    }
    
    private LinkedBlockingDeque<ElecRequest> getQueue(String group)
    {
        LinkedBlockingDeque<ElecRequest> queue;
        if ((queue = groupQueueMap.get(group)) == null)
        {
            queue = new LinkedBlockingDeque<>();
            groupQueueMap.putIfAbsent(group, queue);
        }
        return queue;
    }
    
    @Override
    public void consume(String group, ElecQueueComsumerService<ElecRequest> comsumer)
    {
        try
        {
            LinkedBlockingDeque<ElecRequest> queue = getQueue(group);
            ElecRequest take = queue.take();
            
            comsumerThreadPool.execute(() -> {
                comsumer.consume(take);
            });
        }
        catch (InterruptedException e)
        {
            LOG.error(e.getMessage(), e);
        }
    }
    
}
