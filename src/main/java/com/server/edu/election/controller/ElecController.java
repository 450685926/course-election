package com.server.edu.election.controller;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.server.edu.common.rest.RestResult;

import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "选课", version = ""))
@RestSchema(schemaId = "ElecController")
@RequestMapping("election")
public class ElecController
{
    /**
     * 检查数据准备状态
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    @PostMapping("checkStatus")
    public RestResult<?> checkStatus()
    {
        
        return RestResult.success();
    }
}
