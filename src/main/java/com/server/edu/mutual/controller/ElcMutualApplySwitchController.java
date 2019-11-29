package com.server.edu.mutual.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.server.edu.common.rest.RestResult;
import com.server.edu.mutual.entity.ElcMutualApplyTurns;
import com.server.edu.mutual.service.ElcMutualApplySwitchService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
@SwaggerDefinition(info = @Info(title = "互选课程申请开关", version = ""))
@RestSchema(schemaId = "ElcMutualApplySwitchController")
@RequestMapping("/elcMutualApply/elcMutualApplySwitch")
public class ElcMutualApplySwitchController {
	private static Logger LOG =
	        LoggerFactory.getLogger(ElcMutualApplySwitchController.class);
	@Autowired
	private ElcMutualApplySwitchService elcMutualApplySwitchService;
    
    /**
            *查询选课申请开关
     * 
     * @param calendarId
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "查询选课申请开关")
    @GetMapping
    public RestResult<ElcMutualApplyTurns> getElcMutualApplySwitch(
    		@RequestParam("calendarId") @NotNull Long calendarId,@RequestParam("projectId") String projectId,@RequestParam("category") Integer category)
        throws Exception
    {
        LOG.info("getElcNumberSetInfo.start");
        ElcMutualApplyTurns elcMutualApplyTurns = elcMutualApplySwitchService.getElcMutualApplyTurns(calendarId,projectId,category);
        return RestResult.successData(elcMutualApplyTurns);
    }
	
	
    /**
             * 保存选课申请开关
     * 
     * @param elcMutualApplyTurns
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "保存选课申请开关")
    @PostMapping
    public RestResult<?> save(
    		@RequestBody @Valid ElcMutualApplyTurns elcMutualApplyTurns)
        throws Exception
    {
        LOG.info("saveElcMutualApplySwitch.start");
        elcMutualApplySwitchService.save(elcMutualApplyTurns);
        return RestResult.success();
    }
    
}
