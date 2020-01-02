package com.server.edu.mutual.studentelec.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.server.edu.election.constants.Constants;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.IElecContext;
import com.server.edu.election.studentelec.utils.ElecContextUtil;
import com.server.edu.election.studentelec.utils.ElecStatus;
import com.server.edu.mutual.studentelec.service.AbstractMutualElecQueueComsumerService;
import com.server.edu.mutual.studentelec.service.ElecMutualQueueService;
import com.server.edu.mutual.studentelec.service.MutualElecBkService;
import com.server.edu.mutual.studentelec.service.MutualElecYjsService;
import com.server.edu.mutual.studentelec.service.MutualStudentElecRushCourseService;
import com.server.edu.mutual.studentelec.utils.MutualQueueGroups;

public class MutualStudentElecRushCourseServiceImpl extends AbstractMutualElecQueueComsumerService<ElecRequest>
      implements MutualStudentElecRushCourseService{
	
    private static final Logger LOG =
            LoggerFactory.getLogger(MutualStudentElecRushCourseServiceImpl.class);
	
    @Autowired
    private MutualElecBkService mutualElecBkService;

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
        
        LOG.info("-----------88888------projectId:"+ projectId + "--------------------");
        LOG.info("-----------99999------studentId:"+ studentId + "--------------------");
        LOG.info("-----------101010------calendarId:"+ calendarId + "--------------------");
        IElecContext context = null;
        
        try
        {
            Assert.notNull(projectId, "projectId must be not null");
            Assert.notNull(calendarId, "calendarId must be not null");
            
            if (Constants.PROJ_UNGRADUATE.equals(projectId))
            {
            	LOG.info("-----------------111111111111 start doElec --------------------------");
                context = mutualElecBkService.doELec(request);
            }
//            else
//            {
//                context = mutualElecYjsService.doELec(request);
//            }
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
            
            LOG.info("-----------------111222 elec status:" + ElecContextUtil.getElecStatus(calendarId, studentId) + "--------------------------");
            
            
            if (null != context)
            {
                // 数据保存到缓存
            	LOG.info("-----------------111333 elec saveToCache--------------------------");
                context.saveToCache();
            }
        }
	}

}
