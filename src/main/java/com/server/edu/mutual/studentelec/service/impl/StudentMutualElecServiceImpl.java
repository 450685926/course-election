package com.server.edu.mutual.studentelec.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.server.edu.common.rest.RestResult;
import com.server.edu.common.validator.Assert;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.service.ElecQueueService;
import com.server.edu.election.studentelec.service.cache.AbstractCacheService;
import com.server.edu.election.studentelec.service.impl.RoundDataProvider;
import com.server.edu.election.studentelec.utils.ElecContextUtil;
import com.server.edu.election.studentelec.utils.ElecStatus;
import com.server.edu.election.studentelec.utils.QueueGroups;
import com.server.edu.mutual.studentelec.service.ElecMutualQueueService;
import com.server.edu.mutual.studentelec.service.StudentMutualElecService;

@Service
public class StudentMutualElecServiceImpl extends AbstractCacheService 
	implements StudentMutualElecService 
{
	
	Logger LOG = LoggerFactory.getLogger(getClass());
	
    @Autowired
    private RoundDataProvider dataProvider;
	
    @Autowired
    private ElecQueueService<ElecRequest> queueService;

    @Autowired
    private ElecMutualQueueService<ElecRequest> queueMutualService;
	
	@Override
	public RestResult<ElecRespose> loading(ElecRequest elecRequest) {
        Long roundId = elecRequest.getRoundId();
        Integer chooseObj = elecRequest.getChooseObj();
        String studentId = elecRequest.getStudentId();
        String projectId = elecRequest.getProjectId();
        
//        // 研究生管理员代理选课
//        if (!Constants.PROJ_UNGRADUATE.equals(projectId)
//            && Objects.equals(ChooseObj.ADMIN.type(), chooseObj))
//        {
//            calendarId = elecRequest.getCalendarId();
//            elecRequest.setCalendarId(calendarId);
//        }
//        else
//        {
        ElectionRounds round = dataProvider.getRound(roundId);
        Assert.notNull(round, "elec.roundCourseExistTip");
        Long calendarId = round.getCalendarId();
        elecRequest.setCalendarId(calendarId);
//        }
        
        ElecStatus currentStatus =
            ElecContextUtil.getElecStatus(calendarId, studentId);
        // 如果当前是Init状态，进行初始化
        if (ElecStatus.Init.equals(currentStatus))
        {
            ElecContextUtil.saveElecResponse(studentId, new ElecRespose());
            // 加入队列
            currentStatus = ElecStatus.Loading;
            ElecContextUtil.setElecStatus(calendarId, studentId, currentStatus);
            if (!queueMutualService.add(QueueGroups.MUTUAL_STUDENT_LOADING, elecRequest))
            {
                currentStatus = ElecStatus.Init;
                ElecContextUtil
                    .setElecStatus(calendarId, studentId, currentStatus);
                return RestResult.fail("请稍后再试");
            }
        }
        
        return RestResult.successData(new ElecRespose(currentStatus));
	}

}
