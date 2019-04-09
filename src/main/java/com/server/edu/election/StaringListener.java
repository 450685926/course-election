package com.server.edu.election;

import org.apache.commons.lang3.StringUtils;
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
import com.server.edu.dmskafka.dataSync.data.StudentInfoSync;
import com.server.edu.dmskafka.dataSync.data.StudentStatusSync;
import com.server.edu.election.dao.CourseDao;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.entity.Course;
import com.server.edu.election.entity.Student;
import com.server.edu.election.util.CourseSyncUtil;
import com.server.edu.election.util.StuInfoSyncUtil;

import tk.mybatis.mapper.entity.Example;

@Component
public class StaringListener
    implements ApplicationListener<ApplicationReadyEvent>
{
    @Autowired
    private CourseDao courseDao;
    
    @Autowired
    private StudentDao studentDao;
    
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
                if (SyncType.COURSE == find)
                {
                    syncCourse(cons, find);
                }
                else if (SyncType.STU_BASE == find
                    || SyncType.STU_STATUS == find)
                {
                    syncStu(cons, find);
                }
            });
        });
    }
    
    private void syncCourse(ConsumerRecord<String, String> cons, SyncType find)
    {
        CourseSync c = JSON.parseObject(cons.value(), CourseSync.class);
        Course convert = CourseSyncUtil.convert(c);
        
        if (null != convert.getId())
        {
            Example example = new Example(Course.class);
            example.createCriteria().andEqualTo("id", convert.getId());
            int count = courseDao.selectCountByExample(example);
            if (count == 0)
            {
                courseDao.insertSelective(convert);
            }
            else
            {
                courseDao.updateByPrimaryKeySelective(convert);
            }
        }
    }
    
    private void syncStu(ConsumerRecord<String, String> cons, SyncType find)
    {
        
        Student convert = null;
        if (SyncType.STU_BASE == find)
        {
            StudentInfoSync c =
                JSON.parseObject(cons.value(), StudentInfoSync.class);
            convert = StuInfoSyncUtil.convertInfo(c);
        }
        else if (SyncType.STU_STATUS == find)
        {
            StudentStatusSync c =
                JSON.parseObject(cons.value(), StudentStatusSync.class);
            convert = StuInfoSyncUtil.convertStatus(c);
        }
        
        if (null != convert && StringUtils.isNotBlank(convert.getStudentCode()))
        {
            Example example = new Example(Course.class);
            example.createCriteria()
                .andEqualTo("studentCode", convert.getStudentCode());
            int count = studentDao.selectCountByExample(example);
            if (count == 0)
            {
                studentDao.insertSelective(convert);
            }
            else
            {
                studentDao.updateByPrimaryKeySelective(convert);
            }
        }
    }
    
}
