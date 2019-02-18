package com.server.edu.election.studentelec.service;

import java.io.Serializable;


/**
 * 消息队列接口
 * TODO 当前实现类<tt>LinkedBlockingQueueService</tt>使用LinkedBlockingDeque 做消息队列 ，以后应替换为消息队列中间件
 */
public interface ElecQueueService {
    <V> boolean produce(Serializable group, V data);
    <V> V comsumer(Serializable group);
}
