package com.server.edu.election.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.entity.ElcResultSwitch;
import com.server.edu.election.service.ElecResultSwitchService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "选课结果处理开关设置", version = ""))
@RestSchema(schemaId = "ElecResultSwitchController")
@RequestMapping("elecSwitch")
public class ElecResultSwitchController {
	@Autowired
	private ElecResultSwitchService elecResultSwitchService;

	@ApiOperation(value = "添加选课结果设置开关")
	@PutMapping
    public RestResult<Integer> add(
    		  @RequestBody @NotNull @Valid ElcResultSwitch resultSwitch){
		elecResultSwitchService.add(resultSwitch);
        return RestResult.success();
    }
	
	@ApiOperation(value = "查询选课结果设置开关")
	@GetMapping("/{id}")
	public RestResult<ElcResultSwitch> get(
			@PathVariable("id") Long id){
		ElcResultSwitch result = elecResultSwitchService.get(id);
		return RestResult.successData(result);
	}
	
}
