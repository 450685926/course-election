package com.server.edu.election.studentelec.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.server.edu.dmskafka.clients.KafkaClients;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.service.AbstractElecQueue;
import com.server.edu.election.studentelec.service.ElecQueueComsumerService;
import com.server.edu.election.studentelec.service.ElecQueueService;
import com.server.edu.election.studentelec.utils.QueueGroups;

/**
 * 用 Kafka 做消息队列
 */
//@Service("kafkaQueue")
public class KafkaQueueService extends AbstractElecQueue
    implements ElecQueueService<ElecRequest>
{
    private static final Logger LOG =
        LoggerFactory.getLogger(KafkaQueueService.class);
    
    private final ConcurrentHashMap<String, Producer<String, String>> groupQueueMap =
        new ConcurrentHashMap<>();
    
    Map<String, String> groupMap = new HashMap<>();
    
    public KafkaQueueService()
    {
        this.groupMap.put(QueueGroups.STUDENT_LOADING, "");// 增加对应的group-id
        this.groupMap.put(QueueGroups.STUDENT_ELEC, "");// 增加对应的group-id
    }
    
    @Override
    public boolean add(String group, ElecRequest data)
    {
        Producer<String, String> queue = getQueue(group);
        
        ProducerRecord<String, String> producerRecord =
            new ProducerRecord<String, String>(group, null,
                JSON.toJSONString(data));
        
        queue.send(producerRecord);
        
        return true;
    }
    
    private Producer<String, String> getQueue(String group)
    {
        Producer<String, String> queue;
        if ((queue = groupQueueMap.get(group)) == null)
        {
            Properties properties = new Properties();
            properties.setProperty("topic", groupMap.get(group));
            try
            {
                queue = KafkaClients.createProducer(properties);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            groupQueueMap.putIfAbsent(group, queue);
        }
        return queue;
    }
    
    @Override
    public void consume(String group,
        ElecQueueComsumerService<ElecRequest> comsumer)
    {
        Properties properties = new Properties();
        properties.setProperty("topic", groupMap.get(group));
        try
        {
            KafkaClients.consumeMsg(cons -> {
                super.execute(() -> {
                    comsumer.consume(
                        JSON.parseObject(cons.value(), ElecRequest.class));
                });
            }, properties);
        }
        catch (IOException e)
        {
            LOG.error(e.getMessage(), e);
        }
    }
    
}
