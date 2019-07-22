package com.server.edu.election.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.entity.ExemptionApplyAuditSwitch;
import com.server.edu.election.service.ExemptionAuditSwitchService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "研究生免修免考申请审核开关", version = ""))
@RestSchema(schemaId = "ExemptionAuditSwitchController")
@RequestMapping("/exemptionGradute")
public class ExemptionAuditSwitchController {
    static Logger logger = LoggerFactory.getLogger(ExemptionController.class);
	
	@Autowired
	ExemptionAuditSwitchService exemptionAuditSwitchService;
	
	@ApiOperation(value = "研究生添加免修免考申请审核开关")
    @PostMapping("/addExemptionAuditSwitch")
    public RestResult<?> addExemptionAuditSwitch(@RequestBody @NotNull @Valid ExemptionApplyAuditSwitch applyAuditSwitch){
    	logger.info("addExemptionAuditSwitch start");
    	
    	exemptionAuditSwitchService.addExemptionAuditSwitch(applyAuditSwitch);
    	return RestResult.success();
    }

    @ApiOperation(value = "研究生查询免修免考申请审核开关")
    @PostMapping("/queryExemptionAuditSwitch")
    public RestResult<PageResult<ExemptionApplyAuditSwitch>> queryExemptionAuditSwitch(@RequestBody PageCondition<ExemptionApplyAuditSwitch> condition){
    	logger.info("queryExemptionAuditSwitch start");
    	
    	PageResult<ExemptionApplyAuditSwitch> page = exemptionAuditSwitchService.queryExemptionAuditSwitch(condition);
    	return RestResult.successData(page);
    }
    
    @ApiOperation(value = "根据ID查询免修免考申请审核开关")
    @GetMapping("/{id}")
    public RestResult<ExemptionApplyAuditSwitch> findExemptionAuditSwitchById(@PathVariable("id") Long id){
    	logger.info("findExemptionAuditSwitchById start");
    	
    	ExemptionApplyAuditSwitch exemptionApplyAuditSwitch = exemptionAuditSwitchService.findExemptionAuditSwitchById(id);
    	return RestResult.successData(exemptionApplyAuditSwitch);
    }
    
    @ApiOperation(value = "修改免修免考申请审核开关")
    @PostMapping("/updateExemptionAuditSwitch")
    public RestResult<?> updateExemptionAuditSwitch(@RequestBody @NotNull @Valid ExemptionApplyAuditSwitch applyAuditSwitch){
    	logger.info("updateExemptionAuditSwitch start");
    	
    	exemptionAuditSwitchService.updateExemptionAuditSwitch(applyAuditSwitch);
    	return RestResult.success();
    }
    
    @ApiOperation(value = "删除免修免考申请审核开关")
    @DeleteMapping
    public RestResult<?> deleteExemptionAuditSwitch(@RequestBody @NotEmpty List<Long> ids){
    	logger.info("deleteExemptionAuditSwitch start");
    	
    	exemptionAuditSwitchService.deleteExemptionAuditSwitch(ids);
    	return RestResult.success();
    }
    
}
