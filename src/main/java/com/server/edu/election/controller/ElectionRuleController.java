package com.server.edu.election.controller;

import java.util.List;

import javax.validation.Valid;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.dto.ElectionRuleDto;
import com.server.edu.election.entity.ElectionRule;
import com.server.edu.election.service.ElectionRuleService;
import com.server.edu.election.vo.ElectionRuleVo;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "选课规则参数", version = ""))
@RestSchema(schemaId="ElectionRuleController")
@RequestMapping("electionRule")
public class ElectionRuleController
{
	private static Logger LOG =
	        LoggerFactory.getLogger("TeachingClassChangeContoller");
	@Autowired
	private ElectionRuleService service;
	 /**
     * 选课规则
     * 
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "选课规则")
    @PostMapping("/ruleList")
    public RestResult<List<ElectionRule>> ruleList(
        @RequestBody ElectionRuleDto electionRuleDto)
        throws Exception
    {
        LOG.info("ruleList.start");
        List<ElectionRule> ruleList =service.list(electionRuleDto);
        return RestResult.successData(ruleList);
    }
    
	 /**
     * 选课规则详情
     * 
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "选课规则详情")
    @PostMapping("/ruleDeatil")
    public RestResult<ElectionRuleVo> ruleDeatil(
        @Valid @RequestBody ElectionRuleDto electionRuleDto)
        throws Exception
    {
        LOG.info("ruleList.start");
        ElectionRuleVo ruleList =service.selectRuleDeatil(electionRuleDto);
        return RestResult.successData(ruleList);
    }
    
}
