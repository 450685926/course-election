package com.server.edu.election.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.server.edu.common.rest.RestResult;
import com.server.edu.common.validator.ValidatorUtil;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.entity.Student;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.service.StudentElecService;
import com.server.edu.election.studentelec.service.impl.RoundDataProvider;
import com.server.edu.election.studentelec.utils.ElecContextUtil;
import com.server.edu.election.studentelec.utils.ElecStatus;
import com.server.edu.election.validate.AgentElcGroup;
import com.server.edu.election.vo.ElectionRoundsVo;
import com.server.edu.election.vo.ElectionRuleVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "代理选课", version = ""))
@RestSchema(schemaId = "ElecAgentController")
@RequestMapping("agentElc")
public class ElecAgentController
{
    @Autowired
    private StudentElecService elecService;
    
    @Autowired
    private RoundDataProvider dataProvider;
    
    @ApiOperation(value = "获取生效的轮次")
    @PostMapping("/getRounds")
    public RestResult<List<ElectionRoundsVo>> getRounds(
        @RequestParam("electionObj") @NotBlank String electionObj,
        @RequestParam("projectId") @NotBlank String projectId,
        @RequestParam(name = "mode") @NotNull Integer mode)
    {
        List<ElectionRoundsVo> data = new ArrayList<>();
        List<ElectionRounds> allRound = dataProvider.getAllRound();
        Date date = new Date();
        for (ElectionRounds round : allRound)
        {
            if (StringUtils.equals(round.getProjectId(), projectId)
                && StringUtils.equals(electionObj, round.getElectionObj())
                && Objects.equals(mode, round.getMode())
                && date.after(round.getBeginTime())
                && date.before(round.getEndTime()))
            {
                ElectionRoundsVo vo = new ElectionRoundsVo(round);
                List<ElectionRuleVo> rules =
                    dataProvider.getRules(round.getId());
                vo.setRuleVos(rules);
                data.add(vo);
            }
        }
        
        return RestResult.successData(data);
    }
    
    /**
     * 进入选课,此时要加载数据
     * 登录选课界面时预加载数据的请求，只需要包括studentId status，前端会定时执行请求直到status变为ready 即加载完成
     */
    @ApiOperation(value = "数据加载")
    @PostMapping("/loading")
    public RestResult<ElecRespose> studentLoading(
        @RequestBody ElecRequest elecRequest)
    {
        ValidatorUtil.validateAndThrow(elecRequest, AgentElcGroup.class);
        
        String studentId = elecRequest.getStudentId();
        return elecService.loading(elecRequest.getRoundId(), studentId);
    }
    
    @ApiOperation(value = "获取学生选课数据")
    @PostMapping("/getData")
    public RestResult<ElecContext> getData(@RequestBody ElecRequest elecRequest)
    {
        ValidatorUtil.validateAndThrow(elecRequest, AgentElcGroup.class);
        
        String studentId = elecRequest.getStudentId();
        
        ElectionRounds round = dataProvider.getRound(elecRequest.getRoundId());
        if (round == null)
        {
            return RestResult.error("elec.roundNotExistTip");
        }
        ElecContext c = new ElecContext(studentId, round.getCalendarId());
        
        return RestResult.successData(c);
    }
    
    /**
     * 选课请求,选课时发送一次，此时应该返回ElecRespose.status=processing
     */
    @ApiOperation(value = "学生选课")
    @PostMapping("/elect")
    public RestResult<ElecRespose> elect(@RequestBody ElecRequest elecRequest)
    {
        ValidatorUtil.validateAndThrow(elecRequest, AgentElcGroup.class);
        if (elecRequest.getChooseObj() == null)
        {
            throw new ParameterValidateException("chooseObj not be null");
        }
        Session session = SessionUtils.getCurrentSession();
        elecRequest.setCreateBy(session.getUid());
        elecRequest.setRequestIp(session.getIp());
        return elecService.elect(elecRequest);
    }
    
    /**
     * 获取选课结果的请求 未完成时status为processing， 前端会定时执行请求直到status变为ready，此时应返回所有选课结果
     */
    @ApiOperation(value = "查询选课结果")
    @PostMapping("/electRes")
    public RestResult<ElecRespose> getElect(
        @RequestBody ElecRequest elecRequest)
    {
        ValidatorUtil.validateAndThrow(elecRequest, AgentElcGroup.class);
        
        String studentId = elecRequest.getStudentId();
        ElecRespose response =
            elecService.getElectResult(elecRequest.getRoundId(), studentId);
        return RestResult.successData(response);
    }
    
    /**
    *@Description: 通过学号和轮次获取学生信息
    *@Param:
    *@return: 
    *@Author: bear
    *@date: 2019/3/5 14:13
    */
    @ApiOperation(value = "查询轮次学生信息")
    @PostMapping("/findStuRound")
    public RestResult<Student> findStuRound(
        @RequestBody ElecRequest elecRequest)
    {
        ValidatorUtil.validateAndThrow(elecRequest, AgentElcGroup.class);
        
        Student stu = elecService.findStuRound(elecRequest.getRoundId(),
            elecRequest.getStudentId());
        return RestResult.successData(stu);
    }
    
    @ApiOperation(value = "清除学生选课缓存数据")
    @PostMapping("/clearCache")
    public RestResult<?> clearCache(@RequestBody ElecRequest elecRequest)
    {
        ValidatorUtil.validateAndThrow(elecRequest, AgentElcGroup.class);
        
        ElecContextUtil.setElecStatus(elecRequest.getRoundId(),
            elecRequest.getStudentId(),
            ElecStatus.Init);
        
        return RestResult.success();
    }
    
}