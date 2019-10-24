package com.server.edu.exam.controller;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.exam.service.GraduateExamMakeUpQueryService;
import com.server.edu.exam.vo.GraduateExamApplyExaminationVo;
import com.server.edu.util.excel.export.ExcelResult;
import com.server.edu.util.excel.export.ExportExcelUtils;
import io.swagger.annotations.*;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;

/**
 * @description: 补缓考应考学生查询
 * @author: bear
 * @create: 2019-09-11 16:14
 */

@SwaggerDefinition(info = @Info(title = "补缓考应考学生查询", version = ""))
@RestSchema(schemaId = "GraduateExamMakeUpQueryController")
@RequestMapping("/graduateExamMakeUpQuery")
public class GraduateExamMakeUpQueryController {
    private static Logger LOG =
            LoggerFactory.getLogger(GraduateExamMakeUpQueryController.class);

    @Autowired
    private GraduateExamMakeUpQueryService queryService;

    @PostMapping("/listExamMakeUpQuery")
    public RestResult<PageResult<GraduateExamApplyExaminationVo>> listExamMakeUpQuery(@RequestBody PageCondition<GraduateExamApplyExaminationVo> condition){
       PageResult<GraduateExamApplyExaminationVo> pageResult =  queryService.listExamMakeUpQuery(condition);
       return RestResult.successData(pageResult);
    }

    @PostMapping("/export")
    public RestResult<?> export(@RequestBody GraduateExamApplyExaminationVo condition){
        ExcelResult result = queryService.export(condition);
        return RestResult.successData(result);
    }

    @ApiOperation(value = "导出excel下载文件")
    @GetMapping("/download")
    @ApiResponses({@ApiResponse(code = 200, response = File.class, message = "导出excel下载文件")})
    public ResponseEntity<Resource> download(@RequestParam("path") String path)
            throws Exception {
        return ExportExcelUtils.export(path);
    }

}
