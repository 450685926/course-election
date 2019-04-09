package com.server.edu.election.studentelec.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 队列消费
 */
public abstract class AbstractElecQueueComsumerService<T>
    implements ElecQueueComsumerService<T>
{
    protected Logger LOG = LoggerFactory.getLogger(getClass());
    
    private static ExecutorService starter = Executors.newFixedThreadPool(3);
    
    protected AbstractElecQueueComsumerService(String group,
        ElecQueueService<T> queueService)
    {
        // 启动
        starter.execute(() -> {
            while (true)
            {
                try
                {
                    queueService.consume(group, this);
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
