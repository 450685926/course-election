package com.server.edu.election.studentelec.service.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.service.AbstractElecQueue;
import com.server.edu.election.studentelec.service.ElecQueueComsumerService;
import com.server.edu.election.studentelec.service.ElecQueueService;

/**
 * 用 LinkedBlockingQueue 做消息队列
 */
//@Service("linkedBlockingQueue")
//@Primary
public class LinkedBlockingQueueService extends AbstractElecQueue
    implements ElecQueueService<ElecRequest>
{
    protected Logger LOG = LoggerFactory.getLogger(getClass());
    
    private final ConcurrentHashMap<String, LinkedBlockingQueue<ElecRequest>> groupQueueMap =
        new ConcurrentHashMap<>();
    
    @Override
    public boolean add(String group, ElecRequest data)
    {
        LinkedBlockingQueue<ElecRequest> queue = getQueue(group);
        return queue.add(data);
    }
    
    private LinkedBlockingQueue<ElecRequest> getQueue(String group)
    {
        LinkedBlockingQueue<ElecRequest> queue;
        if ((queue = groupQueueMap.get(group)) == null)
        {
            queue = new LinkedBlockingQueue<>();
            groupQueueMap.putIfAbsent(group, queue);
        }
        return queue;
    }
    
    @Override
    public void consume(String group,
        ElecQueueComsumerService<ElecRequest> comsumer)
    {
        try
        {
            LinkedBlockingQueue<ElecRequest> queue = getQueue(group);
            ElecRequest take = queue.take();
            if (null != take)
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
