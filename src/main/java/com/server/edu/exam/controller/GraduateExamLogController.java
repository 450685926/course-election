package com.server.edu.exam.controller;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.exam.query.GraduateExamLogQuery;
import com.server.edu.exam.service.GraduateExamLogService;
import com.server.edu.exam.vo.GraduateExamLogVo;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @description: 研究生排考日志
 * @author: bear
 * @create: 2019-08-28 09:36
 */

@SwaggerDefinition(info = @Info(title = "研究生排考日志", version = ""))
@RestSchema(schemaId = "GraduateExamLogController")
@RequestMapping("/graduateLog")
public class GraduateExamLogController {
    private static Logger LOG =
            LoggerFactory.getLogger(GraduateExamLogController.class);

    @Autowired
    private GraduateExamLogService logService;

    @PostMapping("/listGraduateExamLog")
    public RestResult<PageResult<GraduateExamLogVo>> listGraduateExamLog(@RequestBody PageCondition<GraduateExamLogQuery> logVo){
        PageResult<GraduateExamLogVo> pageResult = logService.listGraduateExamLog(logVo);
        return RestResult.successData(pageResult);
    }
}
