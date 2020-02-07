package com.server.edu.election;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import com.server.edu.common.PageCondition;
import com.server.edu.election.dao.TeachingClassDao;
import com.server.edu.election.dto.CourseDto;
import com.server.edu.election.dto.ElcStudentLimitDto;
import com.server.edu.election.service.ElcStudentLimitService;
import com.server.edu.election.service.impl.ElectionApplyCoursesServiceImpl;

@ActiveProfiles("dev")
public class ElectionApplyCoursesTest extends ApplicationTest
{
    @Autowired
    ElectionApplyCoursesServiceImpl applyCoursesServiceImpl;
    
    @Autowired
    ElcStudentLimitService limitService;
    
    @Autowired
    private TeachingClassDao classDao;
    
    private static ExecutorService starter = Executors.newFixedThreadPool(10);
    
//    @Test
//    public void test() {
//        PageCondition<CourseDto> condition = new PageCondition<>();
//        CourseDto condition1 = new CourseDto();
//        condition1.setMode(3);
//        condition1.setKeyword("32000");
//        condition.setCondition(condition1 );
//        applyCoursesServiceImpl.courseList(condition );
//    }
    
//    @Test
//    public void test1() {
//        PageCondition<ElcStudentLimitDto> condition = new PageCondition<>();
//        condition.setPageNum_(1);
//        condition.setPageSize_(10);
//        ElcStudentLimitDto cnd = new ElcStudentLimitDto();
//        cnd.setProjectId("1");
//        cnd.setCalendarId(108L);
//        cnd.setMinLimitCredits(0);
//        cnd.setMaxLimitCredits(10);
//        cnd.setMinRebuild(0);
//        cnd.setMaxRebuild(10);
//        cnd.setMinSelectedCredits(0);
//        cnd.setMaxSelectedCredits(10);
//        cnd.setMinSelectedRebuild(10);
//        cnd.setMaxSelectedRebuild(10);
//        condition.setCondition(cnd);
//        limitService.getLimitStudents(condition );
//    }
    
    @Test
    public void test2() {
    	for(int i=0;i<1000;i++) {
            // 启动
            starter.execute(() -> {
                Thread.currentThread()
                    .setName("elecConsume-" + Thread.currentThread().getName());
                while (true)
                {
                    try
                    {
                    	int count = classDao.increElcNumberAtomic(111111112477628L);
                    	System.out.println(count+ Thread.currentThread().getName());
                    }
                    catch (Exception e)
                    {
                        // 异常停45秒后再试
                        try
                        {
                            TimeUnit.SECONDS.sleep(45);
                        }
                        catch (InterruptedException e1)
                        {
                        }
                    }
                }
            });
    	}
    }
    
    
}
