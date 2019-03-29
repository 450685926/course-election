package com.server.edu.election;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.server.edu.dmskafka.dataSync.SyncReceiver;
import com.server.edu.dmskafka.dataSync.SyncType;
import com.server.edu.dmskafka.dataSync.data.CourseSync;
import com.server.edu.election.dao.CourseDao;
import com.server.edu.election.entity.Course;
import com.server.edu.election.util.CourseSyncUtil;

import tk.mybatis.mapper.entity.Example;

@Component
public class StaringListener
    implements ApplicationListener<ApplicationReadyEvent>
{
    @Autowired
    private CourseDao courseDao;
    
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;
    
    @Autowired
    private Environment environment;
    
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event)
    {
        taskExecutor.execute(() -> {
            String groupId = environment.getProperty("kafkaDataSyncGroupId");
            SyncReceiver.receive(groupId, cons -> {
                SyncType find = SyncType.find(cons.key());
                syncCourse(cons, find);
            });
        });
        
    }

    private void syncCourse(ConsumerRecord<String, String> cons, SyncType find)
    {
        if (SyncType.COURSE == find)
        {
            CourseSync c =
                JSON.parseObject(cons.value(), CourseSync.class);
            Course convert = CourseSyncUtil.convert(c);
            
            if (null != convert.getId())
            {
                Example example = new Example(Course.class);
                example.createCriteria().andEqualTo("id", convert.getId());
                int count =
                    courseDao.selectCountByExample(example);
                if (count == 0)
                {
                    courseDao.insertSelective(convert);
                }
                else
                {
                    courseDao.updateByPrimaryKey(convert);
                }
            }
        }
    }
    
}
