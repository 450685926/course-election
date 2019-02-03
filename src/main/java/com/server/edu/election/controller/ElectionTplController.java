package com.server.edu.election.controller;

import java.util.List;

import javax.validation.Valid;

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
import com.server.edu.election.dto.ElectionRuleDto;
import com.server.edu.election.dto.ElectionTplDto;
import com.server.edu.election.entity.ElectionRule;
import com.server.edu.election.service.ElectionTplService;
import com.server.edu.election.vo.ElectionRuleVo;
import com.server.edu.election.vo.ElectionTplVo;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "选课方案模板", version = ""))
@RestSchema(schemaId="ElectionTplController")
@RequestMapping("electionTpl")
public class ElectionTplController {
	private static Logger LOG =
	        LoggerFactory.getLogger("ElectionTplController");
	@Autowired
	private ElectionTplService electionTplService;
	 /**
     * 选课方案模板列表
     * 
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "选课方案模板列表")
    @PostMapping("/tplList")
    public RestResult<PageInfo<ElectionTplVo>> tplList(
        @RequestBody PageCondition<ElectionTplDto> condition)
        throws Exception
    {
        LOG.info("list.start");
        PageInfo<ElectionTplVo> tplList =electionTplService.list(condition);
        return RestResult.successData(tplList);
    }
    
    
    /**
     * 添加选课方案模板
     * 
     * @param dto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "添加选课方案模板")
    @PostMapping("/add")
    public RestResult<Integer> add(
        @RequestBody @Valid ElectionTplDto dto)
        throws Exception
    {
        LOG.info("add.start");
        int result =electionTplService.add(dto);
        return RestResult.successData(result);
    }
    
    /**
     * 修改选课方案模板
     * 
     * @param dto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "修改选课方案模板")
    @PostMapping("/update")
    public RestResult<Integer> update(
        @RequestBody @Valid ElectionTplDto dto)
        throws Exception
    {
        LOG.info("update.start");
        int result =electionTplService.update(dto);
        return RestResult.successData(result);
    }
    
    /**
     * 停用/启用选课方案模板
     * 
     * @param dto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "修改选课方案模板")
    @PostMapping("/updateStatus")
    public RestResult<Integer> updateStatus(
        @RequestBody @Valid ElectionTplDto dto)
        throws Exception
    {
        LOG.info("update.start");
        int result =electionTplService.updateStatus(dto);
        return RestResult.successData(result);
    
    }
    
    /**
     * 选课方案模板
     * 
     * @param electionRuleDto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "选课方案模板")
    @GetMapping("/getTpl")
    public RestResult<ElectionTplVo> getTpl(
        @RequestParam("id") Long id)
        throws Exception
    {
        LOG.info("getTpl.start");
        ElectionTplVo electionTplVo =electionTplService.getTpl(id);
        return RestResult.successData(electionTplVo);
    }
    
    /**
     * 删除选课方案模板
     * 
     * @param electionRuleDto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = " 删除选课方案模板")
    @GetMapping("/delete")
    public RestResult<ElectionTplVo> delete(
        @RequestParam("ids") List<Long> ids)
        throws Exception
    {
        LOG.info("getTpl.start");
        ElectionTplVo electionTplVo =electionTplService.delete(ids);
        return RestResult.successData(electionTplVo);
    }
}
