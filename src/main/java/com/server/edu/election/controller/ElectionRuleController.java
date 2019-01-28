package com.server.edu.election.controller;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "选课规则参数", version = ""))
@RestSchema(schemaId="ElectionRuleController")
@RequestMapping("electionRule")
public class ElectionRuleController
{
    
}
