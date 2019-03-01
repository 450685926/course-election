package com.server.edu.election.studentelec.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.server.edu.common.rest.RestResult;
import com.server.edu.election.constants.ChooseObj;
import com.server.edu.election.constants.CourseTakeType;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.dao.ElcLogDao;
import com.server.edu.election.dao.TeachingClassDao;
import com.server.edu.election.entity.ElcCourseTake;
import com.server.edu.election.entity.ElcLog;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecCourseClass;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.service.ElecQueueService;
import com.server.edu.election.studentelec.service.StudentElecService;
import com.server.edu.election.studentelec.utils.ElecContextUtil;
import com.server.edu.election.studentelec.utils.ElecStatus;
import com.server.edu.election.studentelec.utils.QueueGroups;
import com.server.edu.election.vo.ElcLogVo;
import com.server.edu.election.vo.ElectionRuleVo;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;

@Service
public class StudentElecServiceImpl implements StudentElecService
{
    @Autowired
    private ElecQueueService<ElecRequest> queueService;
    
    @Autowired
    private ElcCourseTakeDao courseTakeDao;
    
    @Autowired
    private TeachingClassDao classDao;
    
    @Autowired
    private ElcLogDao elcLogDao;
    
    @Autowired
    private RoundDataProvider dataProvider;
    
    @Override
    public RestResult<ElecRespose> loading(Long roundId, String studentId)
    {
        ElecStatus currentStatus =
            ElecContextUtil.getElecStatus(roundId, studentId);
        // 如果当前是Init状态，进行初始化
        if (ElecStatus.Init.equals(currentStatus))
        {
            // 加入队列
            currentStatus = ElecStatus.Loading;
            ElecContextUtil.setElecStatus(roundId, studentId, currentStatus);
            if (!addLoadingToQueue(roundId, studentId))
            {
                currentStatus = ElecStatus.Init;
                ElecContextUtil
                    .setElecStatus(roundId, studentId, currentStatus);
                return RestResult.fail("请稍后再试");
            }
        }
        return RestResult.successData(new ElecRespose(currentStatus));
    }
    
    @Override
    public RestResult<ElecRespose> elect(ElecRequest elecRequest)
    {
        if (elecRequest.getElecTeachingClasses().size() == 0)
        {
            return RestResult.fail("你没选择任何课程");
        }
        Long roundId = elecRequest.getRoundId();
        String studentId = elecRequest.getStudentId();
        
        ElecStatus currentStatus =
            ElecContextUtil.getElecStatus(roundId, studentId);
        if (ElecStatus.Ready.equals(currentStatus)
            && ElecContextUtil.tryLock(roundId, studentId))
        {
            try
            {
                // 加锁后二次检查
                currentStatus =
                    ElecContextUtil.getElecStatus(roundId, studentId);
                if (ElecStatus.Ready.equals(currentStatus))
                {
                    // 加入选课队列
                    if (addElecToQueue(roundId, studentId, elecRequest))
                    {
                        ElecContextUtil.setElecStatus(roundId,
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
                ElecContextUtil.unlock(roundId, studentId);
            }
        }
        return RestResult.successData(new ElecRespose(currentStatus));
    }
    
    @Override
    public ElecRespose getElectResult(Long roundId, String studentId)
    {
        ElecContextUtil contextUtil =
            ElecContextUtil.create(roundId, studentId);
        
        ElecRespose response = contextUtil.getElecRespose();
        ElecStatus status = ElecContextUtil.getElecStatus(roundId, studentId);
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
    
    private boolean addLoadingToQueue(Long roundId, String studentId)
    {
        ElecRequest loadingRequest = new ElecRequest();
        loadingRequest.setRoundId(roundId);
        loadingRequest.setStudentId(studentId);
        return queueService.add(QueueGroups.STUDENT_LOADING, loadingRequest);
    }
    
    private boolean addElecToQueue(Long roundId, String studentId,
        ElecRequest elecRequest)
    {
        elecRequest.setRoundId(roundId);
        elecRequest.setStudentId(studentId);
        return queueService.add(QueueGroups.STUDENT_ELEC, elecRequest);
    }
    
    @Transactional
    @Override
    public void saveElc(ElecContext context, ElecCourseClass courseClass)
    {
        StudentInfoCache stu = context.getStudentInfo();
        ElecRequest request = context.getRequest();
        ElecRespose respose = context.getRespose();
        Date date = new Date();
        String studentId = stu.getStudentId();
        
        Long roundId = context.getRoundId();
        ElectionRounds round = dataProvider.getRound(roundId);
        ElectionRuleVo rule =
            dataProvider.getRule(roundId, "LimitCountCheckerRule");
        Long teacherClassId = courseClass.getTeacherClassId();
        if (rule != null)
        {
            // 增加选课人数
            int count = classDao.increElcNumberAtomic(teacherClassId);
            if (count == 0)
            {
                respose.getFailedCourses().add(teacherClassId);
                //respose.getFailedReasons().put(key, "人数已满") TODO
                return;
            }
        }
        else
        {
            classDao.increElcNumber(teacherClassId);
        }
        String teacherClassCode = courseClass.getTeacherClassCode();
        String courseCode = courseClass.getCourseCode();
        String courseName = courseClass.getCourseName();
        
        ElcCourseTake take = new ElcCourseTake();
        take.setCalendarId(round.getCalendarId());
        take.setChooseObj(request.getChooseObj());
        take.setCourseCode(courseCode);
        take.setCourseTakeType(CourseTakeType.NORMAL.type());
        take.setCreatedAt(date);
        take.setStudentId(studentId);
        take.setTeachingClassId(teacherClassId);
        take.setMode(round.getMode());
        take.setTurn(round.getTurn());
        courseTakeDao.insertSelective(take);
        // 添加选课日志
        ElcLog log = new ElcLog();
        log.setCalendarId(round.getCalendarId());
        log.setCourseCode(courseCode);
        log.setCourseName(courseName);
        Session currentSession = SessionUtils.getCurrentSession();
        log.setCreateBy(currentSession.getUid());
        log.setCreatedAt(date);
        log.setCreateIp(currentSession.getIp());
        log.setMode(
            ChooseObj.STU.type() == request.getChooseObj() ? ElcLogVo.MODE_1
                : ElcLogVo.MODE_2);
        log.setStudentId(studentId);
        log.setTeachingClassCode(teacherClassCode);
        log.setTurn(round.getTurn());
        log.setType(ElcLogVo.TYPE_1);
        this.elcLogDao.insertSelective(log);
    }
    
}
