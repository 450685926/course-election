package com.server.edu.election.controller;

import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.log.LogRecord;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.dmskafka.entity.AuditType;
import com.server.edu.election.entity.RebuildCourseCharge;
import com.server.edu.election.service.RebuildCourseChargeService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @description: 重修管理
 * @author: bear
 * @create: 2019-01-31 19:11
 */

@SwaggerDefinition(info = @Info(title = "重修管理", version = ""))
@RestSchema(schemaId = "RebuildCourseController")
@RequestMapping("/rebuildCourse")
public class RebuildCourseController {

    @Autowired
    private RebuildCourseChargeService service;

    @ApiOperation(value = "查询重修收费信息")
    @PostMapping("/findCourseCharge")
    public RestResult<PageResult<RebuildCourseCharge>> findCourseCharge(@RequestBody PageCondition<RebuildCourseCharge> condition) {
        PageResult<RebuildCourseCharge> exemptionCourse = service.findCourseCharge(condition);
        return RestResult.successData(exemptionCourse);
    }

    @LogRecord(title="删除重修收费信息",type = AuditType.DELETE)
    @ApiOperation(value = "删除重修收费信息")
    @PostMapping("/deleteCourseCharge")
    public RestResult<String> deleteCourseCharge(@RequestBody List<Long> ids) {
        String s = service.deleteCourseCharge(ids);
        return RestResult.success(I18nUtil.getMsg(s,""));
    }


    @LogRecord(title="编辑重修收费信息",type = AuditType.UPDATE)
    @ApiOperation(value = "编辑重修收费信息")
    @PostMapping("/editCourseCharge")
    public RestResult<String> editCourseCharge(@RequestBody RebuildCourseCharge courseCharge) {
        String s = service.editCourseCharge(courseCharge);
        return RestResult.success(I18nUtil.getMsg(s,""));
    }


    @LogRecord(title="新增重修收费信息",type = AuditType.INSERT)
    @ApiOperation(value = "新增重修收费信息")
    @PostMapping("/addCourseCharge")
    public RestResult<String> addCourseCharge(@RequestBody RebuildCourseCharge courseCharge) {
        String s = service.addCourseCharge(courseCharge);
        return RestResult.success(I18nUtil.getMsg(s,""));
    }
}
