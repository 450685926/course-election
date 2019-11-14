package com.server.edu.election.controller;

import com.alibaba.fastjson.JSONObject;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.log.LogRecord;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.validator.Assert;
import com.server.edu.common.validator.ValidatorUtil;
import com.server.edu.dmskafka.entity.AuditType;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dto.RebuildCourseDto;
import com.server.edu.election.dto.StudentRePaymentDto;
import com.server.edu.election.entity.RebuildCourseCharge;
import com.server.edu.election.entity.RebuildCourseNoChargeType;
import com.server.edu.election.service.ElcCourseTakeService;
import com.server.edu.election.service.RebuildCourseChargeService;
import com.server.edu.election.vo.RebuildCourseNoChargeList;
import com.server.edu.election.vo.RebuildCourseNoChargeTypeVo;
import com.server.edu.election.vo.StudentVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.CollectionUtil;
import com.server.edu.util.ExportUtil;
import com.server.edu.util.excel.ExcelWriterUtil;
import com.server.edu.util.excel.export.ExcelResult;
import com.server.edu.util.excel.export.ExportExcelUtils;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 重修管理
 * @author: bear
 * @create: 2019-01-31 19:11
 */

@SwaggerDefinition(info = @Info(title = "重修管理", version = ""))
@RestSchema(schemaId = "RebuildCourseController")
@RequestMapping("/rebuildCourse")
public class RebuildCourseController
{

    @Autowired
    private RebuildCourseChargeService service;

    @Autowired
    private ElcCourseTakeService courseTakeService;
    
    private static Logger LOG =
        LoggerFactory.getLogger(ExemptionController.class);
    
    @Value("${cache.directory}")
    private String cacheDirectory;
    
    @ApiOperation(value = "查询重修收费信息")
    @PostMapping("/findCourseCharge")
    public RestResult<PageResult<RebuildCourseCharge>> findCourseCharge(
        @RequestBody PageCondition<RebuildCourseCharge> condition)
    {
        Session currentSession = SessionUtils.getCurrentSession();
        String dptId = currentSession.getCurrentManageDptId();
        if (StringUtils.isBlank(dptId))
        {
            throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.projIdNotExtist"));
        }
        if (Constants.PROJ_LINE_GRADUATE.equals(dptId))
        {
            return null;
        }
        condition.getCondition().setManageDeptId(dptId);
        PageResult<RebuildCourseCharge> exemptionCourse =
            service.findCourseCharge(condition);
        return RestResult.successData(exemptionCourse);
    }
    
    @LogRecord(title = "删除重修收费信息", type = AuditType.DELETE)
    @ApiOperation(value = "删除重修收费信息")
    @PostMapping("/deleteCourseCharge")
    public RestResult<String> deleteCourseCharge(@RequestBody List<Long> ids)
    {
        service.deleteCourseCharge(ids);
        return RestResult.success();
    }
    
    @LogRecord(title = "编辑重修收费信息", type = AuditType.UPDATE)
    @ApiOperation(value = "编辑重修收费信息")
    @PostMapping("/editCourseCharge")
    public RestResult<?> editCourseCharge(
        @RequestBody RebuildCourseCharge courseCharge)
    {
        if (StringUtils.isBlank(courseCharge.getFormLearning())
            || StringUtils.isBlank(courseCharge.getTrainingLevel())
            || courseCharge.getIsCharge() == null)
        {
            throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError "));
        }
        service.editCourseCharge(courseCharge);
        return RestResult.success();
    }
    
    @LogRecord(title = "新增重修收费信息", type = AuditType.INSERT)
    @ApiOperation(value = "新增重修收费信息")
    @PostMapping("/addCourseCharge")
    public RestResult<?> addCourseCharge(
        @RequestBody RebuildCourseCharge courseCharge)
    {
        if (StringUtils.isBlank(courseCharge.getFormLearning())
            || StringUtils.isBlank(courseCharge.getTrainingLevel())
            || courseCharge.getIsCharge() == null)
        {
            throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError "));
        }
        service.addCourseCharge(courseCharge);
        return RestResult.success();
    }
    
    @ApiOperation(value = "查询重修不收费学生类型")
    @PostMapping("/findCourseNoChargeType")
    public RestResult<PageResult<RebuildCourseNoChargeType>> findCourseNoChargeType(
        @RequestBody PageCondition<RebuildCourseNoChargeTypeVo> condition)
    {
        PageResult<RebuildCourseNoChargeType> noChargeType =
            service.findCourseNoChargeType(condition);
        return RestResult.successData(noChargeType);
    }
    
    @LogRecord(title = "新增重修不收费学生类型", type = AuditType.INSERT)
    @ApiOperation(value = "新增重修不收费学生类型")
    @PostMapping("/addCourseNoChargeType")
    public RestResult<?> addCourseNoChargeType(
        @RequestBody RebuildCourseNoChargeType condition)
    {
        service.addCourseNoChargeType(condition);
        return RestResult.success();
    }
    
    @LogRecord(title = "删除重修不收费学生类型", type = AuditType.DELETE)
    @ApiOperation(value = "删除重修不收费学生类型")
    @PostMapping("/deleteCourseNoChargeType")
    public RestResult<?> deleteCourseNoChargeType(
        @RequestBody List<Long> ids)
    {
        service.deleteCourseNoChargeType(ids);
        return RestResult.success();
    }
    
    @LogRecord(title = "编辑重修不收费学生类型", type = AuditType.UPDATE)
    @ApiOperation(value = "编辑重修不收费学生类型")
    @PostMapping("/editCourseNoChargeType")
    public RestResult<?> editCourseNoChargeType(
        @RequestBody RebuildCourseNoChargeType courseNoCharge)
    {
        service.editCourseNoChargeType(courseNoCharge);
        return RestResult.success();
    }
    
    @ApiOperation(value = "查询所有重修的名单")
    @PostMapping("/findCourseNoChargeList")
    public RestResult<PageResult<RebuildCourseNoChargeList>> findCourseNoChargeList(
        @RequestBody @NotNull PageCondition<RebuildCourseDto> condition)
    {
        ValidatorUtil.validateAndThrow(condition.getCondition());
        
        PageResult<RebuildCourseNoChargeList> noChargeType =
            service.findCourseNoChargeList(condition);
        return RestResult.successData(noChargeType);
    }
    
    @ApiOperation(value = "查询学生的未缴费课程数")
    @PostMapping("/findCourseNoChargeStudentList")
    public RestResult<PageResult<StudentVo>> findCourseNoChargeStudentList(
        @RequestBody @NotNull PageCondition<RebuildCourseDto> condition)
    {
        ValidatorUtil.validateAndThrow(condition.getCondition());
        
        PageResult<StudentVo> noChargeType =
            service.findCourseNoChargeStudentList(condition);
        return RestResult.successData(noChargeType);
    }
    
    @ApiOperation(value = "移动到回收站")
    @PostMapping("/moveCourseNoChargeListToRecycle")
    public RestResult<?> moveCourseNoChargeListToRecycle(
        @RequestBody List<RebuildCourseNoChargeList> list)
    {
        service.moveToRecycle(list);
        return RestResult.success();
    }
    
    @ApiOperation(value = "查询回收站")
    @PostMapping("/findRecycleCourse")
    public RestResult<PageResult<RebuildCourseNoChargeList>> findRecycleCourse(
        @RequestBody @NotNull PageCondition<RebuildCourseDto> condition)
    {
        ValidatorUtil.validateAndThrow(condition.getCondition());
        
        PageResult<RebuildCourseNoChargeList> noChargeType =
            service.findRecycleCourse(condition);
        return RestResult.successData(noChargeType);
    }
    
    @ApiOperation(value = "从回收站恢复到未缴费的课程名单")
    @PostMapping("/moveRecycleCourseToNoChargeList")
    public RestResult<?> moveRecycleCourseToNoChargeList(@RequestBody List<RebuildCourseNoChargeList> list) {
        int size = list.size();
        List<RebuildCourseNoChargeList> conflictList = service.moveRecycleCourseToNoChargeList(list);
        if (CollectionUtil.isNotEmpty(conflictList)){
            //有冲突数据
            StringBuilder sb = new StringBuilder();
            sb.append("一共恢复数据"+ size +"条，成功恢复"+(size -conflictList.size())+"条。");
            sb.append("有冲突的"+conflictList.size()+"条。");
            sb.append("冲突数据为:");
            List<String> sList = new ArrayList<>();
            conflictList.forEach(c ->{
                 String s =  c.getStudentName()+"("+c.getStudentCode()+")的"+c.getCourseName()+"("+c.getCourseCode()+")课程";
                 sList.add(s);
            });
            sb.append(StringUtils.join(sList,","));
            return RestResult.success(sb.toString());
        }
       /* Long calendarId = add.getCalendarId();
        List<String> studentIds = add.getStudentIds();
        List<Long> teachingClassIds = add.getTeachingClassIds();
        Integer mode = add.getMode();*/
       /* ElcCourseTakeAddDto add = new ElcCourseTakeAddDto();
        add.setCalendarId(list.get(0).getCalendarId());
        List<String> studentIds = new ArrayList<>();
        List<Long> teachingClassIds = new ArrayList<>();
        list.forEach(l ->{
            studentIds.add(l.getStudentCode());
            teachingClassIds.add(l.getTeachingClassId());
        });
        add.setStudentIds(studentIds);
        add.setTeachingClassIds(teachingClassIds);
        String s = courseTakeService.add(add);*/
        return RestResult.success();
    }
    
    @ApiOperation(value = "导出重修汇总名单")
    @PostMapping("/exportStuNumber")
    public RestResult<ExcelResult> exportStuNumber(
        @RequestBody RebuildCourseDto condition)
        throws Exception
    {
        LOG.info("exportStuNumber.start");
        ExcelResult result = service.exportStuNumber(condition);
        return RestResult.successData(result);
    }
    
    @ApiOperation(value = "导出重修缴费名单")
    @PostMapping("/export")
    public RestResult<ExcelResult> export(
        @RequestBody RebuildCourseDto condition)
        throws Exception
    {
        LOG.info("export.start");
        ExcelResult result = service.export(condition);
        return RestResult.successData(result);
    }
    
    @ApiOperation(value = "导出回收站名单")
    @PostMapping("/exportRecycle")
    public RestResult<ExcelResult> exportRecycle(@RequestBody RebuildCourseDto condition) throws Exception {
        LOG.info("exportRecycle.start");
        ExcelResult result = service.exportRecycle(condition);
        return RestResult.successData(result);
    }
    
    /**
     * @Description: 根据key循环去redis取数据
     */
    @GetMapping("result/{key}")
    public RestResult<?> getResultByKey(
        @PathVariable("key") @NotBlank String key)
    {
        ExcelResult excelResult = ExportExcelUtils.getResultByKey(key);
        return RestResult.successData(excelResult);
    }
    
    /**
     * @Description: 下载
     */
    @ApiOperation(value = "导出excel下载文件")
    @GetMapping("/download")
    @ApiResponses({
        @ApiResponse(code = 200, response = File.class, message = "导出excel下载文件")})
    public ResponseEntity<Resource> download(@RequestParam("path") String path)
        throws Exception
    {
        return ExportExcelUtils.export(path);
    }
    
    @ApiOperation(value = "学生重修缴费明细")
    @PostMapping("/findStuRePayment")
    public RestResult<PageResult<StudentRePaymentDto>> findStuRePayment(@RequestBody PageCondition<StudentRePaymentDto> condition) {
        if (condition.getCondition().getCalendarId() == null) {
            throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
        }
        String studentCode = SessionUtils.getCurrentSession().realUid();
        condition.getCondition().setStudentCode(studentCode);
        PageResult<StudentRePaymentDto> result = service.findStuRePayment(condition);
        return RestResult.successData(result);
    }

    /**
     * @Description: 根据学号查询重修详情
     * @author kan yuanfeng
     * @date 2019/10/22 11:26
     */
    @ApiOperation(value = "根据学号查询重修详情")
    @PostMapping("student")
    public RestResult<PageResult<RebuildCourseNoChargeList>> findNoChargeListByStuId(@RequestBody PageCondition<RebuildCourseDto> condition) {
        //判断学号是否为空
        Assert.hasText(condition.getCondition().getStudentId(),"common.parameterError");
        PageResult<RebuildCourseNoChargeList> pageResult = service.findNoChargeListByStuId(condition);
        return RestResult.successData(pageResult);
    }

    /**
     * @Description: 导出重修详情根据学号
     * @author kan yuanfeng
     * @date 2019/10/22 11:26
     */
    @ApiResponses({@ApiResponse(code = 200, response = File.class, message = "导出重修详情根据学号")})
    @PostMapping("student/export")
    public ResponseEntity<Resource> exportByStuId(@RequestBody RebuildCourseDto rebuildCourseDto) throws Exception {
        ExcelWriterUtil result = service.exportByStuId(rebuildCourseDto);
        return ExportUtil.exportExcel(result, cacheDirectory,  "result.xls");
    }

    /**
     * @Description: 重修缴费回调接口
     * @author kan yuanfeng
     * @date 2019/11/7 9:22
     */
    @PostMapping("payCallback")
    public RestResult<?> payCallback(@RequestBody JSONObject jsonObject){
        Assert.notNull(jsonObject,"common.parameterError");
        service.payCallback(jsonObject);
        return RestResult.success();

    }

    /**
     * @Description: 财务对账(通过账单号)
     * @author kan yuanfeng
     * @date 2019/10/22 11:26
     */
    @ApiOperation("财务对账(通过账单号)")
    @PostMapping("payResult")
    public RestResult<?> payResult(@RequestBody List<RebuildCourseNoChargeList> rebuildCourseNoChargeLists){
        //学期(必填)，账单id
        service.payResult(rebuildCourseNoChargeLists);
        return RestResult.success();
    }

    @ApiOperation(value = "缴费订单查看")
    @PostMapping("payDetail")
    public RestResult<PageResult<StudentRePaymentDto>> payDetail(@RequestBody PageCondition<StudentRePaymentDto> condition) {
        Assert.notNull(condition.getCondition().getCalendarId(),"common.parameterError");
        String studentCode = SessionUtils.getCurrentSession().realUid();
        condition.getCondition().setStudentCode(studentCode);
        PageResult<StudentRePaymentDto> result = service.payDetail(condition);
        return RestResult.successData(result);
    }

    @ApiOperation(value = "缴费订单单条查询")
    @GetMapping("payDetail/{id}")
    public RestResult<?> payDetailById(@PathVariable("id") @NotNull Long id) {
        List<StudentRePaymentDto> result = service.payDetailById(id);
        return RestResult.successData(result);
    }
}
