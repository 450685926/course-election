package com.server.edu.mutual.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.server.edu.common.rest.RestResult;
import com.server.edu.election.constants.RoundMode;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.studentelec.service.impl.RoundDataProvider;
import com.server.edu.election.vo.ElectionRoundsVo;
import com.server.edu.election.vo.ElectionRuleVo;
import com.server.edu.mutual.util.ProjectUtil;

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
}
