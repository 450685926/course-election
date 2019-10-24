package com.server.edu.exam.controller;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.exam.entity.GraduateExamMakeUpAuth;
import com.server.edu.exam.service.GraduateExamMakeUpAuthService;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @description: 研究生补缓考权限管理
 * @author: bear
 * @create: 2019-08-28 11:24
 */

@SwaggerDefinition(info = @Info(title = "研究生补缓考权限管理", version = ""))
@RestSchema(schemaId = "GraduateExamMakeUpAuthController")
@RequestMapping("/graduateMakeUpExamAuth")
public class GraduateExamMakeUpAuthController {
    private static Logger LOG =
            LoggerFactory.getLogger(GraduateExamMakeUpAuthController.class);

    @Autowired
    private GraduateExamMakeUpAuthService makeUpAuthService;

    @PostMapping("/addMakeUpAuth")
    public RestResult<?> addMakeUpAuth(@RequestBody GraduateExamMakeUpAuth makeUpAuth){
        makeUpAuthService.addMakeUpExamAuth(makeUpAuth);
       return RestResult.success();
    }

    @PostMapping("/editMakeUpAuth")
    public RestResult<?> editMakeUpAuth(@RequestBody GraduateExamMakeUpAuth makeUpAuth){
        makeUpAuthService.editMakeUpExamAuth(makeUpAuth);
        return RestResult.success();
    }

    @PostMapping("/deleteMakeUpAuth")
    public RestResult<?> deleteMakeUpAuth(@RequestBody List<Long> ids){
        makeUpAuthService.deleteMakeUpExamAuth(ids);
        return RestResult.success();
    }

    @PostMapping("/listMakeUpAuth")
    public RestResult<PageResult<GraduateExamMakeUpAuth>> listMakeUpAuth(@RequestBody PageCondition<GraduateExamMakeUpAuth> makeUpAuth){
        PageResult<GraduateExamMakeUpAuth> pageResult = makeUpAuthService.listMakeUpExamAuth(makeUpAuth);
        return RestResult.successData(pageResult);
    }
}
