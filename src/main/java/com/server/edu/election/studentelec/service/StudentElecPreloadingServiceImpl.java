package com.server.edu.election.studentelec.service;


import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.utils.ElecStatus;
import com.server.edu.election.studentelec.utils.QueueGroups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 学生选课登录时的准备工作
 * @author liuzheng 2019/2/13 19:29
 */
@Service
public class StudentElecPreloadingServiceImpl extends AbstractElecQueueComsumerService<ElecRequest>
        implements StudentElecPreloadingService {
    private static final Logger LOG = LoggerFactory.getLogger(StudentElecPreloadingService.class);
    private final StudentElecStatusService elecStatusService;


    @Autowired
    public StudentElecPreloadingServiceImpl(ElecQueueService elecQueueService, StudentElecStatusService elecStatusService) {
        // TODO 几个线 程应该是可配置的
        super(4, QueueGroups.STUDENT_LOADING, elecQueueService);
        this.elecStatusService = elecStatusService;
        super.listen("thread-student-preload");
    }

    @Override
    public void consume(ElecRequest preloadRequest) {
        Integer roundId = preloadRequest.getRoundId();
        String studentId = preloadRequest.getStudentId();
        try {
            if (elecStatusService.tryLock(roundId, studentId)) {
                // TODO 缓存学生数据
                // ....
                System.out.println("假装加载数据");
                Thread.sleep(2000);

                //完成后设置当前状态为Ready
                elecStatusService.setElecStatus(roundId, studentId ,ElecStatus.Ready);
            }else{
                // 该方法的值均从队列中获取，此时只要接收请求的方法不出问题，应该不会出现其他状态
                // 什么场景会出现预加载情况下并发修改了该值？？
                LOG.error("预加载状态异常 unexpected status,roundId:{},studentId:{}, status:{}", roundId, studentId,
                        elecStatusService.getElecStatus(roundId, studentId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            super.endConsume();
        }
    }
}
