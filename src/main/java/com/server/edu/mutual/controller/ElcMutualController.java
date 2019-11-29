package com.server.edu.mutual.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.server.edu.common.enums.UserTypeEnum;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.validator.Assert;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.constants.RoundMode;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.service.impl.RoundDataProvider;
import com.server.edu.election.vo.ElectionRoundsVo;
import com.server.edu.election.vo.ElectionRuleVo;
import com.server.edu.mutual.studentelec.service.StudentMutualElecService;
import com.server.edu.mutual.util.ProjectUtil;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "互选学生选课", version = ""))
@RestSchema(schemaId = "elcMutualController")
@RequestMapping("mutualStudent")
public class ElcMutualController {
	Logger LOG = LoggerFactory.getLogger(ElcMutualController.class);
	
    @Autowired
    private RoundDataProvider dataProvider;
    
    @Autowired
    private StudentMutualElecService mutualElecService;
    
	
	@ApiOperation(value = "获取生效的轮次")
    @PostMapping("/getRounds")
    public RestResult<List<ElectionRoundsVo>> getRounds(
        @RequestParam("projectId") @NotBlank String projectId)
	{
        List<ElectionRoundsVo> data = new ArrayList<>();
        List<ElectionRounds> allRound = dataProvider.getAllRound();
        Date date = new Date();
        List<String> projectIds = ProjectUtil.getProjectIds(projectId);
        Session session = SessionUtils.getCurrentSession();
        String studentId = session.realUid();
        
        for (ElectionRounds round : allRound)
        {
            Long roundId = round.getId();
            if (projectIds.contains(round.getProjectId())
    			&& StringUtils.equals(Constants.STU, round.getElectionObj())
            	&& round.getMode().intValue() == RoundMode.BenYanHuXuan.mode()	
                && date.after(round.getBeginTime())
                && date.before(round.getEndTime())
                && dataProvider.containsMutualStuCondition(roundId, studentId, projectId))
            {
                ElectionRoundsVo vo = new ElectionRoundsVo(round);
                List<ElectionRuleVo> rules = dataProvider.getRules(roundId);
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
    @PostMapping("/{roundId}/loading")
    public RestResult<ElecRespose> studentLoading(
        @PathVariable("roundId") @NotNull Long roundId)
    {
        Session session = SessionUtils.getCurrentSession();
        
        if (session.realType() != UserTypeEnum.STUDENT.getValue())
        {
            return RestResult.fail("elec.mustBeStu");
        }
        ElecRequest elecRequest = new ElecRequest();
        elecRequest.setRoundId(roundId);
        elecRequest.setStudentId(session.realUid());
        elecRequest.setProjectId(session.getCurrentManageDptId());
        
        return mutualElecService.loading(elecRequest);
    }
    
    @ApiOperation(value = "获取本科生选课数据")
    @PostMapping("/{roundId}/getDataBk")
    public RestResult<ElecContextBk> getDataBk(
        @PathVariable("roundId") @NotNull Long roundId)
    {
        Session session = SessionUtils.getCurrentSession();
        
        if (session.realType() != UserTypeEnum.STUDENT.getValue())
        {
            return RestResult.fail("elec.mustBeStu");
        }
        ElectionRounds round = dataProvider.getRound(roundId);
        Assert.notNull(round, "elec.roundNotExistTip");
        
        ElecContextBk c =
            new ElecContextBk(session.realUid(), round.getCalendarId());

        return RestResult.successData(c);
    }
 
}
