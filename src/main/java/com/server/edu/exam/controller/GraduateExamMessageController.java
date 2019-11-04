package com.server.edu.exam.controller;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.exam.dto.ExportExamInfoDto;
import com.server.edu.exam.query.GraduateExamMessageQuery;
import com.server.edu.exam.service.GraduateExamMessageService;
import com.server.edu.exam.vo.GraduateExamMessage;
import com.server.edu.util.excel.export.ExcelResult;
import com.server.edu.util.excel.export.ExportExcelUtils;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;

/**
 * @description: 排考信息查询
 * @author: bear
 * @create: 2019-09-06 16:04
 */

@SwaggerDefinition(info = @Info(title = "研究生排考信息查询", version = ""))
@RestSchema(schemaId = "GraduateExamMessageController")
@RequestMapping("/graduateExamMessage")
public class GraduateExamMessageController {

    @Autowired
    private GraduateExamMessageService messageService;

    @PostMapping("/listGraduateExamMessage")
    public RestResult<PageResult<GraduateExamMessage>> listGraduateExamMessage(@RequestBody PageCondition<GraduateExamMessageQuery> condition){
       PageResult<GraduateExamMessage> pageResult = messageService.listGraduateExamMessage(condition);
       return RestResult.successData(pageResult);
    }

    @PostMapping("/export")
    public RestResult<?> export(@RequestBody GraduateExamMessageQuery condition){
        ExcelResult result = messageService.export(condition);
        return RestResult.successData(result);
    }

    @PostMapping("/exportStuList")
    public RestResult<?> exportStuList(@RequestBody ExportExamInfoDto exportExamInfoDto){
        ExcelResult result = messageService.exportStuList(exportExamInfoDto);
        return RestResult.successData(result);
    }

    @GetMapping("/exportCheckTable")
    public RestResult<?> exportCheckTable(@RequestParam Long calendarId,@RequestParam Integer examType,@RequestParam String calendarName){
        ExcelResult result = messageService.exportCheckTable(calendarId,examType,calendarName);
        return RestResult.successData(result);
    }

    @PostMapping("/exportPropertySheet")
    public RestResult<?> exportPropertySheet(@RequestBody GraduateExamMessageQuery condition){
        ExcelResult result = messageService.exportPropertySheet(condition);
        return RestResult.successData(result);
    }

    @PostMapping("/exportInspectionSheet")
    public RestResult<?> exportInspectionSheet(@RequestBody GraduateExamMessageQuery condition){
        ExcelResult result = messageService.exportInspectionSheet(condition);
        return RestResult.successData(result);
    }

    @GetMapping("result/{key}")
    public RestResult<?> getResultByKey(@PathVariable("key") @NotBlank String key) {
        ExcelResult excelResult = ExportExcelUtils.getResultByKey(key);
        return RestResult.successData(excelResult);
    }

    @GetMapping("resultObs/{obsKey}")
    public RestResult<?> getResultByObsKey(@PathVariable("obsKey") @NotBlank String obsKey) {
        ExcelResult excelResult = ExportExcelUtils.getResultByKey2(obsKey);
        return RestResult.successData(excelResult);
    }

    /**
     * 批量下载签到表，导出并打压缩包(通过path下载文件)
     * @param path
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "导出批量下载文件")
    @GetMapping("/downLoadMore")
    @ApiResponses({@ApiResponse(code = 200, response = File.class, message = "导出excel下载文件")})
    public ResponseEntity<Resource> downLoadMore(@RequestParam("path") String path,@RequestParam("fileName") String fileName) throws Exception
    {
        if(!StringUtils.isNotBlank(fileName)){
            fileName = "result.zip";
        }
        ResponseEntity<Resource> result = ExportExcelUtils.export(path,"application/zip",fileName);
        return result;
    }

    @GetMapping("/exportCheckTableFreemarker")
    public RestResult<?> exportCheckTableFreemarker(@RequestParam Long calendarId,@RequestParam Integer examType,@RequestParam String calendarName){
        ExcelResult result = messageService.exportCheckTableFreemarker(calendarId,examType,calendarName);
        return RestResult.successData(result);
    }
}
