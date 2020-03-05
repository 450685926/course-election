package com.server.edu.mutual.studentelec.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.IElecContext;
import com.server.edu.election.studentelec.utils.ElecContextUtil;
import com.server.edu.election.studentelec.utils.ElecStatus;
import com.server.edu.mutual.studentelec.context.ElecContextMutualBk;
import com.server.edu.mutual.studentelec.perload.MutualDataProLoad;
import com.server.edu.mutual.studentelec.service.AbstractMutualElecQueueComsumerService;
import com.server.edu.mutual.studentelec.service.ElecMutualQueueService;
import com.server.edu.mutual.studentelec.service.MutualStudentElecPreloadingService;
import com.server.edu.mutual.studentelec.utils.MutualQueueGroups;

/**
 * 本研互选学生选课登录时的准备工作
 * @author luoxiaoli 2019/11/27 19:29
 */
@Service
public class MutualStudentElecPreloadingServiceImpl
    extends AbstractMutualElecQueueComsumerService<ElecRequest>
    implements MutualStudentElecPreloadingService
{
    private static final Logger LOG =
        LoggerFactory.getLogger(MutualStudentElecPreloadingServiceImpl.class);
    
    @Autowired
    private ApplicationContext applicationContext;
    
    public MutualStudentElecPreloadingServiceImpl(
    	ElecMutualQueueService<ElecRequest> elecQueueService)
    {
        super(MutualQueueGroups.MUTUAL_STUDENT_LOADING, elecQueueService);
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void consume(ElecRequest preloadRequest)
    {
        Long roundId = preloadRequest.getRoundId();
        String studentId = preloadRequest.getStudentId();
        Long calendarId = preloadRequest.getCalendarId();
        String projectId = preloadRequest.getProjectId();
        try
        {
            if (ElecContextUtil.tryLock(calendarId, studentId))
            {
                IElecContext context = null;
                try
                {
                	context = new ElecContextMutualBk(studentId, calendarId);

//                	if (Constants.PROJ_UNGRADUATE.equals(projectId))
//                    {
//                        context = new ElecContextMutualBk(studentId, calendarId);
//                    }
//                    else
//                    {
//                        context = new ElecContext(studentId, calendarId);
//                    }
                	
                    context.setRequest(preloadRequest);
                    
                    Map<String, MutualDataProLoad> beansOfType =
                        applicationContext.getBeansOfType(MutualDataProLoad.class);
                    
                    // String projId = projectId;
                    // 获取轮次对应管理部门的DataProLoad
                    List<MutualDataProLoad> values =
                        beansOfType.values().stream().filter(load -> {
                            return load.getProjectIds().contains(projectId);
                        }).collect(Collectors.toList());
                    
                    //排序
                    Collections.sort(values);
                    
                    context.clear();
                    for (MutualDataProLoad load : values)
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

