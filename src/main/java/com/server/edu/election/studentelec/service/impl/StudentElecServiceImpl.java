package com.server.edu.election.studentelec.service.impl;

import java.util.Date;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.constants.ChooseObj;
import com.server.edu.election.constants.Constants;
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
import com.server.edu.election.service.ElectionApplyService;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.SelectedCourse;
import com.server.edu.election.studentelec.rules.bk.LimitCountCheckerRule;
import com.server.edu.election.studentelec.service.ElecQueueService;
import com.server.edu.election.studentelec.service.StudentElecService;
import com.server.edu.election.studentelec.service.cache.AbstractCacheService;
import com.server.edu.election.studentelec.utils.ElecContextUtil;
import com.server.edu.election.studentelec.utils.ElecStatus;
import com.server.edu.election.studentelec.utils.QueueGroups;
import com.server.edu.election.vo.ElcLogVo;
import com.server.edu.util.CollectionUtil;

@Service
public class StudentElecServiceImpl extends AbstractCacheService
    implements StudentElecService
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
    
    @Autowired
    private ElectionApplyService electionApplyService;
    
    @Override
    public RestResult<ElecRespose> loading(ElecRequest elecRequest)
    {
        Long roundId = elecRequest.getRoundId();
        Integer chooseObj = elecRequest.getChooseObj();
        String studentId = elecRequest.getStudentId();
        String projectId = elecRequest.getProjectId();
        Long calendarId = null;
        
        // 研究生
        if (!Constants.PROJ_UNGRADUATE.equals(projectId)
            && Objects.equals(ChooseObj.ADMIN.type(), chooseObj))
        {
            calendarId = elecRequest.getCalendarId();
        }
        else
        {
            ElectionRounds round = dataProvider.getRound(roundId);
            calendarId = round.getCalendarId();
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
        
        // 研究生
        if (!Constants.PROJ_UNGRADUATE.equals(projectId)
            && Objects.equals(ChooseObj.ADMIN.type(), chooseObj))
        {
            calendarId = elecRequest.getCalendarId();
        }
        else
        {
            ElectionRounds round = dataProvider.getRound(roundId);
            calendarId = round.getCalendarId();
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
    
    @Transactional
    @Override
    public void saveElc(ElecContext context, TeachingClassCache teachClass,
        ElectRuleType type)
    {
        StudentInfoCache stu = context.getStudentInfo();
        ElecRequest request = context.getRequest();
        ElecRespose respose = context.getRespose();
        Date date = new Date();
        String studentId = stu.getStudentId();
        
        Long roundId = request.getRoundId();
        ElectionRounds round = dataProvider.getRound(roundId);
        Long teachClassId = teachClass.getTeachClassId();
        String TeachClassCode = teachClass.getTeachClassCode();
        String courseCode = teachClass.getCourseCode();
        String courseName = teachClass.getCourseName();
        
        Integer logType = ElcLogVo.TYPE_1;
        
        Integer courseTakeType =
            Constants.REBUILD_CALSS.equals(teachClass.getTeachClassType())
                ? CourseTakeType.RETAKE.type()
                : CourseTakeType.NORMAL.type();
        
        if (ElectRuleType.ELECTION.equals(type))
        {
            if (dataProvider.containsRule(roundId,
                LimitCountCheckerRule.class.getSimpleName()))
            {
                LOG.info("---- LimitCountCheckerRule ----");
                // 增加选课人数
                int count = classDao.increElcNumberAtomic(teachClassId);
                if (count == 0)
                {
                    respose.getFailedReasons()
                        .put(teachClassId.toString(),
                            I18nUtil.getMsg("ruleCheck.limitCount"));
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
            take.setCourseTakeType(courseTakeType);
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
            if (round.getTurn() != Constants.THIRD_TURN
                && round.getTurn() != Constants.FOURTH_TURN)
            {
                int count = classDao.decrElcNumber(teachClassId);
                if (count > 0)
                {
                    dataProvider.decrElcNumber(teachClassId);
                }
            }
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
        
        if (ElectRuleType.ELECTION.equals(type))
        {
            if (stu.getManagerDeptId().equals(Constants.PROJ_GRADUATE))
            {
                //更新选课申请数据
                electionApplyService
                    .update(studentId, round.getCalendarId(), courseCode);
            }
            // 更新缓存
            dataProvider.incrementElecNumber(teachClassId);
            
            respose.getSuccessCourses().add(teachClassId);
            SelectedCourse course = new SelectedCourse(teachClass);
            course.setTeachClassId(teachClassId);
            course.setTurn(round.getTurn());
            course.setCourseTakeType(courseTakeType);
            course.setChooseObj(request.getChooseObj());
            context.getSelectedCourses().add(course);
        }
    }
    
    /**根据轮次查询学生信息*/
    @Override
    public Student findStuRound(Long roundId, String studentId)
    {
        Student stu = stuDao.findStuRound(roundId, studentId);
        return stu;
    }
    
}
