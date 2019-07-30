package com.server.edu.election.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.server.edu.common.rest.RestResult;
import com.server.edu.election.entity.ElcNumberSet;
import com.server.edu.election.service.ElcNumberSetService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
@SwaggerDefinition(info = @Info(title = "可选容量设置", version = ""))
@RestSchema(schemaId = "ElcNumberSetController")
@RequestMapping("elcNumberSet")
public class ElcNumberSetController {
	private static Logger LOG =
	        LoggerFactory.getLogger(ElcNumberSetController.class);
	@Autowired
	private ElcNumberSetService elcNumberSetService;
    /**
     *releaseAll
     * 
     * @param calendarId
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "释放全部")
    @PostMapping("/releaseAll")
    public RestResult<Integer> releaseAll(
    		@RequestParam("calendarId") @NotNull Long calendarId)
        throws Exception
    {
        LOG.info("releaseAll.start");
        int result =elcNumberSetService.releaseAll(calendarId);
        return RestResult.successData(result);
    }
    
    /**
     *获取选课容量释放信息
     * 
     * @param calendarId
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "获取选课容量释放信息")
    @PostMapping("/getElcNumberSetInfo")
    public RestResult<ElcNumberSet> getElcNumberSetInfo(
    		@RequestParam("calendarId") @NotNull Long calendarId)
        throws Exception
    {
        LOG.info("getElcNumberSetInfo.start");
        ElcNumberSet elcNumberSet =elcNumberSetService.getElcNumberSetInfo(calendarId);
        return RestResult.successData(elcNumberSet);
    }
	
	
    /**
     * 定时释放
     * 
     * @param elcNumberSet
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "定时释放")
    @PostMapping("/save")
    public RestResult<Integer> save(
    		@RequestBody @Valid ElcNumberSet elcNumberSet)
        throws Exception
    {
        LOG.info("save.start");
        int result =elcNumberSetService.save(elcNumberSet);
        return RestResult.successData(result);
    }
    
	
}
