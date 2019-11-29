package com.server.edu.mutual.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.RestResult;
import com.server.edu.mutual.dto.ElcMutualCrossStu;
import com.server.edu.mutual.dto.ElcMutualCrossStuDto;
import com.server.edu.mutual.service.ElcMutualCrossService;
import com.server.edu.mutual.vo.ElcMutualCrossStuVo;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
@SwaggerDefinition(info = @Info(title = "本研互选及跨学科学生名单", version = ""))
@RestSchema(schemaId = "ElcMutualStudentsController")
@RequestMapping("elcMutualCross")
public class ElcMutualStudentsController {
	private static Logger LOG =
	        LoggerFactory.getLogger(ElcMutualStudentsController.class);
	@Autowired
	private ElcMutualCrossService elcMutualCrossService;
    
    /**
     *获取互选跨学科名单列表
     * 
     * @param dto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "获取互选跨学科名单列表")
    @PostMapping("/getElcMutualCrossList")
    public RestResult<PageInfo<ElcMutualCrossStuVo>> getElcMutualCrossList(
    		@RequestBody PageCondition<ElcMutualCrossStuDto> dto)
        throws Exception
    {
        LOG.info("getElcMutualCrossList.start");
        PageInfo<ElcMutualCrossStuVo> list =elcMutualCrossService.getElcMutualCrossList(dto);
        return RestResult.successData(list);
    }
	
    /**
            * 本研互选学生名单初始化
     * 
     * @param calendarId
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "本研互选学生名单初始化")
    @PostMapping("/init")
    public RestResult<Integer> init(
    		@RequestParam("calendarId") @NotNull Long calendarId)
        throws Exception
    {
        LOG.info("init.start");
        int result =elcMutualCrossService.init(calendarId);
        return RestResult.successData(result);
    }
	
    /**
            * 按学号添加学生名单
     * 
     * @param calendarId
     * @param studentIds
     * @param mode
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "按学号添加学生名单")
    @PostMapping("/add")
    public RestResult<Integer> add(
    		@RequestParam("calendarId") @NotNull Long calendarId,@RequestParam("studentIds") @NotBlank String studentIds,@RequestParam("mode") @NotNull Integer mode)
        throws Exception
    {
        LOG.info("add.start");
        int result =elcMutualCrossService.add(calendarId,studentIds,mode);
        return RestResult.successData(result);
    }
    
    
    /**
            * 批量添加学生名单
     * 
     * @param dto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "批量添加学生名单")
    @PostMapping("/batchAdd")
    public RestResult<Integer> batchAdd(
    		@RequestBody @Valid ElcMutualCrossStu dto)
        throws Exception
    {
        LOG.info("batchAdd.start");
        int result =elcMutualCrossService.batchAdd(dto);
        return RestResult.successData(result);
    }
    
    /**
             * 全部添加学生名单
     * 
     * @param dto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "全部添加学生名单")
    @PostMapping("/addAll")
    public RestResult<Integer> addAll(
    		@RequestBody @Valid ElcMutualCrossStu dto)
        throws Exception
    {
        LOG.info("batchAdd.start");
        int result =elcMutualCrossService.addAll(dto);
        return RestResult.successData(result);
    }
    
    /**
             * 根据ID删除本研互选或者跨学科学生
     * 
     * @param 
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "根据ID删除本研互选或者跨学科学生")
    @PostMapping("/delete")
    public RestResult<Integer> delete(
    		@RequestBody ElcMutualCrossStuDto dto)
        throws Exception
    {
        LOG.info("delete.start");
        int result =elcMutualCrossService.delete(dto);
        return RestResult.successData(result);
    }
    
    /**
     * 批量删除学生名单
     * 
     * @param dto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "批量删除学生名单")
    @PostMapping("/batchDelete")
    public RestResult<Integer> batchDelete(
    		@RequestBody @Valid ElcMutualCrossStu dto)
        throws Exception
    {
        LOG.info("batchDelete.start");
        int result =elcMutualCrossService.batchDelete(dto);
        return RestResult.successData(result);
    }
    
    /**
     *全部删除学生名单
     * 
     * @param dto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "全部删除学生名单")
    @PostMapping("/deleteAll")
    public RestResult<Integer> deleteAll(
    		@RequestParam("calendarId") Long calendarId,@RequestParam("mode") Integer mode)
        throws Exception
    {
        LOG.info("deleteAll.start");
        int result =elcMutualCrossService.deleteAll(calendarId,mode);
        return RestResult.successData(result);
    }
	
}
