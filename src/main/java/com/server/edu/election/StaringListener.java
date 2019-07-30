package com.server.edu.election;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class StaringListener
    implements ApplicationListener<ApplicationReadyEvent>
{
//    @Autowired
//    private StudentDao studentDao;
//    
//    @Autowired
//    private ThreadPoolTaskExecutor taskExecutor;
    
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event)
    {
        /*taskExecutor.execute(() -> {
            SyncReceiver.receive(cons -> {
                SyncType find = SyncType.find(cons.key());
                if (SyncType.STU_BASE == find
                    || SyncType.STU_STATUS == find)
                {
                    syncStu(cons, find);
                }
            });
        });*/
    }
    
    /*private void syncStu(ConsumerRecord<String, String> cons, SyncType find)
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
            Example example = new Example(Student.class);
            example.createCriteria()
                .andEqualTo("studentCode", convert.getStudentCode());
            int count = studentDao.selectCountByExample(example);
            if (count == 0)
            {
                studentDao.insertSelective(convert);
            }
            else
            {
            	convert.setStudentCode(null);
                studentDao.updateByExampleSelective(convert, example);
            }
        }
    }*/
    
}
