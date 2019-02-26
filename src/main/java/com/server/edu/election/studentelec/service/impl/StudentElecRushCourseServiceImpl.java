package com.server.edu.election.studentelec.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.service.AbstractElecQueueComsumerService;
import com.server.edu.election.studentelec.service.ElecQueueService;
import com.server.edu.election.studentelec.service.StudentElecRushCourseService;
import com.server.edu.election.studentelec.utils.QueueGroups;

@Service
public class StudentElecRushCourseServiceImpl
    extends AbstractElecQueueComsumerService<ElecRequest>
    implements StudentElecRushCourseService
{
    private static final Logger LOG =
        LoggerFactory.getLogger(StudentElecRushCourseServiceImpl.class);
    
    @Autowired
    protected StudentElecRushCourseServiceImpl(ElecQueueService<ElecRequest> queueService)
    {
        super(4, QueueGroups.STUDENT_ELEC, queueService);
        super.listen("thread-rushcourses");
    }
    
    @Override
    public void consume(ElecRequest data)
    {
        LOG.info("");
        // TODO 检查约束条件，抢课
        // 检查约束
        // 抢课
        
    }
}
