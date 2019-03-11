package com.server.edu.election.studentelec.service;

/**
 * 队列消费
 */
public interface ElecQueueComsumerService<T>
{
    
    /**
     * 队列消费方法
     */
    void consume(T data);
    
}
