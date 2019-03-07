package com.server.edu.election.studentelec.service.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.server.edu.common.rest.RestResult;
import com.server.edu.election.constants.ChooseObj;
import com.server.edu.election.constants.CourseTakeType;
import com.server.edu.election.constants.ElectRuleType;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.dao.ElcLogDao;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.dao.TeachingClassDao;
import com.server.edu.election.entity.ElcCourseTake;
import com.server.edu.election.entity.ElcLog;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.entity.Student;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.service.ElecQueueService;
import com.server.edu.election.studentelec.service.StudentElecService;
import com.server.edu.election.studentelec.utils.ElecContextUtil;
import com.server.edu.election.studentelec.utils.ElecStatus;
import com.server.edu.election.studentelec.utils.QueueGroups;
import com.server.edu.election.vo.ElcLogVo;
import com.server.edu.election.vo.ElectionRuleVo;
import com.server.edu.util.CollectionUtil;

@Service
public class StudentElecServiceImpl implements StudentElecService
{
    Logger LOG = LoggerFactory.getLogger(getClass());
    
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
    
    @Autowired
    private StudentDao stuDao;
    
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
        ElecRespose response = this.getElectResult(roundId, studentId);
        
        return RestResult.successData(response);
    }
    
    @Override
    public RestResult<ElecRespose> elect(ElecRequest elecRequest)
    {
        if (CollectionUtil.isEmpty(elecRequest.getElecTeachingClasses())
            && CollectionUtil.isEmpty(elecRequest.getWithdrawTeachClasss()))
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
    public void saveElc(ElecContext context, TeachingClassCache courseClass,
        ElectRuleType type)
    {
        StudentInfoCache stu = context.getStudentInfo();
        ElecRequest request = context.getRequest();
        ElecRespose respose = context.getRespose();
        Date date = new Date();
        String studentId = stu.getStudentId();
        
        Long roundId = context.getRoundId();
        ElectionRounds round = dataProvider.getRound(roundId);
        Long teachClassId = courseClass.getTeachClassId();
        String TeachClassCode = courseClass.getTeachClassCode();
        String courseCode = courseClass.getCourseCode();
        String courseName = courseClass.getCourseName();
        
        Integer logType = ElcLogVo.TYPE_1;
        if (ElectRuleType.ELECTION.equals(type))
        {
            ElectionRuleVo rule =
                dataProvider.getRule(roundId, "LimitCountCheckerRule");
            if (rule != null)
            {
                LOG.info("---- LimitCountCheckerRule ----");
                // 增加选课人数
                int count = classDao.increElcNumberAtomic(teachClassId);
                if (count == 0)
                {
                    respose.getFailedReasons()
                        .put(teachClassId.toString(), "教学班人数已满");
                    return;
                }
            }
            else
            {
                classDao.increElcNumber(teachClassId);
            }
            
            ElcCourseTake take = new ElcCourseTake();
            take.setCalendarId(round.getCalendarId());
            take.setChooseObj(request.getChooseObj());
            take.setCourseCode(courseCode);
            take.setCourseTakeType(CourseTakeType.NORMAL.type());
            take.setCreatedAt(date);
            take.setStudentId(studentId);
            take.setTeachingClassId(teachClassId);
            take.setMode(round.getMode());
            take.setTurn(round.getTurn());
            courseTakeDao.insertSelective(take);
        }
        else
        {
            logType = ElcLogVo.TYPE_2;
            ElcCourseTake take = new ElcCourseTake();
            take.setCalendarId(round.getCalendarId());
            take.setCourseCode(courseCode);
            take.setStudentId(studentId);
            take.setTeachingClassId(teachClassId);
            courseTakeDao.delete(take);
            classDao.decrElcNumber(teachClassId);
        }
        
        // 添加选课日志
        ElcLog log = new ElcLog();
        log.setCalendarId(round.getCalendarId());
        log.setCourseCode(courseCode);
        log.setCourseName(courseName);
        log.setCreateBy(request.getCreateBy());
        log.setCreatedAt(date);
        log.setCreateIp(request.getRequestIp());
        log.setMode(
            ChooseObj.STU.type() == request.getChooseObj() ? ElcLogVo.MODE_1
                : ElcLogVo.MODE_2);
        log.setStudentId(studentId);
        log.setTeachingClassCode(TeachClassCode);
        log.setTurn(round.getTurn());
        log.setType(logType);
        this.elcLogDao.insertSelective(log);
    }
    
    /**根据轮次查询学生信息*/
    @Override
    public Student findStuRound(Long roundId, String studentId)
    {
        Student stu = stuDao.findStuRound(roundId, studentId);
        return stu;
    }
    
}
