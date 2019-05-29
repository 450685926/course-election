package com.server.edu.election.controller;

import java.io.File;
import java.util.List;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dto.RebuildCourseDto;
import com.server.edu.election.dto.StudentRePaymentDto;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.excel.export.ExcelResult;
import com.server.edu.util.excel.export.ExportExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.log.LogRecord;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.dmskafka.entity.AuditType;
import com.server.edu.election.entity.RebuildCourseCharge;
import com.server.edu.election.entity.RebuildCourseNoChargeType;
import com.server.edu.election.service.RebuildCourseChargeService;
import com.server.edu.election.vo.RebuildCourseNoChargeList;
import com.server.edu.election.vo.StudentVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

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
    public RestResult<PageResult<RebuildCourseNoChargeList>> findCourseNoChargeList(@RequestBody PageCondition<RebuildCourseDto> condition) {
        if(condition.getCondition().getCalendarId()==null ||condition.getCondition().getMode()==null){
            return RestResult.fail(I18nUtil.getMsg("baseresservice.parameterError"));
        }
        PageResult<RebuildCourseNoChargeList> noChargeType = service.findCourseNoChargeList(condition);
        return RestResult.successData(noChargeType);
    }

    @ApiOperation(value = "查询学生的未缴费课程数")
    @PostMapping("/findCourseNoChargeStudentList")
    public RestResult<PageResult<StudentVo>> findCourseNoChargeStudentList(@RequestBody PageCondition<RebuildCourseDto > condition) {
        if(condition.getCondition().getCalendarId()==null ||condition.getCondition().getMode()==null){
            return RestResult.fail(I18nUtil.getMsg("baseresservice.parameterError"));
        }
        PageResult<StudentVo> noChargeType = service.findCourseNoChargeStudentList(condition);
        return RestResult.successData(noChargeType);
    }


    @ApiOperation(value = "移动到回收站")
    @PostMapping("/moveCourseNoChargeListToRecycle")
    public RestResult<String> moveCourseNoChargeListToRecycle(@RequestBody List<RebuildCourseNoChargeList> list) {
        String s = service.moveToRecycle(list);
        return RestResult.success(I18nUtil.getMsg(s,""));
    }

    @ApiOperation(value = "查询回收站")
    @PostMapping("/findRecycleCourse")
    public RestResult<PageResult<RebuildCourseNoChargeList>> findRecycleCourse(@RequestBody PageCondition<RebuildCourseDto > condition) {
        if(condition.getCondition().getCalendarId()==null ||condition.getCondition().getMode()==null){
            return RestResult.fail(I18nUtil.getMsg("baseresservice.parameterError"));
        }
        PageResult<RebuildCourseNoChargeList> noChargeType = service.findRecycleCourse(condition);
        return RestResult.successData(noChargeType);
    }

    @ApiOperation(value = "从回收站恢复到未缴费的课程名单")
    @PostMapping("/moveRecycleCourseToNoChargeList")
    public RestResult<String> moveRecycleCourseToNoChargeList(@RequestBody List<RebuildCourseNoChargeList> list) {
        String s = service.moveRecycleCourseToNoChargeList(list);
        return RestResult.success(I18nUtil.getMsg(s,""));
    }

    @ApiOperation(value = "导出重修汇总名单")
    @PostMapping("/exportStuNumber")
    public RestResult<ExcelResult> exportStuNumber(@RequestBody RebuildCourseDto condition)
            throws Exception {
        LOG.info("exportStuNumber.start");
        ExcelResult result = service.exportStuNumber(condition);
        return RestResult.successData(result);
    }

    @ApiOperation(value = "导出重修缴费名单")
    @PostMapping("/export")
    public RestResult<ExcelResult> export(@RequestBody RebuildCourseDto condition)
            throws Exception {
        LOG.info("export.start");
        ExcelResult result = service.export(condition);
        return RestResult.successData(result);
    }


    @ApiOperation(value = "导出回收站名单")
    @PostMapping("/exportRecycle")
    public RestResult<ExcelResult> exportRecycle(@RequestBody RebuildCourseDto condition)
            throws Exception {
        LOG.info("exportRecycle.start");
        ExcelResult result = service.exportRecycle(condition);
        return RestResult.successData(result);
    }

    /**
     * @Description: 根据key循环去redis取数据
     */
    @GetMapping("result/{key}")
    public RestResult<?> getResultByKey(@PathVariable("key") @NotBlank String key) {
        ExcelResult excelResult = ExportExcelUtils.getResultByKey(key);
        return RestResult.successData(excelResult);
    }

    /**
     * @Description: 下载
     */
    @ApiOperation(value = "导出excel下载文件")
    @GetMapping("/download")
    @ApiResponses({@ApiResponse(code = 200, response = File.class, message = "导出excel下载文件")})
    public ResponseEntity<Resource> download(@RequestParam("path") String path)
            throws Exception {
        return ExportExcelUtils.export(path);
    }

    @ApiOperation(value = "学生重修缴费明细")
    @PostMapping("/findStuRePayment")
    public RestResult<List<StudentRePaymentDto>> findStuRePayment(@RequestBody StudentRePaymentDto studentRePaymentDto){
        if(studentRePaymentDto.getCalendarId()==null){
            return RestResult.fail(I18nUtil.getMsg("baseresservice.parameterError"));
        }
        String studentCode = SessionUtils.getCurrentSession().realUid();
        studentRePaymentDto.setStudentCode(studentCode);
        List<StudentRePaymentDto> list=service.findStuRePayment(studentRePaymentDto);
        return RestResult.successData(list);
    }

}
