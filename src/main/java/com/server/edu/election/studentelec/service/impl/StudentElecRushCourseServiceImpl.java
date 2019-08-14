package com.server.edu.election.studentelec.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.server.edu.election.constants.Constants;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.IElecContext;
import com.server.edu.election.studentelec.service.AbstractElecQueueComsumerService;
import com.server.edu.election.studentelec.service.ElecBkService;
import com.server.edu.election.studentelec.service.ElecQueueService;
import com.server.edu.election.studentelec.service.ElecYjsService;
import com.server.edu.election.studentelec.service.StudentElecRushCourseService;
import com.server.edu.election.studentelec.utils.ElecContextUtil;
import com.server.edu.election.studentelec.utils.ElecStatus;
import com.server.edu.election.studentelec.utils.QueueGroups;

@Service
public class StudentElecRushCourseServiceImpl
    extends AbstractElecQueueComsumerService<ElecRequest>
    implements StudentElecRushCourseService
{
    private static final Logger LOG =
        LoggerFactory.getLogger(StudentElecRushCourseServiceImpl.class);
    
    @Autowired
    private ElecBkService bkService;
    
    @Autowired
    private ElecYjsService yjsService;
    
    @Autowired
    protected StudentElecRushCourseServiceImpl(
        ElecQueueService<ElecRequest> queueService)
    {
        super(QueueGroups.STUDENT_ELEC, queueService);
    }
    
    @Override
    public void consume(ElecRequest request)
    {
        String projectId = request.getProjectId();
        String studentId = request.getStudentId();
        Long calendarId = request.getCalendarId();
        
        IElecContext context = null;
        try
        {
            Assert.notNull(projectId, "projectId must be not null");
            Assert.notNull(calendarId, "calendarId must be not null");
            
            if (Constants.PROJ_UNGRADUATE.equals(projectId))
            {
                context = bkService.doELec(request);
            }
            else
            {
                context = yjsService.doELec(request);
            }
        }
        catch (Exception e)
        {
            LOG.error(e.getMessage(), e);
            if (context != null)
            {
                context.getRespose()
                    .getFailedReasons()
                    .put("error", e.getMessage());
            }
        }
        finally
        {
            // 不管选课有没有成功，结束时表示可以进行下一个选课请求
            ElecContextUtil
                .setElecStatus(calendarId, studentId, ElecStatus.Ready);
            if (null != context)
            {
                // 数据保存到缓存
                context.saveToCache();
            }
        }
        
    }
    
}
