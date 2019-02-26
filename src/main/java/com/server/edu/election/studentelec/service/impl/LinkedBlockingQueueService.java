package com.server.edu.election.studentelec.service.impl;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

import org.springframework.stereotype.Service;

import com.server.edu.election.studentelec.service.ElecQueueService;

/**
 * 用 LinkedBlockingDeque 做消息队列
 * TODO 临时使用 要用消息队列代替
 */
@Service("simple")
public class LinkedBlockingQueueService<T> implements ElecQueueService<T>
{
    private final ConcurrentHashMap<Serializable, LinkedBlockingDeque<?>> groupQueueMap =
        new ConcurrentHashMap<>();
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean add(Serializable group, T data)
    {
        LinkedBlockingDeque<T> queue;
        if ((queue = (LinkedBlockingDeque<T>)groupQueueMap.get(group)) == null)
        {
            queue = new LinkedBlockingDeque<>();
            groupQueueMap.putIfAbsent(group, queue);
        }
        return queue.add(data);
    }
    
    @Override
    public T take(Serializable group)
        throws InterruptedException
    {
        @SuppressWarnings("unchecked")
        LinkedBlockingDeque<T> queue =
            (LinkedBlockingDeque<T>)groupQueueMap.get(group);
        if (queue == null)
        {
            return null;
        }
        return queue.take();
    }
}
