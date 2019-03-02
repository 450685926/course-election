package com.server.edu.election.studentelec.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRequest;
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
    
    public StudentElecPreloadingServiceImpl(
        ElecQueueService<ElecRequest> elecQueueService)
    {
        // 使用4个线程来执行初始化数据操作
        super(4, QueueGroups.STUDENT_LOADING, elecQueueService);
    }
    
    @Override
    public void consume(ElecRequest preloadRequest)
    {
        Long roundId = preloadRequest.getRoundId();
        String studentId = preloadRequest.getStudentId();
        try
        {
            if (ElecContextUtil.tryLock(roundId, studentId))
            {
                ElecContext context =
                    new ElecContext(studentId, roundId.longValue());
                try
                {
                    Map<String, DataProLoad> beansOfType =
                        applicationContext.getBeansOfType(DataProLoad.class);
                    
                    List<DataProLoad> values =
                        new ArrayList<>(beansOfType.values());
                    //排序
                    Collections.sort(values);
                    
                    for (DataProLoad load : values)
                    {
                        load.load(context);
                    }
                    //保存数据
                    context.saveToCache();
                    
                    //完成后设置当前状态为Ready
                    ElecContextUtil
                        .setElecStatus(roundId, studentId, ElecStatus.Ready);
                    context.getRespose().getFailedReasons().clear();
                    context.saveResponse();
                }
                catch (Exception e)
                {
                    context.getRespose().getFailedReasons().put("loadFail", e.getMessage());
                    context.saveResponse();
                    
                    LOG.error(e.getMessage(), e);
                    ElecContextUtil
                        .setElecStatus(roundId, studentId, ElecStatus.Init);
                }
                finally
                {
                    ElecContextUtil.unlock(roundId, studentId);
                }
            }
            else
            {
                // 该方法的值均从队列中获取，此时只要接收请求的方法不出问题，应该不会出现其他状态
                // 什么场景会出现预加载情况下并发修改了该值？？
                LOG.error(
                    "预加载状态异常 unexpected status,roundId:{},studentId:{}, status:{}",
                    roundId,
                    studentId,
                    ElecContextUtil.getElecStatus(roundId, studentId));
            }
        }
        catch (Exception e)
        {
            LOG.error(e.getMessage(), e);
        }
    }
}
