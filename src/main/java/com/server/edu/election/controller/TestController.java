package com.server.edu.election.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.server.edu.common.rest.RestResult;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.studentelec.service.StudentElecService;
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
    private StudentElecService elecService;
    
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
        List<ElectionRoundsVo> data = new ArrayList<>();
        List<ElectionRounds> allRound = dataProvider.getAllRound();
        return RestResult.successData(data);
    }
    
    @PostMapping("/getRounds1")
    public RestResult<List<ElectionRoundsVo>> getRounds1(
        @RequestParam("electionObj") @NotBlank String electionObj,
        @RequestParam("projectId") @NotBlank String projectId,
        @RequestParam(name = "mode") @NotNull Integer mode,
        @RequestParam(name = "studentId") String studentId)
    {
        SessionUtils.getCurrentSession();
        List<ElectionRoundsVo> data = new ArrayList<>();
        return RestResult.successData(data);
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
