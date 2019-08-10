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
import com.server.edu.election.dto.ElcCouSubsDto;
import com.server.edu.election.entity.ElcCouSubs;
import com.server.edu.election.service.ElcCouSubsService;
import com.server.edu.election.vo.ElcCouSubsVo;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "替代课程", version = ""))
@RestSchema(schemaId = "ElcCouSubsController")
@RequestMapping("elcNoGradCouSubs")
public class ElcCouSubsController
{
    private static Logger LOG =
        LoggerFactory.getLogger(ElcCouSubsController.class);
    
    @Autowired
    private ElcCouSubsService elcNoGradCouSubsService;
    
    /**
    * 替代课程列表
    * 
    * @param condition
    * @return
    * @see [类、类#方法、类#成员]
    */
    @ApiOperation(value = "替代课程列表")
    @PostMapping("/page")
    public RestResult<PageInfo<ElcCouSubsVo>> page(
        @RequestBody PageCondition<ElcCouSubsDto> condition)
        throws Exception
    {
        LOG.info("page.start");
        PageInfo<ElcCouSubsVo> list =
            elcNoGradCouSubsService.page(condition);
        return RestResult.successData(list);
    }
    
    /**
     * 新增
     * 
     * @param elcCouSubs
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "新增")
    @PostMapping("/add")
    public RestResult<Integer> add(
        @RequestBody @Valid ElcCouSubs elcCouSubs)
        throws Exception
    {
        LOG.info("start.start");
        int result = elcNoGradCouSubsService.add(elcCouSubs);
        return RestResult.successData(result);
    }
    
    /**
     * 修改
     * 
     * @param elcCouSubs
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "修改")
    @PostMapping("/update")
    public RestResult<Integer> update(
        @RequestBody @Valid ElcCouSubs elcCouSubs)
        throws Exception
    {
        LOG.info("update.start");
        int result = elcNoGradCouSubsService.update(elcCouSubs);
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
    public RestResult<Integer> delete(@RequestBody @NotEmpty List<Long> ids)
        throws Exception
    {
        LOG.info("update.start");
        int result = elcNoGradCouSubsService.delete(ids);
        return RestResult.successData(result);
    }
    
}
