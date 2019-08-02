package com.server.edu.election.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.validation.constraints.NotNull;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSON;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.studentelec.service.impl.RoundDataProvider;
import com.server.edu.election.vo.ElectionRoundsVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;

import io.swagger.annotations.ApiOperation;

@RestSchema(schemaId = "TestController")
@RequestMapping("testElc")
public class TestController
{
    Logger LOG = LoggerFactory.getLogger(TestController.class);
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Autowired
    private RoundDataProvider dataProvider;
    
    @ApiOperation(value = "获取生效的轮次")
    @PostMapping("/getRounds")
    public RestResult<List<ElectionRoundsVo>> getRounds(
        @RequestParam("electionObj") @NotBlank String electionObj,
        @RequestParam("projectId") @NotBlank String projectId,
        @RequestParam(name = "mode") @NotNull Integer mode,
        @RequestParam(name = "studentId") String studentId)
    {
        long start = System.currentTimeMillis();
        List<ElectionRoundsVo> data = new ArrayList<>();
        
        List<ElectionRounds> allRound = dataProvider.getAllRound();
        
        long end = System.currentTimeMillis();
        long seconds = TimeUnit.MILLISECONDS.toSeconds(end - start);
        if (seconds > 1)
        {
            LOG.error("----- getRounds request time[{}] > 1 -------", seconds);
        }
        return RestResult.successData(data);
    }
    
    @PostMapping("/getRound2")
    public RestResult<List<ElectionRoundsVo>> getRound2(
        @RequestParam("electionObj") @NotBlank String electionObj,
        @RequestParam("projectId") @NotBlank String projectId,
        @RequestParam(name = "mode") @NotNull Integer mode,
        @RequestParam(name = "studentId") String studentId)
    {
        
        long start = System.currentTimeMillis();
        
        String string = redisTemplate.opsForValue().get("testElc_test1");
        
        long end = System.currentTimeMillis();
        long seconds = TimeUnit.MILLISECONDS.toSeconds(end - start);
        if (seconds > 1)
        {
            LOG.error("----- getRound2 request time[{}] > 1 -------", seconds);
        }
        List<ElectionRoundsVo> data = new ArrayList<>();
        return RestResult.successData(data);
    }
    
    @PostMapping("/getRound3")
    public RestResult<List<ElectionRounds>> getRound3()
    {
        
        long start = System.currentTimeMillis();
        
        String text = redisTemplate.opsForValue().get("testElc_test1");
        
        List<ElectionRounds> data = JSON.parseArray(text, ElectionRounds.class);
        
        long end = System.currentTimeMillis();
        long seconds = TimeUnit.MILLISECONDS.toSeconds(end - start);
        if (seconds > 1)
        {
            LOG.error("----- getRound3 request time[{}] > 1 -------", seconds);
        }
        
        return RestResult.successData(data);
    }
    
    @GetMapping("/test1")
    public RestResult<?> test1()
    {
        List<ElectionRounds> allRound = dataProvider.getAllRound();
        redisTemplate.opsForValue()
            .set("testElc_test1",
                JSON.toJSONString(allRound),
                1,
                TimeUnit.HOURS);
        return RestResult.success();
    }
    
    static ExecutorService executorService = Executors.newFixedThreadPool(100);
    
    @GetMapping("/test2")
    public RestResult<?> test2()
    {
        this.test1();
        
        long start = System.currentTimeMillis();
        CountDownLatch downLatch = new CountDownLatch(100);
        for (int i = 1; i <= 100; i++)
        {
            executorService.submit(() -> {
                try
                {
                    for (int j = 0; j < 1000; j++)
                    {
                        getRound3();
                    }
                }
                finally
                {
                    downLatch.countDown();
                }
            });
        }
        try
        {
            downLatch.await();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        long seconds = TimeUnit.MILLISECONDS.toSeconds(end - start);
        LOG.error("----- test2 request time[{}] -------", seconds);
        return RestResult.successData(seconds);
    }
    
    @GetMapping("/test3")
    public RestResult<?> test3()
    {
        this.test1();
        
        long start = System.currentTimeMillis();
        CountDownLatch downLatch = new CountDownLatch(100);
        for (int i = 1; i <= 100; i++)
        {
            executorService.submit(() -> {
                try
                {
                    for (int j = 0; j < 1000; j++)
                    {
                        redisTemplate.opsForValue().get("testElc_test1");
                    }
                }
                finally
                {
                    downLatch.countDown();
                }
            });
        }
        try
        {
            downLatch.await();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        long seconds = TimeUnit.MILLISECONDS.toSeconds(end - start);
        LOG.error("----- test3 request time[{}] -------", seconds);
        return RestResult.successData(seconds);
    }
    
    @GetMapping("/test4")
    public RestResult<?> test4()
    {
        this.test1();
        long start = System.currentTimeMillis();
        for (int j = 0; j < 100000; j++)
        {
            redisTemplate.opsForValue().get("testElc_test1");
        }
        long end = System.currentTimeMillis();
        long seconds = TimeUnit.MILLISECONDS.toSeconds(end - start);
        LOG.error("----- test4 request time[{}] -------", seconds);
        return RestResult.successData(seconds);
    }
    
    @PostMapping("/getSession")
    public RestResult<Session> getSession(
        @RequestParam("sessionId") @NotBlank String sessionId)
    {
        Session session = SessionUtils.getSession(sessionId);
        if (session == null)
        {
            throw new ParameterValidateException("");
        }
        return RestResult.successData(session);
    }
    
    @PostMapping("/getSession2")
    public RestResult<Session> getSession2(
        @RequestParam("sessionId") @NotBlank String sessionId)
    {
        Session session = SessionUtils.getSession(sessionId);
        if (session == null)
        {
            throw new ParameterValidateException("");
        }
        return RestResult.successData(new Session());
    }
}
