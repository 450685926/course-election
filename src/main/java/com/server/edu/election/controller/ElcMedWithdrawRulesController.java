package com.server.edu.election.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.hibernate.validator.constraints.NotEmpty;
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
import com.server.edu.election.entity.ElcMedWithdrawRules;
import com.server.edu.election.service.ElcMedWithdrawRulesService;
import com.server.edu.election.vo.ElcMedWithdrawRulesVo;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "退课规则", version = ""))
@RestSchema(schemaId = "ElcMedWithdrawRulesController")
@RequestMapping("elcMedWithdrawRules")
public class ElcMedWithdrawRulesController {
	private static Logger LOG =
	        LoggerFactory.getLogger(ElcMedWithdrawRulesController.class);
	@Autowired
	private ElcMedWithdrawRulesService elcMedWithdrawRulesService;
	
	 /**
     * 退课规则列表
     * 
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "退课规则列表")
    @PostMapping("/list")
    public RestResult<PageInfo<ElcMedWithdrawRules>> list(
        @RequestBody PageCondition<ElcMedWithdrawRules> condition)
        throws Exception
    {
        LOG.info("list.start");
        PageInfo<ElcMedWithdrawRules> list =elcMedWithdrawRulesService.list(condition);
        return RestResult.successData(list);
    }
    
    /**
     * 添加退课规则
     * 
     * @param elcMedWithdrawRules
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "添加退课规则")
    @PostMapping("/add")
    public RestResult<Integer> add(
    		@RequestBody @Valid ElcMedWithdrawRules elcMedWithdrawRules)
        throws Exception
    {
        LOG.info("add.start");
        int result =elcMedWithdrawRulesService.add(elcMedWithdrawRules);
        return RestResult.successData(result);
    }
    
    /**
     * 修改退课规则
     * 
     * @param elcMedWithdrawRules
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "修改退课规则")
    @PostMapping("/update")
    public RestResult<Integer> update(
    		@RequestBody @Valid ElcMedWithdrawRules elcMedWithdrawRules)
        throws Exception
    {
        LOG.info("add.start");
        int result =elcMedWithdrawRulesService.update(elcMedWithdrawRules);
        return RestResult.successData(result);
    }
    
    /**
     * 删除退课规则
     * 
     * @param ids
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "删除退课规则")
    @PostMapping("/delete")
    public RestResult<Integer> delete(
    		@RequestBody @NotEmpty List<Long> ids)
        throws Exception
    {
        LOG.info("delete.start");
        int result =elcMedWithdrawRulesService.delete(ids);
        return RestResult.successData(result);
    }
    
    /**
     * 退课规则
     * 
     * @param id
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "退课规则")
    @GetMapping("/getRule")
    public RestResult<ElcMedWithdrawRulesVo> getRule(
        @RequestParam("id") @NotNull Long id)
        throws Exception
    {
        LOG.info("getRule.start");
        ElcMedWithdrawRulesVo elcMedWithdrawRules =elcMedWithdrawRulesService.getRule(id);
        return RestResult.successData(elcMedWithdrawRules);
    }
    
}
