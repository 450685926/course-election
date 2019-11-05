package com.server.edu.election.controller;

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
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.dto.ElectionApplyDto;
import com.server.edu.election.entity.ElectionApply;
import com.server.edu.election.service.ElectionApplyService;
import com.server.edu.election.vo.ElectionApplyVo;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
@SwaggerDefinition(info = @Info(title = "选课申请管理", version = ""))
@RestSchema(schemaId = "ElectionApplyController")
@RequestMapping("electionApply")
public class ElectionApplyController {
	private static Logger LOG =
	        LoggerFactory.getLogger(ElectionApplyController.class);
	@Autowired
	private ElectionApplyService electionApplyService;
	 /**
     * 选课申请管理列表
     * 
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "选课申请管理列表")
    @PostMapping("/applyCourseList")
    public RestResult<PageInfo<ElectionApplyVo>> applyList(
        @RequestBody PageCondition<ElectionApplyDto> condition)
        throws Exception
    {
        LOG.info("applyList.start");
        PageInfo<ElectionApplyVo> list =electionApplyService.applyList(condition);
        return RestResult.successData(list);
    }
    
    @ApiOperation(value = "选课申请管理列表(未处理)")
    @PostMapping("/applyCourseList")
    public RestResult<PageResult<ElectionApplyVo>> applyUnList(
    		@RequestBody PageCondition<ElectionApplyDto> condition)
    				throws Exception
    {
    	LOG.info("applyList.start");
    	PageResult<ElectionApplyVo> list =electionApplyService.applyUnList(condition);
    	return RestResult.successData(list);
    }
    
    @ApiOperation(value = "选课申请管理列表(已处理)")
    @PostMapping("/applyCourseList")
    public RestResult<PageResult<ElectionApplyVo>> alreadyApplyList(
    		@RequestBody PageCondition<ElectionApplyDto> condition)
    				throws Exception
    {
    	LOG.info("applyList.start");
    	PageResult<ElectionApplyVo> list =electionApplyService.alreadyApplyList(condition);
    	return RestResult.successData(list);
    }
    
	 /**
     * 学生选课申请结果列表
     * 
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "学生选课申请结果列表")
    @PostMapping("/stuApplyCourseList")
    public RestResult<PageInfo<ElectionApplyVo>> stuApplyCourseList(
        @RequestBody PageCondition<ElectionApplyDto> condition)
        throws Exception
    {
        LOG.info("stuApplyCourseList.start");
        PageInfo<ElectionApplyVo> list =electionApplyService.stuApplyCourseList(condition);
        return RestResult.successData(list);
    }
    
    
    /**
     * 回复
     * 
     * @param dto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "回复")
    @PostMapping("/reply")
    public RestResult<Integer> reply(
    		@RequestBody  @Valid ElectionApply electionApply)
        throws Exception
    {
        LOG.info("reply.start");
        int result =electionApplyService.reply(electionApply);
        return RestResult.successData(result);
    }
	
	
    /**
     * 删除已处理申请
     * 
     * @param ids
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "删除已处理申请")
    @PostMapping("/delete")
    public RestResult<Integer> delete(
    		@RequestBody @NotNull Long calendarId)
        throws Exception
    {
        LOG.info("delete.start");
        int result =electionApplyService.delete(calendarId);
        return RestResult.successData(result);
    }
    
    /**
     * 审批同意
     * 
     * @param ids
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "审批同意")
    @PostMapping("/agree")
    public RestResult<Integer> agree(
    		@RequestBody @NotNull Long id)
        throws Exception
    {
        LOG.info("agree.start");
        int result =electionApplyService.agree(id);
        return RestResult.successData(result);
    }
    
    /**
     * 选课申请
     * 
     * @param ids
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "选课申请")
    @PostMapping("/apply")
    public RestResult<Integer> apply(
    		@RequestParam("studentId") @NotBlank String studentId,@RequestParam("roundId") @NotNull Long roundId,@RequestParam("courseCode") @NotBlank String courseCode)
        throws Exception
    {
        LOG.info("apply.start");
        int result =electionApplyService.apply(studentId,roundId,courseCode);
        return RestResult.successData(result);
    }
    
    /**
     * 取消申请
     * 
     * @param ids
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "取消申请")
    @PostMapping("/cancelApply")
    public RestResult<Integer> cancelApply(
    		@RequestParam("studentId") @NotBlank String studentId,@RequestParam("roundId") @NotNull Long roundId,@RequestParam("courseCode") @NotBlank String courseCode)
        throws Exception
    {
        LOG.info("cancelApply.start");
        int result =electionApplyService.cancelApply(studentId,roundId,courseCode);
        return RestResult.successData(result);
    }
    
    
	
}
