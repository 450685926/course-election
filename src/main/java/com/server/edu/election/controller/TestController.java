package com.server.edu.election.controller;

import java.util.ArrayList;
import java.util.List;
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
        if(TimeUnit.MILLISECONDS.toSeconds(end - start) > 1) {
            LOG.error("----- getRounds request time > 1 -------");
        }
        return RestResult.successData(data);
    }
    
    @PostMapping("/getRound2")
    public RestResult<List<ElectionRoundsVo>> getRound2(
        @RequestParam("electionObj") @NotBlank String electionObj,
        @RequestParam("projectId") @NotBlank String projectId,
        @RequestParam(name = "mode") @NotNull Integer mode,
        @RequestParam(name = "studentId") String studentId){
        
        long start = System.currentTimeMillis();
        
        String string = redisTemplate.opsForValue().get("testElc_test1");
        
        long end = System.currentTimeMillis();
        if(TimeUnit.MILLISECONDS.toSeconds(end - start) > 1) {
            LOG.error("----- getRound2 request time > 1 -------");
        }
        List<ElectionRoundsVo> data = new ArrayList<>();
        return RestResult.successData(data);
    }
    
    @PostMapping("/getRound3")
    public RestResult<List<ElectionRounds>> getRound3(){
        
        long start = System.currentTimeMillis();
        
        String text = redisTemplate.opsForValue().get("testElc_test1");
        
        List<ElectionRounds> data = JSON.parseArray(text, ElectionRounds.class);
        
        long end = System.currentTimeMillis();
        if(TimeUnit.MILLISECONDS.toSeconds(end - start) > 1) {
            LOG.error("----- getRound2 request time > 1 -------");
        }
        
        return RestResult.successData(data);
    }
    
    @GetMapping("/test1")
    public RestResult<?> test1(){
        List<ElectionRounds> allRound = dataProvider.getAllRound();
        redisTemplate.opsForValue().set("testElc_test1", JSON.toJSONString(allRound), 1, TimeUnit.HOURS);
        return RestResult.success();
    }
    
    @PostMapping("/getSession")
    public RestResult<Session> getSession(
        @RequestParam("sessionId") @NotBlank String sessionId)
    {
        Session session = SessionUtils.getSession(sessionId);
        if(session == null) {
            throw new ParameterValidateException("");
        }
        return RestResult.successData(session);
    }
    
    @PostMapping("/getSession2")
    public RestResult<Session> getSession2(
        @RequestParam("sessionId") @NotBlank String sessionId)
    {
        Session session = SessionUtils.getSession(sessionId);
        if(session == null) {
            throw new ParameterValidateException("");
        }
        return RestResult.successData(new Session());
    }
}
