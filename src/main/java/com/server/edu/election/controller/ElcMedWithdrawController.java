package com.server.edu.election.controller;

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
import com.server.edu.election.entity.ElcMedWithdraw;
import com.server.edu.election.service.ElcMedWithdrawService;
import com.server.edu.election.vo.ElcCourseTakeVo;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
@SwaggerDefinition(info = @Info(title = "期中退课", version = ""))
@RestSchema(schemaId = "ElcMedWithdrawController")
@RequestMapping("elcMedWithdraw")
public class ElcMedWithdrawController {
	private static Logger LOG =
	        LoggerFactory.getLogger(ElcMedWithdrawRulesController.class);
	@Autowired
	private ElcMedWithdrawService elcMedWithdrawService;
	 /**
     * 期中退课列表
     * 
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "期中退课列表")
    @PostMapping("/page")
    public RestResult<PageInfo<ElcCourseTakeVo>> page(
        @RequestBody PageCondition<ElcMedWithdraw> condition)
        throws Exception
    {
        LOG.info("list.start");
        PageInfo<ElcCourseTakeVo> list =elcMedWithdrawService.page(condition);
        return RestResult.successData(list);
    }
    
    
    /**
     * 期中退课
     * 
     * @param id,projectId
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "期中退课申请")
    @PostMapping("/medWithdraw")
    public RestResult<Integer> medWithdraw(
    		@RequestParam("id") @NotNull Long id,@RequestParam("projectId") @NotBlank String projectId)
        throws Exception
    {
        LOG.info("medWithdraw.start");
        int result =elcMedWithdrawService.medWithdraw(id,projectId);
        return RestResult.successData(result);
    }
    
    /**
     * 取消期中退课
     * 
     * @param id
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "取消期中退课")
    @PostMapping("/cancelMedWithdraw")
    public RestResult<Integer> cancelMedWithdraw(
    		@RequestParam("id") @NotNull Long id,@RequestParam("projectId") @NotBlank String projectId)
        throws Exception
    {
        LOG.info("medWithdraw.start");
        int result =elcMedWithdrawService.cancelMedWithdraw(id,projectId);
        return RestResult.successData(result);
    }
}
