package com.server.edu.election.studentelec.service.impl;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.server.edu.common.rest.RestResult;
import com.server.edu.election.constants.ChooseObj;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.entity.Student;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.service.ElecQueueService;
import com.server.edu.election.studentelec.service.StudentElecService;
import com.server.edu.election.studentelec.service.cache.AbstractCacheService;
import com.server.edu.election.studentelec.utils.ElecContextUtil;
import com.server.edu.election.studentelec.utils.ElecStatus;
import com.server.edu.election.studentelec.utils.QueueGroups;
import com.server.edu.util.CollectionUtil;

@Service
public class StudentElecServiceImpl extends AbstractCacheService
    implements StudentElecService
{
    Logger LOG = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private ElecQueueService<ElecRequest> queueService;
    
    @Autowired
    private RoundDataProvider dataProvider;
    
    @Autowired
    private StudentDao stuDao;
    
    @Override
    public RestResult<ElecRespose> loading(ElecRequest elecRequest)
    {
        Long roundId = elecRequest.getRoundId();
        Integer chooseObj = elecRequest.getChooseObj();
        String studentId = elecRequest.getStudentId();
        String projectId = elecRequest.getProjectId();
        Long calendarId = null;
        
        // 研究生管理员代理选课
        if (!Constants.PROJ_UNGRADUATE.equals(projectId)
            && Objects.equals(ChooseObj.ADMIN.type(), chooseObj))
        {
            calendarId = elecRequest.getCalendarId();
            elecRequest.setCalendarId(calendarId);
        }
        else
        {
            ElectionRounds round = dataProvider.getRound(roundId);
            calendarId = round.getCalendarId();
            elecRequest.setCalendarId(calendarId);
        }
        
        ElecStatus currentStatus =
            ElecContextUtil.getElecStatus(calendarId, studentId);
        // 如果当前是Init状态，进行初始化
        if (ElecStatus.Init.equals(currentStatus))
        {
            // 加入队列
            currentStatus = ElecStatus.Loading;
            ElecContextUtil.setElecStatus(calendarId, studentId, currentStatus);
            if (!queueService.add(QueueGroups.STUDENT_LOADING, elecRequest))
            {
                currentStatus = ElecStatus.Init;
                ElecContextUtil
                    .setElecStatus(calendarId, studentId, currentStatus);
                return RestResult.fail("请稍后再试");
            }
        }
        ElecRespose response = this.getElectResult(elecRequest);
        
        return RestResult.successData(response);
    }
    
    @Override
    public RestResult<ElecRespose> elect(ElecRequest elecRequest)
    {
        if (CollectionUtil.isEmpty(elecRequest.getElecClassList())
            && CollectionUtil.isEmpty(elecRequest.getWithdrawClassList()))
        {
            return RestResult.fail("你没选择任何课程");
        }
        Long roundId = elecRequest.getRoundId();
        String studentId = elecRequest.getStudentId();
        String projectId = elecRequest.getProjectId();
        Integer chooseObj = elecRequest.getChooseObj();
        Long calendarId = null;
        
        // 研究生管理员代理选课
        if (!Constants.PROJ_UNGRADUATE.equals(projectId)
            && Objects.equals(ChooseObj.ADMIN.type(), chooseObj))
        {
            calendarId = elecRequest.getCalendarId();
            elecRequest.setCalendarId(calendarId);
        }
        else
        {
            ElectionRounds round = dataProvider.getRound(roundId);
            calendarId = round.getCalendarId();
            elecRequest.setCalendarId(calendarId);
        }
        
        ElecStatus currentStatus =
            ElecContextUtil.getElecStatus(calendarId, studentId);
        if (ElecStatus.Ready.equals(currentStatus)
            && ElecContextUtil.tryLock(calendarId, studentId))
        {
            try
            {
                // 加入选课队列
                if (queueService.add(QueueGroups.STUDENT_ELEC, elecRequest))
                {
                    ElecContextUtil.setElecStatus(calendarId,
                        studentId,
                        ElecStatus.Processing);
                    currentStatus = ElecStatus.Processing;
                }
                else
                {
                    return RestResult.fail("请稍后再试");
                }
            }
            finally
            {
                ElecContextUtil.unlock(calendarId, studentId);
            }
        }
        return RestResult.successData(new ElecRespose(currentStatus));
    }
    
    @Override
    public ElecRespose getElectResult(ElecRequest elecRequest)
    {
        Long roundId = elecRequest.getRoundId();
        String studentId = elecRequest.getStudentId();
        Long calendarId = elecRequest.getCalendarId();
        
        // 研究生
        if (null == roundId && null != calendarId)
        {
        }
        else
        {
            ElectionRounds round = dataProvider.getRound(roundId);
            if (round == null)
            {
                return new ElecRespose();
            }
            calendarId = round.getCalendarId();
        }
        
        ElecRespose response =
            ElecContextUtil.getElecRespose(studentId, calendarId);
        ElecStatus status =
            ElecContextUtil.getElecStatus(calendarId, studentId);
        if (response == null)
        {
            response = new ElecRespose(status);
        }
        else
        {
            response.setStatus(status);
        }
        return response;
    }
    
    /**根据轮次查询学生信息*/
    @Override
    public Student findStuRound(Long roundId, String studentId)
    {
        Student stu = stuDao.findStuRound(roundId, studentId);
        return stu;
    }
    
}
