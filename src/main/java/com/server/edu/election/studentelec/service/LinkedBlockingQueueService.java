package com.server.edu.election.studentelec.service;

import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 用 LinkedBlockingDeque 做消息队列
 * TODO 临时使用 要用消息队列代替
 */
@Service("simple")
public class LinkedBlockingQueueService implements ElecQueueService {
    private final ConcurrentHashMap<Serializable,LinkedBlockingDeque> groupQueueMap = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T> boolean produce(Serializable group, T data) {
        LinkedBlockingDeque<T> queue;
        if((queue = groupQueueMap.get(group)) == null){
            queue = new LinkedBlockingDeque<>();
            groupQueueMap.putIfAbsent(group, queue);
        }
        return queue.add(data);
    }

    @Override
    public<T> T comsumer(Serializable group) {
        @SuppressWarnings("unchecked")
        LinkedBlockingDeque<T> queue = groupQueueMap.get(group);
        if (queue == null) {
            return null;
        }
        return queue.poll();
    }
}
