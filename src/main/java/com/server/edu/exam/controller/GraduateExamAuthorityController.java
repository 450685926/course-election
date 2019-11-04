package com.server.edu.exam.controller;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.exam.service.GraduateExamAuthService;
import com.server.edu.exam.vo.GraduateExamAuthVo;
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
 * @description: 研究生排考权限管理
 * @author: bear
 * @create: 2019-08-26 14:28
 */

@SwaggerDefinition(info = @Info(title = "研究生排考权限管理", version = ""))
@RestSchema(schemaId = "GraduateExamAuthorityController")
@RequestMapping("/graduateAuth")
public class GraduateExamAuthorityController {
    private static Logger LOG =
            LoggerFactory.getLogger(GraduateExamAuthorityController.class);

    @Autowired
    private GraduateExamAuthService authService;

    @PostMapping("/addGraduateExamAuth")
    public RestResult<?> addGraduateAuth(@RequestBody GraduateExamAuthVo examAuthVo) {
        authService.addGraduateAuth(examAuthVo);
        return RestResult.success();
    }

    @PostMapping("/editGraduateExamAuth")
    public RestResult<?> editGraduateExamAuth(@RequestBody GraduateExamAuthVo examAuthVo) {
        authService.editGraduateAuth(examAuthVo);
        return RestResult.success();
    }

    @PostMapping("/deleteGraduateExamAuth")
    public RestResult<?> deleteGraduateExamAuth(@RequestBody List<Long> ids) {
        authService.deleteGraduateAuth(ids);
        return RestResult.success();
    }

    @PostMapping("/findGraduateExamAuth")
    public RestResult<PageResult<GraduateExamAuthVo>> listGraduateExamAuth(@RequestBody PageCondition<GraduateExamAuthVo> examAuthVo) {
        PageResult<GraduateExamAuthVo> page = authService.listGraduateExamAuth(examAuthVo);
        return RestResult.successData(page);
    }

}
