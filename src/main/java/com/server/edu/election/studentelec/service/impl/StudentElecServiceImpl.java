package com.server.edu.election.studentelec.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.server.edu.common.rest.RestResult;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.service.ElecQueueService;
import com.server.edu.election.studentelec.service.StudentElecService;
import com.server.edu.election.studentelec.service.StudentElecStatusService;
import com.server.edu.election.studentelec.utils.ElecStatus;
import com.server.edu.election.studentelec.utils.QueueGroups;

@Service
public class StudentElecServiceImpl implements StudentElecService
{
    @Autowired
    private StudentElecStatusService elecStatusService;
    
    @Autowired
    private ElecQueueService<ElecRequest> queueService;
    
    @Override
    public RestResult<ElecRespose> loading(Integer roundId, String studentId)
    {
        ElecStatus currentStatus =
            elecStatusService.getElecStatus(roundId, studentId);
        // 如果当前是Init状态，且加锁成功
        // TODO 如果整轮选课期间只加载一次，数据不过期，那这样就够了，如果数据会过期，还需要另外判断数据过期后修改状态为Init
        if (ElecStatus.Init.equals(currentStatus)
            && elecStatusService.tryLock(roundId, studentId))
        {
            try
            {
                // 加锁后二次检查
                currentStatus =
                    elecStatusService.getElecStatus(roundId, studentId);
                if (ElecStatus.Init.equals(currentStatus))
                {
                    // 加入队列
                    if (loadingInQueue(roundId, studentId))
                    {
                        elecStatusService.setElecStatus(roundId,
                            studentId,
                            ElecStatus.Loading);
                        return RestResult
                            .successData(new ElecRespose(ElecStatus.Loading));
                    }
                    else
                    {
                        return RestResult.fail("请稍后再试");
                    }
                    
                }
                else
                {
                    return RestResult
                        .successData(new ElecRespose(currentStatus));
                }
            }
            finally
            {
                elecStatusService.unlock(roundId, studentId);
            }
        }
        return RestResult.successData(new ElecRespose(currentStatus));
    }
    
    @Override
    public RestResult<ElecRespose> elect(Integer roundId, String studentId,
        ElecRequest elecRequest)
    {
        if (elecRequest.getElecTeachingClasses().size() == 0)
        {
            return RestResult.fail("你没选择任何课程");
        }
        elecRequest.setStudentId(studentId);
        ElecStatus currentStatus =
            elecStatusService.getElecStatus(roundId, studentId);
        if (ElecStatus.Ready.equals(currentStatus)
            && elecStatusService.tryLock(roundId, studentId))
        {
            try
            {
                // 加锁后二次检查
                currentStatus =
                    elecStatusService.getElecStatus(roundId, studentId);
                if (ElecStatus.Ready.equals(currentStatus))
                {
                    // 加入选课队列
                    if (elecInQueue(roundId, studentId, elecRequest))
                    {
                        elecStatusService.setElecStatus(roundId,
                            studentId,
                            ElecStatus.Processing);
                        currentStatus = ElecStatus.Processing;
                    }
                    else
                    {
                        return RestResult.fail("请稍后再试");
                    }
                    
                }
            }
            finally
            {
                elecStatusService.unlock(roundId, studentId);
            }
        }
        return RestResult.successData(new ElecRespose(currentStatus));
    }
    
    private boolean loadingInQueue(Integer roundId, String studentId)
    {
        ElecRequest loadingRequest = new ElecRequest();
        loadingRequest.setRoundId(roundId);
        loadingRequest.setStudentId(studentId);
        return queueService.add(QueueGroups.STUDENT_LOADING, loadingRequest);
    }
    
    private boolean elecInQueue(Integer roundId, String studentId,
        ElecRequest elecRequest)
    {
        elecRequest.setRoundId(roundId);
        elecRequest.setStudentId(studentId);
        return queueService.add(QueueGroups.STUDENT_ELEC, elecRequest);
    }
    
}
