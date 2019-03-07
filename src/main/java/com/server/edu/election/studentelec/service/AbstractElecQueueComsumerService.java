package com.server.edu.election.studentelec.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 队列消费
 * 继承runnable run() 作为监听线程
 */
public abstract class AbstractElecQueueComsumerService<T>
    implements ElecQueueComsumerService<T>
{
    private static final Logger LOG =
        LoggerFactory.getLogger(AbstractElecQueueComsumerService.class);
    
    private final int THREADS;
    
    protected final String group;
    
    /** 消费执行线程*/
    private final ExecutorService comsumerThreadPool;
    /** 队列获取线程池 */
    private static ExecutorService starter = Executors.newFixedThreadPool(10);
    
    private final ElecQueueService<T> queueService;
    
    protected AbstractElecQueueComsumerService(int threadNumber,
        String group, ElecQueueService<T> queueService)
    {
        this.THREADS = threadNumber;
        this.group = group;
        this.comsumerThreadPool = Executors.newFixedThreadPool(THREADS);
        this.queueService = queueService;
        // 扔进线程池从队列中获取数据
        starter.execute(this);
    }
    
    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                T data = queueService.take(this.group);
                if (data != null)
                {
                    comsumerThreadPool.execute(() -> consume(data));
                }
            }
            catch (InterruptedException e)
            {
                LOG.error("----- error happen at:{} -----",this.group);
                LOG.error(e.getMessage(), e);
            }
        }
    }
    
}
