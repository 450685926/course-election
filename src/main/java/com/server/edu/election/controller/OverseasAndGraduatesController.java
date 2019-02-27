package com.server.edu.election.controller;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;

import com.server.edu.election.entity.ElcNoGraduateStds;
import com.server.edu.election.service.ElcNoGraduateStdsService;
import com.server.edu.election.vo.ElcNoGraduateStdsVo;
import com.server.edu.util.CollectionUtil;
import com.server.edu.util.excel.GeneralExcelUtil;
import com.server.edu.util.excel.parse.ExcelParseConfig;
import com.server.edu.util.excel.parse.ExcelParseDesigner;
import io.swagger.annotations.*;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 留学结业生表
 * @author: bear
 * @create: 2019-02-22 09:45
 */

@SwaggerDefinition(info = @Info(title = "留学结业生", version = ""))
@RestSchema(schemaId = "OverseasAndGraduatesController")
@RequestMapping("/overseasOrGraduate")
public class OverseasAndGraduatesController {

    static Logger logger =
            LoggerFactory.getLogger(OverseasAndGraduatesController.class);
    @Autowired
    private ElcNoGraduateStdsService stdsService;

    @ApiOperation(value = "查询留学结业生")
    @PostMapping("/findOverseasOrGraduate")
    public RestResult<PageResult<ElcNoGraduateStdsVo>> findOverseasOrGraduate(@RequestBody PageCondition<ElcNoGraduateStdsVo> condition){
        if(condition.getCondition().getMode()==null){
            return RestResult.fail("common.parameterError");
        }
        PageResult<ElcNoGraduateStdsVo> overseasOrGraduate = stdsService.findOverseasOrGraduate(condition);
        return RestResult.successData(overseasOrGraduate);
    }


    @ApiOperation(value = "新增留学结业生")
    @PostMapping("/addOverseasOrGraduate")
    public RestResult<String> addOverseasOrGraduate(@RequestBody List<String> studentCodes,Integer mode){
        if(mode==null){
            return RestResult.fail("common.parameterError");
        }
        String s = stdsService.addOverseasOrGraduate(studentCodes, mode);
        return RestResult.success(s);
    }

    @ApiOperation(value = "删除留学结业生")
    @PostMapping("/deleteOverseasOrGraduate")
    public RestResult<String> deleteOverseasOrGraduate(@RequestBody List<String> codes){
        if(CollectionUtil.isEmpty(codes)){
            return RestResult.fail("common.parameterError");
        }
        String s = stdsService.deleteOverseasOrGraduate(codes);
        return RestResult.success(s);
    }

    @PostMapping(value = "/upload")
    public RestResult<?> upload(@RequestPart(name = "file") MultipartFile file,
                                @RequestPart(name = "mode") @NotNull Integer mode)
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

            designer.getConfigs().add(new ExcelParseConfig("studentId", 0));
            designer.getConfigs().add(new ExcelParseConfig("graduateYear", 1));
            designer.getConfigs().add(new ExcelParseConfig("remark", 2));
            List<ElcNoGraduateStds> datas = GeneralExcelUtil
                    .parseExcel(workbook, designer, ElcNoGraduateStds.class);

            String msg =stdsService.addExcel(datas,mode);
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
    @GetMapping(value = "/download")
    public ResponseEntity<Resource> download()
    {
        Resource resource = new ClassPathResource("/excel/jieYeShengMingDan.xls");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/vnd.ms-excel")
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment;filename=" + "jieYeShengMingDan.xls")
                .body(resource);
    }
}
