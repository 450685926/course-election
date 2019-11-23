package com.server.edu.election.controller;

import com.server.edu.common.rest.RestResult;
import com.server.edu.election.entity.ElcRebuildChargeTimeSet;
import com.server.edu.election.service.ElcRebuildChargeTimeSetService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@SwaggerDefinition(info = @Info(title = "重修缴费时间设置", version = ""))
@RestSchema(schemaId = "ElcRebuildChargeTimeSetController")
@RequestMapping("elcRebuildChargeTimeSet")
public class ElcRebuildChargeTimeSetController {
	private static Logger LOG =
	        LoggerFactory.getLogger(ElcRebuildChargeTimeSetController.class);
	
	@Autowired
	private ElcRebuildChargeTimeSetService elcRebuildChargeTimeSetService;
	
    /**
     * 新增
     * 
     * @param elcRebuildChargeTimeSet
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "新增选课重修缴费设置")
    @PostMapping("/add")
    public RestResult<Integer> add(
    		@RequestBody @Valid ElcRebuildChargeTimeSet elcRebuildChargeTimeSet)
        throws Exception
    {
        LOG.info("add.start");
        int result =elcRebuildChargeTimeSetService.add(elcRebuildChargeTimeSet);
        return RestResult.successData(result);
    }
    
    /**
     * 修改
     * 
     * @param elcRebuildChargeTimeSet
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "修改选课重修缴费设置")
    @PostMapping("/update")
    public RestResult<Integer> update(
    		@RequestBody @Valid ElcRebuildChargeTimeSet elcRebuildChargeTimeSet)
        throws Exception
    {
        LOG.info("update.start");
        int result =elcRebuildChargeTimeSetService.update(elcRebuildChargeTimeSet);
        return RestResult.successData(result);
    }
    
    /**
     * 查询缴费设置
     * 
     * @param calendarId
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "查询缴费设置")
    @GetMapping("/getRebuildChargeTimeSet")
    public RestResult<ElcRebuildChargeTimeSet> getRebuildChargeTimeSet(
    		@RequestParam("calendarId") @NotNull Long calendarId,@RequestParam("projId") @NotBlank String projId)
        throws Exception
    {
        LOG.info("getRebuildChargeTimeSet.start");
        ElcRebuildChargeTimeSet timeSet =elcRebuildChargeTimeSetService.getRebuildChargeTimeSet(calendarId,projId);
        return RestResult.successData(timeSet);
    }

    /**
     * @Description: 检查缴费时间是否在正常缴费时间内
     * @author kan yuanfeng
     * @date 2019/11/19 14:56
     */
    @ApiOperation("检查缴费时间是否在正常缴费时间内")
    @GetMapping("check/{calendarId}")
    public RestResult<?> checkTime(@PathVariable("calendarId") @NotNull Long calendarId){
        elcRebuildChargeTimeSetService.checkTime(calendarId);
        return RestResult.success();
    }

}
