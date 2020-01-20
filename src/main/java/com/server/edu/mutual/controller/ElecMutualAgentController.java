package com.server.edu.mutual.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.server.edu.common.rest.RestResult;
import com.server.edu.common.validator.Assert;
import com.server.edu.election.constants.RoundMode;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.entity.Student;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.service.impl.RoundDataProvider;
import com.server.edu.election.vo.ElectionRoundsVo;
import com.server.edu.election.vo.ElectionRuleVo;
import com.server.edu.mutual.studentelec.context.ElecContextMutualBk;
import com.server.edu.mutual.studentelec.service.StudentMutualElecService;
import com.server.edu.mutual.util.ProjectUtil;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "本研互选代理选课", version = ""))
@RestSchema(schemaId = "ElecMutualAgentController")
@RequestMapping("elecMutualAgent")
public class ElecMutualAgentController {
	Logger LOG = LoggerFactory.getLogger(ElecMutualAgentController.class);
	
    @Autowired
    private RoundDataProvider dataProvider;
    
    @Autowired
    private StudentMutualElecService mutualElecService;
    
	
    @ApiOperation(value = "获取生效的轮次")
    @PostMapping("/getRounds")
    public RestResult<List<ElectionRoundsVo>> getRounds(
        @RequestParam("electionObj") @NotBlank String electionObj,
        @RequestParam("projectId") @NotBlank String projectId)
    {
        List<ElectionRoundsVo> data = new ArrayList<>();
        List<ElectionRounds> allRound = dataProvider.getAllRound();
        Date date = new Date();
        List<String> projectIds = ProjectUtil.getProjectIds(projectId);
        
        for (ElectionRounds round : allRound)
        {
            Long roundId = round.getId();
            if (projectIds.contains(round.getProjectId())
    			&& StringUtils.equals(electionObj, round.getElectionObj())
            	&& round.getMode().intValue() == RoundMode.BenYanHuXuan.mode()	
                && date.after(round.getBeginTime())
                && date.before(round.getEndTime()))
            {
                ElectionRoundsVo vo = new ElectionRoundsVo(round);
                List<ElectionRuleVo> rules = dataProvider.getRules(roundId);
                vo.setRuleVos(rules);
                data.add(vo);
            }
        }
        return RestResult.successData(data);
    }
    
    @ApiOperation(value = "查询轮次学生信息")
    @PostMapping("/findStuRound")
    public RestResult<Student> findStuRound(
        @RequestBody ElecRequest elecRequest)
    {
        RestResult<Student> result = new RestResult<Student>();
		try {
			result = mutualElecService.findStuRound(elecRequest.getRoundId(),elecRequest.getStudentId());
		} catch (Exception e) {
			result.fail(e.getMessage());
		}
        return result;
    }
    
    /**
     * 进入选课,此时要加载数据
     * 登录选课界面时预加载数据的请求，只需要包括studentId status，前端会定时执行请求直到status变为ready 即加载完成
     */
    @ApiOperation(value = "数据加载")
    @PostMapping("/{roundId}/loading")
    public RestResult<ElecRespose> studentLoading(
        @PathVariable("roundId") @NotNull Long roundId, @RequestParam("studentId") String studentId)
    {
        Session session = SessionUtils.getCurrentSession();
        ElecRequest elecRequest = new ElecRequest();
        elecRequest.setRoundId(roundId);
        elecRequest.setStudentId(studentId);
        elecRequest.setProjectId(session.getCurrentManageDptId());
        
        return mutualElecService.loading(elecRequest);
    }
    
    @ApiOperation(value = "获取本科生选课数据")
    @PostMapping("/{roundId}/getDataBk")
    public RestResult<ElecContextMutualBk> getDataBk(
        @PathVariable("roundId") @NotNull Long roundId, @RequestParam("studentId") String studentId)
    {
        ElectionRounds round = dataProvider.getRound(roundId);
        Assert.notNull(round, "elec.roundNotExistTip");
        
        Long calendarId = round.getCalendarId();
        
        ElecContextMutualBk c =
            new ElecContextMutualBk(studentId, calendarId);
        c = mutualElecService.getData(c, round, calendarId);
        return RestResult.successData(c);
    }
    
    /**
     * 获取选课结果的请求 未完成时status为processing， 前端会定时执行请求直到status变为ready，此时应返回所有选课结果
     */
    @ApiOperation(value = "查询选课结果")
    @PostMapping("/{roundId}/electRes")
    public RestResult<ElecRespose> getElect(
        @PathVariable("roundId") @NotNull Long roundId, @RequestParam("studentId") String studentId)
    {
        ElecRequest elecRequest = new ElecRequest();
        elecRequest.setRoundId(roundId);
        elecRequest.setStudentId(studentId);
        ElecRespose response = mutualElecService.getElectResult(elecRequest);
        return RestResult.successData(response);
    }
    
    /**
     * 选课请求,选课时发送一次，此时应该返回ElecRespose.status=processing
     */
    @ApiOperation(value = "学生选课")
    @PostMapping("/elect")
    public RestResult<ElecRespose> elect(
        @RequestBody @Valid ElecRequest elecRequest, @RequestParam("studentId") String studentId, @RequestParam("chooseObj") Integer chooseObj)
    {
    	Session session = SessionUtils.getCurrentSession();
        elecRequest.setChooseObj(chooseObj);
        elecRequest.setStudentId(studentId);
        elecRequest.setCreateBy(session.getUid());
        elecRequest.setRequestIp(SessionUtils.getRequestIp());
        elecRequest.setProjectId(session.getCurrentManageDptId());
        
        return mutualElecService.elect(elecRequest);
    }
    
}
