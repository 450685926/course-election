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

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.dto.ElcMedWithdrawApplyDto;
import com.server.edu.election.entity.ApprovalInfo;
import com.server.edu.election.service.ElcMedWithdrawApplyService;
import com.server.edu.election.vo.ElcCourseTakeVo;
import com.server.edu.election.vo.ElcMedWithdrawApplyVo;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
@SwaggerDefinition(info = @Info(title = "期中退课审批", version = ""))
@RestSchema(schemaId = "ElcMedWithdrawApplyController")
@RequestMapping("elcMedWithdrawApply")
public class ElcMedWithdrawApplyController {
	private static Logger LOG =
	        LoggerFactory.getLogger(ElcMedWithdrawRulesController.class);
	@Autowired
	private ElcMedWithdrawApplyService elcMedWithdrawApplyService;
	 /**
     * 期中退课申请列表
     * 
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "期中退课申请列表")
    @PostMapping("/applyList")
    public RestResult<PageInfo<ElcCourseTakeVo>> applyList(
        @RequestBody PageCondition<ElcMedWithdrawApplyDto> condition)
        throws Exception
    {
        LOG.info("list.start");
        PageInfo<ElcCourseTakeVo> list =elcMedWithdrawApplyService.applyList(condition);
        return RestResult.successData(list);
    }
    
	 /**
     * 期中退课记录列表
     * 
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "期中退课记录列表")
    @PostMapping("/applyLogs")
    public RestResult<PageInfo<ElcMedWithdrawApplyVo>> applyLogs(
        @RequestBody PageCondition<ElcMedWithdrawApplyDto> condition)
        throws Exception
    {
        LOG.info("list.start");
        PageInfo<ElcMedWithdrawApplyVo> list =elcMedWithdrawApplyService.applyLogs(condition);
        return RestResult.successData(list);
    }
    
    /**
     * 期中退课申请
     * 
     * @param ids
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "期中退课申请")
    @PostMapping("/apply")
    public RestResult<Integer> apply(
    		@RequestParam("id") @NotNull Long id,@RequestParam("projectId") @NotNull Integer projectId)
        throws Exception
    {
        LOG.info("approval.start");
        int result =elcMedWithdrawApplyService.apply(id,projectId);
        return RestResult.successData(result);
    }
	
	
	 /**
     * 期中退课审批列表
     * 
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = " 期中退课审批列表")
    @PostMapping("/list")
    public RestResult<PageInfo<ElcMedWithdrawApplyVo>> list(
        @RequestBody PageCondition<ElcMedWithdrawApplyDto> condition)
        throws Exception
    {
        LOG.info("list.start");
        PageInfo<ElcMedWithdrawApplyVo> list =elcMedWithdrawApplyService.list(condition);
        return RestResult.successData(list);
    }
    
    /**
     * 期中退课审批
     * 
     * @param ids
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "期中退课审批")
    @PostMapping("/approval")
    public RestResult<String> approval(
    		@RequestBody @Valid ApprovalInfo approvalInfo)
        throws Exception
    {
        LOG.info("approval.start");
        String result =elcMedWithdrawApplyService.approval(approvalInfo);
        return RestResult.successData(result);
    }
	
	
}
