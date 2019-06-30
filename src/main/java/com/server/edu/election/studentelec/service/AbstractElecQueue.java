package com.server.edu.election.studentelec.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractElecQueue
{
    Logger logger = LoggerFactory.getLogger(getClass());
    
    /** 消费执行线程*/
    private ExecutorService comsumerThreadPool;
    
    /** 消费执行线程*/
    protected ExecutorService getPool()
    {
        if (null == comsumerThreadPool)
        {
            synchronized (this)
            {
                comsumerThreadPool = newFixedPool();
            }
        }
        return comsumerThreadPool;
    }
    
    //  活动的任务数
    AtomicInteger activeCount = new AtomicInteger(0);
    
    // 任务容量
    Integer taskCapacity = 100;
    
    public void execute(ElecTask task)
    {
        activeCount.incrementAndGet();
        getPool().execute(() -> {
            try
            {
                task.run();
            }
            finally
            {
                activeCount.decrementAndGet();
            }
        });
        
        checkWait();
    }
    
    /** 线程池队列满了之后等待*/
    private void checkWait()
    {
        try
        {
            while (activeCount.get() >= taskCapacity)
            {
                logger.info("comsumerThreadPool is full take thread sleep");
                TimeUnit.SECONDS.sleep(3);
            }
        }
        catch (InterruptedException e1)
        {
            logger.info(e1.getMessage(), e1);
        }
    }
    
    ThreadPoolExecutor newFixedPool()
    {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        return new ThreadPoolExecutor(availableProcessors, availableProcessors,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
            new MyThreadFactory());
    }
    
    /**
     * The default thread factory
     */
    static class MyThreadFactory implements ThreadFactory
    {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        
        private final ThreadGroup group;
        
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        
        private final String namePrefix;
        
        MyThreadFactory()
        {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup()
                : Thread.currentThread().getThreadGroup();
            namePrefix = "elecComsumerThreadPool-"
                + poolNumber.getAndIncrement() + "-thread-";
        }
        
        @Override
        public Thread newThread(Runnable r)
        {
            Thread t = new Thread(group, r,
                namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
    
    public static interface ElecTask
    {
        public void run();
    }
}
