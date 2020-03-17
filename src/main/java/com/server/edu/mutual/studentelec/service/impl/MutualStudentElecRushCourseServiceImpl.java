package com.server.edu.mutual.studentelec.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.IElecContext;
import com.server.edu.election.studentelec.utils.ElecContextUtil;
import com.server.edu.election.studentelec.utils.ElecStatus;
import com.server.edu.mutual.studentelec.service.AbstractMutualElecQueueComsumerService;
import com.server.edu.mutual.studentelec.service.ElecMutualQueueService;
import com.server.edu.mutual.studentelec.service.MutualElecService;
import com.server.edu.mutual.studentelec.service.MutualStudentElecRushCourseService;
import com.server.edu.mutual.studentelec.utils.MutualQueueGroups;

@Service
public class MutualStudentElecRushCourseServiceImpl extends AbstractMutualElecQueueComsumerService<ElecRequest>
      implements MutualStudentElecRushCourseService{
	
    private static final Logger LOG =
            LoggerFactory.getLogger(MutualStudentElecRushCourseServiceImpl.class);
	
    @Autowired
    private MutualElecService mutualElecService;

    public MutualStudentElecRushCourseServiceImpl(
    		ElecMutualQueueService<ElecRequest> mutualQueueService){
    	super(MutualQueueGroups.MUTUAL_STUDENT_ELEC, mutualQueueService);
    }

//    @Autowired
//    private MutualElecYjsService mutualElecYjsService;
    
    @SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void consume(ElecRequest request) {
        String projectId = request.getProjectId();
        String studentId = request.getStudentId();
        Long calendarId = request.getCalendarId();
        
        IElecContext context = null;
        try
        {
            Assert.notNull(projectId, "projectId must be not null");
            Assert.notNull(calendarId, "calendarId must be not null");

            context = mutualElecService.doELec(request);
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
        	if (null != context)
        	{
        		context.saveToCache();
        	}
            ElecContextUtil.setElecStatus(calendarId, studentId, ElecStatus.Ready);
        }
	}

}
