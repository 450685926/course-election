package com.server.edu.election.studentelec.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.antlr.v4.parse.BlockSetTransformer.setAlt_return;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.server.edu.election.constants.Constants;
import com.server.edu.election.entity.ElectionRounds;
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
public class AdminAgentElecPreloadingServiceImpl
    extends AbstractElecQueueComsumerService<ElecRequest>
    implements StudentElecPreloadingService
{
    private static final Logger LOG =
        LoggerFactory.getLogger(StudentElecPreloadingService.class);
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private RoundDataProvider dataProvider;
    
    public AdminAgentElecPreloadingServiceImpl(
        ElecQueueService<ElecRequest> elecQueueService)
    {
        super(QueueGroups.ADMIN_LOADING, elecQueueService);
    }
    
    @Override
    public void consume(ElecRequest preloadRequest)
    {
    	Long calendarId = preloadRequest.getCalendarId();
    	String studentId = preloadRequest.getStudentId();
    	try
    	{
    		if (ElecContextUtil.tryLockAdmin(calendarId, studentId))
    		{
    			ElecContext context = null;
    			try
    			{
    				context = new ElecContext(studentId, calendarId);
    				context.setRequest(preloadRequest);
    				String projectId = Constants.PROJ_GRADUATE;
    				
    				Map<String, DataProLoad> beansOfType =
    						applicationContext.getBeansOfType(DataProLoad.class);
    				
    				// 获取轮次对应管理部门的DataProLoad
    				List<DataProLoad> values =
    						beansOfType.values().stream().filter(load -> {
    							return load.getProjectIds().contains(projectId);
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
    				.setElecAdminStatus(calendarId, studentId, ElecStatus.Ready);
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
    				.setElecAdminStatus(calendarId, studentId, ElecStatus.Init);
    			}
    			finally
    			{
    				ElecContextUtil.unlockAdmin(calendarId, studentId);
    			}
    		}
    		else
    		{
    			// 该方法的值均从队列中获取，此时只要接收请求的方法不出问题，应该不会出现其他状态
    			// 什么场景会出现预加载情况下并发修改了该值？？
    			LOG.error(
    					"预加载状态异常 unexpected status,calendarId:{},studentId:{}, status:{}",
    					calendarId,
    					studentId,
    					ElecContextUtil.getElecAdminStatus(calendarId, studentId));
    		}
    	}
    	catch (Exception e)
    	{
    		LOG.error(e.getMessage(), e);
    	}
    }
    
}
