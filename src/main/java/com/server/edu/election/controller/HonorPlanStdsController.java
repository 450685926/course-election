package com.server.edu.election.controller;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.validator.ValidatorUtil;
import com.server.edu.election.query.HonorPlanStdsQuery;
import com.server.edu.election.service.HonorPlanStdsService;
import com.server.edu.election.vo.HonorPlanStdsVo;
import com.server.edu.util.excel.export.ExcelResult;
import com.server.edu.util.excel.export.ExportExcelUtils;
import io.swagger.annotations.*;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@SwaggerDefinition(info = @Info(title = "荣誉课程组名单", version = ""))
@RestSchema(schemaId = "HonorPlanStdsController")
@RequestMapping("honorPlanStds")
public class HonorPlanStdsController
{
    @Autowired
    private HonorPlanStdsService honorPlanStdsService;

    /**
     * 查询
     */
    /**
     * 上课名单列表
     *
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "荣誉课程组学生名单")
    @PostMapping("/page")
    public RestResult<PageResult<HonorPlanStdsVo>> page(
            @RequestBody PageCondition<HonorPlanStdsQuery> condition)
            throws Exception
    {
        ValidatorUtil.validateAndThrow(condition.getCondition());

        PageResult<HonorPlanStdsVo> list =
                honorPlanStdsService.listPage(condition);

        return RestResult.successData(list);
    }

    /**
     * 增加
     */
    @ApiOperation(value = "荣誉课程组学生名单添加")
    @PutMapping()
    public RestResult<?> add(
            @RequestBody HonorPlanStdsVo honorPlanStdsVo)
    {
        RestResult<?> result =  honorPlanStdsService.add(honorPlanStdsVo);

        return result;
    }


    /**
     * 删除
     */
    @ApiOperation(value = "荣誉课程组学生名单删除")
    @PostMapping("/delete")
    public RestResult<?> delete(
            @RequestBody HonorPlanStdsQuery honorPlanStds)
    {
        RestResult<?> result = honorPlanStdsService.delete(honorPlanStds);

        return result;
    }

    /**
     * 导入
     */

    /**
     * 导出
     */
    @ApiOperation(value = "导出荣誉计划名单")
    @PostMapping("/export")
    public RestResult<?> export(
            @RequestBody HonorPlanStdsQuery condition)
            throws Exception
    {
        ExcelResult result = honorPlanStdsService.export(condition);
        return RestResult.successData(result);
    }

    /**
     *
     * @param key
     * @return
     */
    @GetMapping("/result/{key}")
    public RestResult<ExcelResult> findSyndromeStatus(@PathVariable String key){
        ExcelResult resultByKey = ExportExcelUtils.getResultByKey(key);
        return RestResult.successData(resultByKey);
    }

    /**
     * @Description: 下载
     */
    @ApiOperation(value = "导出excel下载文件")
    @GetMapping("/downloadExcel")
    @ApiResponses({
            @ApiResponse(code = 200, response = File.class, message = "导出excel下载文件")})
    public ResponseEntity<Resource> downloadExcel(@RequestParam("path") String path)
            throws Exception
    {
        return ExportExcelUtils.export(path);
    }
    /**
     * 下载模板
     */
    @ApiResponses({
            @ApiResponse(code = 200, response = File.class, message = "荣誉计划名单下载模版")})
    @GetMapping(value = "/download")
    public ResponseEntity<Resource> download()
    {
        Resource resource = new ClassPathResource("/excel/HonorCourseMingDan.xls");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/vnd.ms-excel")
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment;filename=" + "HonorCourseMingDan.xls")
                .body(resource);
    }
}
