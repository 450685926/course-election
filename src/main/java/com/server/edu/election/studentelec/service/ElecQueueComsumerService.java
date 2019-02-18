package com.server.edu.election.studentelec.service;

import java.io.Serializable;

/**
 * 队列消费
 * 继承runnable run() 作为监听线程
 */
public interface ElecQueueComsumerService<T> extends Runnable{

    /**
     * 队列消费方法
     */
    void consume(T data);

}
