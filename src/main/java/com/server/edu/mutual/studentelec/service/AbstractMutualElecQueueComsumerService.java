package com.server.edu.mutual.studentelec.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 本研互选队列消费
 */
public abstract class AbstractMutualElecQueueComsumerService<T>
    implements MutualElecQueueComsumerService<T>
{
    protected Logger LOG = LoggerFactory.getLogger(getClass());
    
    private static ExecutorService starter = Executors.newFixedThreadPool(3);
    
    protected AbstractMutualElecQueueComsumerService(String group,
         ElecMutualQueueService<T> mutualQueueService)   
    {
        // 启动
        starter.execute(() -> {
            Thread.currentThread()
                .setName("elecConsume-" + Thread.currentThread().getName());
            while (true)
            {
                try
                {
                	mutualQueueService.consume(group, this);
                }
                catch (Exception e)
                {
                    LOG.error(e.getMessage(), e);
                    // 异常停45秒后再试
                    try
                    {
                        TimeUnit.SECONDS.sleep(45);
                    }
                    catch (InterruptedException e1)
                    {
                        LOG.error(e1.getMessage(), e1);
                    }
                }
            }
        });
    }
    
}
