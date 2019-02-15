package com.server.edu.election.controller;

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

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.dto.ElectionConstantsDto;
import com.server.edu.election.entity.ElectionConstants;
import com.server.edu.election.service.ElectionConstantsService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "选课常量", version = ""))
@RestSchema(schemaId = "ElectionConstantsController")
@RequestMapping("electionConstants")
public class ElectionConstantsController {
	private static Logger LOG =
	        LoggerFactory.getLogger(ElectionConstantsController.class);
	@Autowired
	private ElectionConstantsService electionConstantsService;
	
	 /**
     * 选课常量列表
     * 
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "选课常量列表")
    @PostMapping("/list")
    public RestResult<PageInfo<ElectionConstants>> list(
        @RequestBody PageCondition<ElectionConstantsDto> condition)
        throws Exception
    {
        LOG.info("list.start");
        PageInfo<ElectionConstants> list =electionConstantsService.list(condition);
        return RestResult.successData(list);
    }
    
    /**
     * 添加选课常量
     * 
     * @param dto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "添加选课常量")
    @PostMapping("/add")
    public RestResult<Integer> add(
        @RequestBody @Valid ElectionConstantsDto dto)
        throws Exception
    {
        LOG.info("add.start");
        int result =electionConstantsService.add(dto);
        return RestResult.successData(result);
    }
    
    /**
     * 修改选课常量
     * 
     * @param dto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "修改选课常量")
    @PostMapping("/update")
    public RestResult<Integer> update(
        @RequestBody @Valid ElectionConstantsDto dto)
        throws Exception
    {
        LOG.info("update.start");
        int result =electionConstantsService.update(dto);
        return RestResult.successData(result);
    }
    
    /**
     * 查看选课常量
     * 
     * @param dto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "查看选课常量")
    @GetMapping("/getConstants")
    public RestResult<ElectionConstants> getConstants(
    		@RequestParam("id") @NotNull Long id)
        throws Exception
    {
        LOG.info("getConstants.start");
        ElectionConstants electionConstants =electionConstantsService.getConstants(id);
        return RestResult.successData(electionConstants);
    }

}
