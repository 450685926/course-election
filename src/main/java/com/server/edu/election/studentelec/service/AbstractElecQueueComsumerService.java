package com.server.edu.election.studentelec.service;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.server.edu.election.studentelec.utils.QueueGroups;

/**
 * 队列消费
 * 继承runnable run() 作为监听线程
 * 仅当currentCapacity>0才会获取队列中的数据
 */
public abstract class AbstractElecQueueComsumerService<T>
    implements ElecQueueComsumerService<T>
{
    private static final Logger LOG =
        LoggerFactory.getLogger(AbstractElecQueueComsumerService.class);
    
    private final int THREADS;
    
    protected final Serializable group;
    
    private final ExecutorService comsumerThreadPool;
    
    private final Thread listenerThread = new Thread(this);
    
    private final ElecQueueService<T> queueService;
    
    protected AbstractElecQueueComsumerService(int threadNumber,
        Serializable group, ElecQueueService<T> queueService)
    {
        this.THREADS = threadNumber;
        this.group = group;
        this.comsumerThreadPool = Executors.newFixedThreadPool(THREADS);
        this.queueService = queueService;
    }
    
    /** 开启监听 */
    protected void listen(String threadName)
    {
        listenerThread.setName(threadName);
        listenerThread.start();
    }
    
    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                T data = queueService.take(QueueGroups.STUDENT_LOADING);
                if (data != null)
                {
                    comsumerThreadPool.execute(() -> consume(data));
                }
            }
            catch (InterruptedException e)
            {
                LOG.error(e.getMessage(), e);
            }
        }
    }
    
}
