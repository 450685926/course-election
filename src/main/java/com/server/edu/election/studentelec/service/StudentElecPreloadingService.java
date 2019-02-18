package com.server.edu.election.studentelec.service;


import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.service.ElecQueueComsumerService;


/**
 * 学生选课登录时的准备工作
 * @author liuzheng 2019/2/13 19:29
 */
public interface StudentElecPreloadingService extends ElecQueueComsumerService<ElecRequest> {

}
