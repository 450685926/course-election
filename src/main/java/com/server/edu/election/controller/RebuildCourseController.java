package com.server.edu.election.controller;

import java.io.File;
import java.net.URLDecoder;
import java.util.List;

import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.rpc.BaseresServiceInvoker;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.log.LogRecord;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.dmskafka.entity.AuditType;
import com.server.edu.election.dto.RebuildCoursePaymentCondition;
import com.server.edu.election.entity.RebuildCourseCharge;
import com.server.edu.election.vo.RebuildCourseNoChargeList;
import com.server.edu.election.entity.RebuildCourseNoChargeType;
import com.server.edu.election.service.RebuildCourseChargeService;
import com.server.edu.election.vo.StudentVo;

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

    private static Logger LOG =
            LoggerFactory.getLogger(ExemptionController.class);

    @Value("${cache.directory}")
    private String cacheDirectory;

    @ApiOperation(value = "查询重修收费信息")
    @PostMapping("/findCourseCharge")
    public RestResult<PageResult<RebuildCourseCharge>> findCourseCharge(@RequestBody PageCondition<RebuildCourseCharge> condition) {
        Session currentSession = SessionUtils.getCurrentSession();
        String dptId = currentSession.getCurrentManageDptId();
        if(StringUtils.isBlank(dptId)){
            return RestResult.fail(I18nUtil.getMsg("baseresservice.projIdNotExtist"));
        }
        if(Constants.PROJ_LINE_GRADUATE.equals(dptId)){
            return null;
        }
        condition.getCondition().setManageDeptId(dptId);
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
        if(StringUtils.isBlank(courseCharge.getFormLearning())
                ||StringUtils.isBlank(courseCharge.getTrainingLevel())
                ||courseCharge.getIsCharge()==null){
            return RestResult.fail(I18nUtil.getMsg("baseresservice.parameterError "));
        }
        String s = service.editCourseCharge(courseCharge);
        return RestResult.success(I18nUtil.getMsg(s,""));
    }


    @LogRecord(title="新增重修收费信息",type = AuditType.INSERT)
    @ApiOperation(value = "新增重修收费信息")
    @PostMapping("/addCourseCharge")
    public RestResult<String> addCourseCharge(@RequestBody RebuildCourseCharge courseCharge) {
        if(StringUtils.isBlank(courseCharge.getFormLearning())
                ||StringUtils.isBlank(courseCharge.getTrainingLevel())
                ||courseCharge.getIsCharge()==null){
            return RestResult.fail(I18nUtil.getMsg("baseresservice.parameterError "));
        }
        String s = service.addCourseCharge(courseCharge);
        return RestResult.success(I18nUtil.getMsg(s,""));
    }


    @ApiOperation(value = "查询重修不收费学生类型")
    @PostMapping("/findCourseNoChargeType")
    public RestResult<PageResult<RebuildCourseNoChargeType>> findCourseNoChargeType(@RequestBody PageCondition<RebuildCourseNoChargeType> condition) {
        PageResult<RebuildCourseNoChargeType> noChargeType = service.findCourseNoChargeType(condition);
        return RestResult.successData(noChargeType);
    }


    @LogRecord(title="新增重修不收费学生类型",type = AuditType.INSERT)
    @ApiOperation(value = "新增重修不收费学生类型")
    @PostMapping("/addCourseNoChargeType")
    public RestResult<String> addCourseNoChargeType(@RequestBody RebuildCourseNoChargeType condition) {
        String s = service.addCourseNoChargeType(condition);
        return RestResult.success(I18nUtil.getMsg(s,""));
    }


    @LogRecord(title="删除重修不收费学生类型",type = AuditType.DELETE)
    @ApiOperation(value = "删除重修不收费学生类型")
    @PostMapping("/deleteCourseNoChargeType")
    public RestResult<String> deleteCourseNoChargeType(@RequestBody List<Long> ids) {
        String s = service.deleteCourseNoChargeType(ids);
        return RestResult.success(I18nUtil.getMsg(s,""));
    }

    @LogRecord(title="编辑重修不收费学生类型",type = AuditType.UPDATE)
    @ApiOperation(value = "编辑重修不收费学生类型")
    @PostMapping("/editCourseNoChargeType")
    public RestResult<String> editCourseNoChargeType(@RequestBody RebuildCourseNoChargeType courseNoCharge) {
        String s = service.editCourseNoChargeType(courseNoCharge);
        return RestResult.success(I18nUtil.getMsg(s,""));
    }



    @ApiOperation(value = "查询未缴费的课程名单")
    @PostMapping("/findCourseNoChargeList")
    public RestResult<PageResult<RebuildCourseNoChargeList>> findCourseNoChargeList(@RequestBody PageCondition<RebuildCoursePaymentCondition > condition) {
        PageResult<RebuildCourseNoChargeList> noChargeType = service.findCourseNoChargeList(condition);
        return RestResult.successData(noChargeType);
    }

    @ApiOperation(value = "查询学生的未缴费课程数")
    @PostMapping("/findCourseNoChargeStudentList")
    public RestResult<PageResult<StudentVo>> findCourseNoChargeStudentList(@RequestBody PageCondition<RebuildCoursePaymentCondition > condition) {
        PageResult<StudentVo> noChargeType = service.findCourseNoChargeStudentList(condition);
        return RestResult.successData(noChargeType);
    }

    //导入未缴费todo

    @ApiOperation(value = "移动到回收站")
    @PostMapping("/moveCourseNoChargeListToRecycle")
    public RestResult<String> moveCourseNoChargeListToRecycle(@RequestBody List<RebuildCourseNoChargeList> list) {
        String s = service.moveToRecycle(list);
        return RestResult.success(I18nUtil.getMsg(s,""));
    }

    @ApiOperation(value = "查询回收站")
    @PostMapping("/findRecycleCourse")
    public RestResult<PageResult<RebuildCourseNoChargeList>> findRecycleCourse(@RequestBody PageCondition<RebuildCoursePaymentCondition > condition) {
        PageResult<RebuildCourseNoChargeList> noChargeType = service.findRecycleCourse(condition);
        return RestResult.successData(noChargeType);
    }

    @ApiOperation(value = "从回收站恢复到未缴费的课程名单")
    @PostMapping("/moveRecycleCourseToNoChargeList")
    public RestResult<String> moveRecycleCourseToNoChargeList(@RequestBody List<RebuildCourseNoChargeList> list) {
        String s = service.moveRecycleCourseToNoChargeList(list);
        return RestResult.success(I18nUtil.getMsg(s,""));
    }


    @ApiOperation(value = "导出未缴费课程名单")
    @PostMapping("/exportNoChargeList")
    public RestResult<String> exportNoChargeList (
            @RequestBody RebuildCoursePaymentCondition condition)
            throws Exception
    {
        LOG.info("export.start");
        String export = service.exportNoChargeList(condition);
        return RestResult.successData(export);
    }

    @ApiOperation(value = "导出学生课程汇总名单")
    @PostMapping("/exportStudentNoChargeCourse")
    public RestResult<String> exportStudentNoChargeCourse (
            @RequestBody RebuildCoursePaymentCondition condition)
            throws Exception
    {
        LOG.info("export.start");
        String export = service.exportStudentNoChargeCourse(condition);
        return RestResult.successData(export);
    }



    @ApiOperation(value = "导出excel下载文件")
    @GetMapping("/download")
    @ApiResponses({@ApiResponse(code = 200, response = File.class, message = "导出excel下载文件")})
    public ResponseEntity<Resource> download(@RequestParam("fileName") String fileName) throws Exception
    {
        LOG.info("export.start");
        fileName = new String(fileName.getBytes(), "ISO8859-1");
        Resource resource = new FileSystemResource(URLDecoder.decode(cacheDirectory + fileName,"utf-8"));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/vnd.ms-excel")
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment;filename*=UTF-8''"+URLDecoder.decode(fileName,"utf-8"))
                .body(resource);
    }
}
