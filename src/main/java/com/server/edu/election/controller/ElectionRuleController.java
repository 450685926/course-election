package com.server.edu.election.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.server.edu.common.rest.RestResult;
import com.server.edu.election.dto.ElectionRuleDto;
import com.server.edu.election.entity.ElectionRule;
import com.server.edu.election.service.ElectionRuleService;
import com.server.edu.election.vo.ElectionRuleVo;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "选课规则", version = ""))
@RestSchema(schemaId = "ElectionRuleController")
@RequestMapping("electionRule")
public class ElectionRuleController
{
	private static Logger LOG =
	        LoggerFactory.getLogger(ElectionRuleController.class);
	@Autowired
	private ElectionRuleService service;
	 /**
     * 选课规则
     * 
     * @param electionRuleDto
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
        List<ElectionRule> ruleList = service.list(electionRuleDto);
        return RestResult.successData(ruleList);
    }
    
	 /**
     * 选课规则详情
     * 
     * @param electionRuleDto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "选课规则详情")
    @GetMapping("/ruleDeatil")
    public RestResult<ElectionRuleVo> ruleDeatil(
    		@RequestParam("id") @NotNull Long id)
        throws Exception
    {
        LOG.info("ruleDeatil.start");
        ElectionRuleVo ruleList =service.selectRuleDeatil(id);
        return RestResult.successData(ruleList);
    }
    
	 /**
     * 修改规则
     * 
     * @param electionRuleDto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "修改规则")
    @PostMapping("/updateRule")
    public RestResult<Integer> updateRule(
        @Valid @RequestBody ElectionRuleDto electionRuleDto)
        throws Exception
    {
        LOG.info("updateStatus.start");
        return service.updateRule(electionRuleDto);
    }
    
	 /**
     * 修改规则参数
     * 
     * @param electionRuleDto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "修改规则参数")
    @PostMapping("/updateRuleParameter")
    public RestResult<Integer> updateRuleParameter(
        @Valid @RequestBody ElectionRuleDto electionRuleDto)
        throws Exception
    {
        LOG.info("updateStatus.start");
        return service.updateRuleParameter(electionRuleDto);
    }
    
    
    /**
     * 通过projectId查询规则
     * 
     * @param projectId
     * @return
     * @see [类、类#方法、类#成员]
     */
    @GetMapping("/byProject/{projectId}")
    public RestResult<List<ElectionRuleVo>> getAllList(
        @PathVariable("projectId") String projectId)
    {
        List<ElectionRuleVo> listAll = service.listAll(projectId);
        
        return RestResult.successData(listAll);
    }
    
}
