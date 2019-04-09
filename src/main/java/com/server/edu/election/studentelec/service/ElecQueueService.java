package com.server.edu.election.studentelec.service;

/**
 * 消息队列接口
 */
public interface ElecQueueService<T>
{
    /**
     * 向队列中添加消息
     * 
     * @param group
     * @param data
     * @return true成功，false失败
     * @see [类、类#方法、类#成员]
     */
    boolean add(String group, T data);
    
    /**
     * 向队列中取出消息，当没有队列中数据为空是阻塞
     * 
     * @param group
     * @return
     * @throws InterruptedException
     * @see [类、类#方法、类#成员]
     */
    void consume(String group, ElecQueueComsumerService<T> comsumer);
}
