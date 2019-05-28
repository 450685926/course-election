package com.server.edu.election.controller;

import java.util.List;

import javax.validation.Valid;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.entity.ElcNoGradCouSubs;
import com.server.edu.election.service.ElcNoGradCouSubsService;
import com.server.edu.election.vo.ElcNoGradCouSubsVo;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
@SwaggerDefinition(info = @Info(title = "结业生替代课程", version = ""))
@RestSchema(schemaId = "ElcNoGradCouSubsController")
@RequestMapping("elcNoGradCouSubs")
public class ElcNoGradCouSubsController {
	private static Logger LOG =
	        LoggerFactory.getLogger(ElcNoGradCouSubsController.class);
	@Autowired
	private ElcNoGradCouSubsService elcNoGradCouSubsService;
	 /**
     * 建议课表列表
     * 
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "替代课程列表")
    @PostMapping("/page")
    public RestResult<PageInfo<ElcNoGradCouSubsVo>> page(
        @RequestBody PageCondition<ElcNoGradCouSubs> condition)
        throws Exception
    {
        LOG.info("page.start");
        PageInfo<ElcNoGradCouSubsVo> list =elcNoGradCouSubsService.page(condition);
        return RestResult.successData(list);
    }
    
    /**
     * 新增
     * 
     * @param elcNoGradCouSubs
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "新增")
    @PostMapping("/add")
    public RestResult<Integer> add(
    		@RequestBody  @Valid ElcNoGradCouSubs elcNoGradCouSubs)
        throws Exception
    {
        LOG.info("start.start");
        int result =elcNoGradCouSubsService.add(elcNoGradCouSubs);
        return RestResult.successData(result);
    }
	
	
    /**
     * 修改
     * 
     * @param elcNoGradCouSubs
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "修改")
    @PostMapping("/update")
    public RestResult<Integer> update(
    		@RequestBody  @Valid ElcNoGradCouSubs elcNoGradCouSubs)
        throws Exception
    {
        LOG.info("update.start");
        int result =elcNoGradCouSubsService.update(elcNoGradCouSubs);
        return RestResult.successData(result);
    }
    
    /**
     * 删除
     * 
     * @param courses
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "修改")
    @PostMapping("/delete")
    public RestResult<Integer> delete(
    		@RequestBody  @NotEmpty List<Long> ids)
        throws Exception
    {
        LOG.info("update.start");
        int result =elcNoGradCouSubsService.delete(ids);
        return RestResult.successData(result);
    }
    
	
}
