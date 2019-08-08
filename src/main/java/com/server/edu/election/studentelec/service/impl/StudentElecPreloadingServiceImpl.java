package com.server.edu.election.studentelec.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.google.common.base.Objects;
import com.server.edu.election.constants.ChooseObj;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.IElecContext;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.preload.DataProLoad;
import com.server.edu.election.studentelec.service.AbstractElecQueueComsumerService;
import com.server.edu.election.studentelec.service.ElecQueueService;
import com.server.edu.election.studentelec.service.StudentElecPreloadingService;
import com.server.edu.election.studentelec.utils.ElecContextUtil;
import com.server.edu.election.studentelec.utils.ElecStatus;
import com.server.edu.election.studentelec.utils.QueueGroups;

/**
 * 学生选课登录时的准备工作
 * @author liuzheng 2019/2/13 19:29
 */
@Service
public class StudentElecPreloadingServiceImpl
    extends AbstractElecQueueComsumerService<ElecRequest>
    implements StudentElecPreloadingService
{
    private static final Logger LOG =
        LoggerFactory.getLogger(StudentElecPreloadingService.class);
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private RoundDataProvider dataProvider;
    
    public StudentElecPreloadingServiceImpl(
        ElecQueueService<ElecRequest> elecQueueService)
    {
        super(QueueGroups.STUDENT_LOADING, elecQueueService);
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void consume(ElecRequest preloadRequest)
    {
        Long roundId = preloadRequest.getRoundId();
        String studentId = preloadRequest.getStudentId();
        Long calendarId = preloadRequest.getCalendarId();
        String projectId = preloadRequest.getProjectId();
        Integer chooseObj = preloadRequest.getChooseObj();
        try
        {
            // 研究生的管理员代选是没有轮次和规则的
            if (StringUtils.isNotBlank(projectId)
                && !Constants.PROJ_UNGRADUATE.equals(projectId)
                && Objects.equal(ChooseObj.ADMIN.type(), chooseObj))
            {
            }
            else
            {
                ElectionRounds round = dataProvider.getRound(roundId);
                calendarId = round.getCalendarId();
                projectId = round.getProjectId();
            }
            if (ElecContextUtil.tryLock(calendarId, studentId))
            {
                IElecContext context = null;
                try
                {
                    if (Constants.PROJ_UNGRADUATE.equals(projectId))
                    {
                        context = new ElecContextBk(studentId, calendarId);
                    }
                    else
                    {
                        context = new ElecContext(studentId, calendarId);
                    }
                    context.setRequest(preloadRequest);
                    
                    Map<String, DataProLoad> beansOfType =
                        applicationContext.getBeansOfType(DataProLoad.class);
                    
                    String projId = projectId;
                    // 获取轮次对应管理部门的DataProLoad
                    List<DataProLoad> values =
                        beansOfType.values().stream().filter(load -> {
                            return load.getProjectIds().contains(projId);
                        }).collect(Collectors.toList());
                    
                    //排序
                    Collections.sort(values);
                    
                    context.clear();
                    for (DataProLoad load : values)
                    {
                        load.load(context);
                    }
                    //保存数据
                    context.saveToCache();
                    
                    //完成后设置当前状态为Ready
                    ElecContextUtil
                        .setElecStatus(calendarId, studentId, ElecStatus.Ready);
                    context.getRespose().getFailedReasons().clear();
                    context.saveResponse();
                }
                catch (Exception e)
                {
                    if (null != context)
                    {
                        context.getRespose()
                            .getFailedReasons()
                            .put("loadFail", e.getMessage());
                        context.saveResponse();
                    }
                    
                    LOG.error(e.getMessage(), e);
                    ElecContextUtil
                        .setElecStatus(calendarId, studentId, ElecStatus.Init);
                }
                finally
                {
                    ElecContextUtil.unlock(calendarId, studentId);
                }
            }
            else
            {
                // 该方法的值均从队列中获取，此时只要接收请求的方法不出问题，应该不会出现其他状态
                // 什么场景会出现预加载情况下并发修改了该值？？
                LOG.error(
                    "预加载状态异常 unexpected status,projectId:{},calendarId:{},roundId:{},studentId:{}, status:{}",
                    projectId,
                    calendarId,
                    roundId,
                    studentId,
                    ElecContextUtil.getElecStatus(calendarId, studentId));
            }
        }
        catch (Exception e)
        {
            LOG.error(e.getMessage(), e);
        }
    }
    
}
