package com.server.edu.election.studentelec.service;

import com.server.edu.election.studentelec.utils.QueueGroups;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 队列消费
 * 继承runnable run() 作为监听线程
 * 仅当currentCapacity>0才会获取队列中的数据
 */
public abstract class AbstractElecQueueComsumerService<T> implements ElecQueueComsumerService<T>{
    private final int THREADS;
    protected final Serializable group;
    private final ExecutorService loadingThreadPool;
    private final Thread listenerThread = new Thread(this);
    private final ElecQueueService queueService;

    // 当前容量
    private final AtomicInteger currentCapacity;

    protected AbstractElecQueueComsumerService(int threadNumber, Serializable group, ElecQueueService queueService){
        this.THREADS = threadNumber;
        this.group = group;
        this.loadingThreadPool = Executors.newFixedThreadPool(THREADS);
        // 获取的数量应多于线程池大小
        // TODO 可配置
        this.currentCapacity = new AtomicInteger(2*THREADS + 1) ;
        this.queueService = queueService;
    }

    /** 开启监听 */
    protected void listen(String threadName){
        listenerThread.setName(threadName);
        listenerThread.start();
    }
    /** 完成一次消费 应在consume()方法执行结束 finally中执行 */
    protected void endConsume(){
        // 完成后当前容量+1
        currentCapacity.addAndGet(1);
    }

    @Override
    public void run() {
        while (true){
            if (currentCapacity.get()>0) {
                T data = queueService.comsumer(QueueGroups.STUDENT_LOADING);
                if (data != null) {
                    // 如果当前容量大于0，且队列中有数据
                    // 容量-1
                    currentCapacity.addAndGet(-1);
                    loadingThreadPool.execute(()-> consume(data));
                }
            }
        }
    }

}
