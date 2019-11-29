package com.server.edu.mutual.controller;

import javax.validation.constraints.NotNull;

import org.apache.servicecomb.provider.rest.common.RestSchema;
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
import com.server.edu.mutual.dto.ElcMutualApplyDto;
import com.server.edu.mutual.service.ElcMutualApplyService;
import com.server.edu.mutual.vo.ElcMutualApplyVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "本研互选申请", version = ""))
@RestSchema(schemaId = "elcMutualApplyController")
@RequestMapping("elcMutualApply")
public class ElcMutualApplyController {
	private static Logger LOG =
	        LoggerFactory.getLogger(ElcMutualApplyController.class);
	@Autowired
	private ElcMutualApplyService elcMutualApplyService;
	
    /**
     * 获取已申请的互选课程列表
     * 
     * @param dto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "获取已申请的互选课程列表")
    @PostMapping("/page")
    public RestResult<PageInfo<ElcMutualApplyVo>> page(
    		@RequestBody PageCondition<ElcMutualApplyDto> dto)
        throws Exception
    {
        LOG.info("page.start");
        PageInfo<ElcMutualApplyVo> list = elcMutualApplyService.getElcMutualApplyList(dto);
        return RestResult.successData(list);
    }
    
  /**
     *获取学生可选的互选课程列表
     * 
     * @param dto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "获取学生可选的互选课程列表")
    @PostMapping("/getElcMutualCoursesForStu")
    public RestResult<PageInfo<ElcMutualApplyVo>> getElcMutualCoursesForStu2(
    		@RequestBody PageCondition<ElcMutualApplyDto> dto)
    				throws Exception
    {
    	LOG.info("getElcMutualCoursesForStu.start");
    	PageInfo<ElcMutualApplyVo> list = elcMutualApplyService.getElcMutualCoursesForStu(dto);
    	return RestResult.successData(list);
    }
    
    /**
     *学生申请互选
     * 
     * @param dto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "学生申请互选")
    @PostMapping("/apply")
    public RestResult<Integer> apply(
    		@RequestBody ElcMutualApplyDto dto)
        throws Exception
    {
        LOG.info("apply.start");
        int result =elcMutualApplyService.apply(dto);
        return RestResult.successData(result);
    }
    
    /**
     *学生取消申请
     * 
     * @param dto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "学生取消申请")
    @PostMapping("/cancel")
    public RestResult<Integer> cancel(
    		@RequestParam("id") @NotNull Long id)
        throws Exception
    {
        LOG.info("cancel.start");
        int result =elcMutualApplyService.cancel(id);
        return RestResult.successData(result);
    }
	

}
