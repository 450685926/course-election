package com.server.edu.election.studentelec.service;

import java.io.Serializable;

/**
 * 消息队列接口
 * TODO 当前实现类<tt>LinkedBlockingQueueService</tt>使用LinkedBlockingDeque 做消息队列 ，以后应替换为消息队列中间件
 */
public interface ElecQueueService<V>
{
    /**
     * 向队列中添加消息
     * 
     * @param group
     * @param data
     * @return true成功，false失败
     * @see [类、类#方法、类#成员]
     */
    boolean add(Serializable group, V data);
    
    /**
     * 向队列中取出消息，当没有队列中数据为空是阻塞
     * 
     * @param group
     * @return
     * @throws InterruptedException
     * @see [类、类#方法、类#成员]
     */
    V take(Serializable group)
        throws InterruptedException;
}
