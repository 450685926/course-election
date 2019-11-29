package com.server.edu.mutual.studentelec.service;

/**
 * 本研互选队列消费
 */
public interface MutualElecQueueComsumerService<T>
{
    
    /**
     * 队列消费方法
     */
    void consume(T data);
    
}