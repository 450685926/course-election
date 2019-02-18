package com.server.edu.election.controller;

import javax.validation.Valid;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.dto.ElcMedWithdrawRuleRefCourDto;
import com.server.edu.election.service.MedRuleRefCourService;
import com.server.edu.election.vo.ElcMedWithdrawRuleRefCourVo;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "期中退课白名单管理", version = ""))
@RestSchema(schemaId = "MedRuleRefCourController")
@RequestMapping("medRuleRef")
public class MedRuleRefCourController {
	private static Logger LOG =
	        LoggerFactory.getLogger(MedRuleRefCourController.class);
	@Autowired
	private MedRuleRefCourService medRuleRefCourService;
	
	 /**
     * 期中退课白名单列表
     * 
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "期中退课白名单列表")
    @PostMapping("/list")
    public RestResult<PageInfo<ElcMedWithdrawRuleRefCourVo>> list(
        @RequestBody PageCondition<ElcMedWithdrawRuleRefCourDto> condition)
        throws Exception
    {
        LOG.info("list.start");
        PageInfo<ElcMedWithdrawRuleRefCourVo> list =medRuleRefCourService.list(condition);
        return RestResult.successData(list);
    }
    
    /**
     * 添加退课白名单
     * 
     * @param elcMedWithdrawRuleRefCourDto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "添加退课白名单")
    @PostMapping("/add")
    public RestResult<Integer> add(
    		@RequestBody @Valid ElcMedWithdrawRuleRefCourDto elcMedWithdrawRuleRefCourDto)
        throws Exception
    {
        LOG.info("add.start");
        int result =medRuleRefCourService.add(elcMedWithdrawRuleRefCourDto);
        return RestResult.successData(result);
    }
    
    /**
     * 全量添加退课白名单
     * 
     * @param 
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "全量添加退课白名单")
    @PostMapping("/addAll")
    public RestResult<Integer> addAll(@RequestBody ElcMedWithdrawRuleRefCourDto dto)
        throws Exception
    {
        LOG.info("addAll.start");
        int result =medRuleRefCourService.addAll(dto);
        return RestResult.successData(result);
    }
    
    
    

}
