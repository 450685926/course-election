package com.server.edu.election.controller;

import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.log.LogRecord;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.dmskafka.entity.AuditType;
import com.server.edu.election.dto.ExemptionApplyCondition;
import com.server.edu.election.dto.ExemptionCourseScoreDto;
import com.server.edu.election.entity.*;
import com.server.edu.election.service.ExemptionCourseService;
import com.server.edu.election.vo.*;
import com.server.edu.util.excel.GeneralExcelUtil;
import com.server.edu.util.excel.parse.ExcelParseConfig;
import com.server.edu.util.excel.parse.ExcelParseDesigner;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 免修免考管理
 * @author: bear
 * @create: 2019-01-31 09:26
 */

@SwaggerDefinition(info = @Info(title = "免修免考管理", version = ""))
@RestSchema(schemaId = "ExemptionController")
@RequestMapping("/exemption")
public class ExemptionController {

    @Autowired
    private ExemptionCourseService exemptionCourseService;

    private static Logger LOG =
            LoggerFactory.getLogger(ExemptionController.class);

    @Value("${cache.directory}")
    private String cacheDirectory;

    static Logger logger =
            LoggerFactory.getLogger(ExemptionController.class);
    /**
    *@Description: 查询免修免考课程
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/1/31 9:29
    */

    @ApiOperation(value = "查询免修免考课程信息")
    @PostMapping("/findExemptionCourse")
    public RestResult<PageResult<ExemptionCourseVo>> findMasterCourse(@RequestBody PageCondition<ExemptionCourse> condition) {
        PageResult<ExemptionCourseVo> exemptionCourse = exemptionCourseService.findExemptionCourse(condition);
        return RestResult.successData(exemptionCourse);
    }

    @LogRecord(title="删除免修免考课程",type = AuditType.DELETE)
    @ApiOperation(value = "删除免修免考课程")
    @PostMapping("/deleteExemptionCourse")
     public RestResult<String> deleteExemptionCourseByIds(@RequestBody List<Long> ids){
        String s = exemptionCourseService.deleteExemptionCourse(ids);
        return RestResult.success(I18nUtil.getMsg(s,""));
    }


    @LogRecord(title="新增免修免考课程",type = AuditType.INSERT)
    @ApiOperation(value = "新增免修免考课程")
    @PostMapping("/addExemptionCourse")
    public RestResult<String> addExemptionCourse(@RequestBody ExemptionCourse exemptionCourse){
        String s = exemptionCourseService.addExemptionCourse(exemptionCourse);
        return RestResult.success(I18nUtil.getMsg(s,""));
    }

    @LogRecord(title="修改免修免考课程",type = AuditType.UPDATE)
    @ApiOperation(value = "修改免修免考课程")
    @PostMapping("/editExemptionCourse")
    public RestResult<String> editExemptionCourse(@RequestBody ExemptionCourse exemptionCourse){
        String s = exemptionCourseService.updateExemptionCourse(exemptionCourse);
        return RestResult.success(I18nUtil.getMsg(s,""));
    }

    @ApiOperation(value = "查询免修免考入学成绩")
    @PostMapping("/findExemptionCourseScore")
    public RestResult<PageResult<ExemptionCourseScoreVo>> findExemptionCourseScore(@RequestBody PageCondition<ExemptionCourseScoreDto> condition) {
        PageResult<ExemptionCourseScoreVo> exemptionScore = exemptionCourseService.findExemptionScore(condition);
        return RestResult.successData(exemptionScore);
    }

    //免修免考成绩导入todo

    @ApiOperation(value = "查询免修免考申请规则")
    @PostMapping("/findExemptionCourseRule")
    public RestResult<PageResult<ExemptionCourseRuleVo>> findExemptionCourseRule(@RequestBody PageCondition<ExemptionCourseRule> courseRule){
        PageResult<ExemptionCourseRuleVo> exemptionRule = exemptionCourseService.findExemptionRule(courseRule);
        return RestResult.successData(exemptionRule);
    }


    @LogRecord(title="删除免修免考申请规则",type = AuditType.DELETE)
    @ApiOperation(value = "删除免修免考申请规则")
    @PostMapping("/deleteExemptionCourseRule")
    public RestResult<String> deleteExemptionCourseRule(@RequestBody List<Long> ids, @RequestParam Integer applyType){
        String s = exemptionCourseService.deleteExemptionCourseRule(ids, applyType);
        return RestResult.success(I18nUtil.getMsg(s,""));
    }


    @LogRecord(title="新增免修免考申请规则",type = AuditType.INSERT)
    @ApiOperation(value = "新增免修免考申请规则")
    @PostMapping("/addExemptionCourseRule")
    public RestResult<String> addExemptionCourseRule(@RequestBody ExemptionCourseRuleVo courseRuleVo,@RequestParam Integer applyType){
        String s = exemptionCourseService.addExemptionCourseRule(courseRuleVo, applyType);
        return RestResult.success(I18nUtil.getMsg(s,""));
    }

    //修改免修免考规则todo


    @ApiOperation(value = "查询免修免考申请管理")
    @PostMapping("/findExemptionApply")
    public RestResult<PageResult<ExemptionApplyManageVo>> findExemptionApply(@RequestBody PageCondition<ExemptionApplyCondition> condition){
        PageResult<ExemptionApplyManageVo> exemptionApply = exemptionCourseService.findExemptionApply(condition);
        return RestResult.successData(exemptionApply);
    }

    @LogRecord(title="新增免修免考申请",type = AuditType.INSERT)
    @ApiOperation(value = "新增免修免考申请管理")
    @PostMapping("/addExemptionApply")
    public RestResult<String> addExemptionApply(@RequestBody ExemptionApplyManage applyManage){
        String s = exemptionCourseService.addExemptionApply(applyManage);
        return RestResult.success(I18nUtil.getMsg(s,""));
    }



    @ApiOperation(value = "新增免修免考申请条件限制")
    @PostMapping("/addExemptionApplyConditionLimit")
    public RestResult<ExemptionCourseMaterialVo> addExemptionApplyConditionLimit(@RequestBody ExemptionApplyManage applyManage){
    return exemptionCourseService.addExemptionApplyConditionLimit(applyManage);
    }


    @ApiOperation(value = "查询学生信息")
    @GetMapping("/findStudentMessage")
    public RestResult<Student> findStudentMessage(@RequestParam  String studentCode){
        return exemptionCourseService.findStudentMessage(studentCode);
    }

    @LogRecord(title="删除免修免考申请",type = AuditType.DELETE)
    @ApiOperation(value = "删除免修免考申请")
    @PostMapping("/deleteExemptionApply")
    public RestResult<String> deleteExemptionApply(@RequestBody List<Long>  ids){
        String s= exemptionCourseService.deleteExemptionApply(ids);
        return RestResult.success(I18nUtil.getMsg(s,""));
    }

    @LogRecord(title="审批免修免考申请",type = AuditType.UPDATE)
    @ApiOperation(value = "审批免修免考申请")
    @PostMapping("/approvalExemptionApply")
    public RestResult<String> approvalExemptionApply(@RequestBody List<Long>  ids,@RequestParam Integer status){
        String s= exemptionCourseService.approvalExemptionApply(ids,status);
        return RestResult.success(I18nUtil.getMsg(s,""));
    }


    @LogRecord(title="编辑免修免考申请",type = AuditType.UPDATE)
    @ApiOperation(value = "编辑免修免考申请")
    @PostMapping("/editExemptionApply")
    public RestResult<String> editExemptionApply(@RequestBody ExemptionApplyManage applyManage){
        String s= exemptionCourseService.editExemptionApply(applyManage);
        return RestResult.success(I18nUtil.getMsg(s,""));
    }

    @ApiOperation(value = "导出免修免考管理")
    @PostMapping("/export")
    public RestResult<String> export (
            @RequestBody ExemptionApplyCondition condition)
            throws Exception
    {
        LOG.info("export.start");
        String export = exemptionCourseService.export(condition);
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

    @PostMapping(value = "/upload")
    public RestResult<?> upload(@RequestPart(name = "file") MultipartFile file,
                                @RequestPart(name = "calendarId") @NotNull Long calendarId)
    {
        if (file == null)
        {
            return RestResult.error("文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (!originalFilename.endsWith(".xls"))
        {
            return RestResult.error("请使用1999-2003(.xls)类型的Excle");
        }

        try (HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream()))
        {
            ExcelParseDesigner designer = new ExcelParseDesigner();
            designer.setDataStartRowIdx(1);
            designer.setConfigs(new ArrayList<>());

            designer.getConfigs().add(new ExcelParseConfig("studentCode", 0));
            designer.getConfigs().add(new ExcelParseConfig("courseCode", 1));
            designer.getConfigs().add(new ExcelParseConfig("score", 2){
                public Object handler(String value)
                {
                    if(StringUtils.isNotBlank(value)){
                        return Double.valueOf(value);
                    }
                    return null;
                }
            });
            List<ExemptionCourseScore> datas = GeneralExcelUtil
                    .parseExcel(workbook, designer, ExemptionCourseScore.class);

            String msg =exemptionCourseService.addExcel(datas,calendarId);
            return RestResult.success(msg);

        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return RestResult.error("解析文件错误" + e.getMessage());
        }
    }

    @ApiResponses({
            @ApiResponse(code = 200, response = File.class, message = "下载模版")})
    @GetMapping(value = "/downloadTemplate")
    public ResponseEntity<Resource> downloadTemplate()
    {
        Resource resource = new ClassPathResource("/excel/ruXueScore.xls");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/vnd.ms-excel")
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment;filename=" + "ruXueScore.xls")
                .body(resource);
    }
}
