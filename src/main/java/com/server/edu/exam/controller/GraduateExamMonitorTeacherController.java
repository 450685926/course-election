package com.server.edu.exam.controller;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.exam.entity.GraduateExamMonitorTeacher;
import com.server.edu.exam.service.GraduateExamMonitorTeacherService;
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
 * @description: 研究生排考监考老师数量
 * @author: bear
 * @create: 2019-08-27 10:44
 */

@SwaggerDefinition(info = @Info(title = "研究生排考监考老师数量", version = ""))
@RestSchema(schemaId = "GraduateExamMonitorTeacherController")
@RequestMapping("/graduateMonitorTeacher")
public class GraduateExamMonitorTeacherController {

    private static Logger LOG =
            LoggerFactory.getLogger(GraduateExamMonitorTeacherController.class);

    @Autowired
    private GraduateExamMonitorTeacherService monitorTeacherService;

    @PostMapping("/addGraduateMonitorTeacher")
    public RestResult<?> addGraduateMonitorTeacher(@RequestBody GraduateExamMonitorTeacher monitorTeacher) {
        monitorTeacherService.addGraduateMonitorTeacher(monitorTeacher);
        return RestResult.success();
    }

    @PostMapping("/editGraduateMonitorTeacher")
    public RestResult<?> editGraduateMonitorTeacher(@RequestBody GraduateExamMonitorTeacher monitorTeacher) {
        monitorTeacherService.editGraduateMonitorTeacher(monitorTeacher);
        return RestResult.success();
    }

    @PostMapping("/deleteGraduateMonitorTeacher")
    public RestResult<?> deleteGraduateMonitorTeacher(@RequestBody List<Long> ids) {
        monitorTeacherService.deleteGraduateMonitorTeacher(ids);
        return RestResult.success();
    }

    @PostMapping("/listGraduateMonitorTeacher")
    public RestResult<?> listeGraduateMonitorTeacher(@RequestBody PageCondition<GraduateExamMonitorTeacher> monitorTeacher) {
       PageResult<GraduateExamMonitorTeacher> pageResult = monitorTeacherService.listGraduateMonitorTeacher(monitorTeacher);
        return RestResult.successData(pageResult);
    }
}
