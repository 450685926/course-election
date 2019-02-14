package com.server.edu.election.controller;

import javax.validation.Valid;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.entity.ElcLog;
import com.server.edu.election.service.ElcLogService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "选课日志", version = ""))
@RestSchema(schemaId = "ElcLogController")
@RequestMapping("elcLog")
public class ElcLogController
{
    @Autowired
    private ElcLogService service;
    
    /**
     * 选课日志列表
     * 
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "选课日志列表")
    @PostMapping("/page")
    public RestResult<PageResult<ElcLog>> list(
        @RequestBody @Valid PageCondition<ElcLog> condition)
        throws Exception
    {
        PageResult<ElcLog> list = service.listPage(condition);
        
        return RestResult.successData(list);
    }
}
