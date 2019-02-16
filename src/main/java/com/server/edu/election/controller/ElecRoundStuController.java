package com.server.edu.election.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.validator.ValidatorUtil;
import com.server.edu.election.dto.Student4Elc;
import com.server.edu.election.query.ElecRoundStuQuery;
import com.server.edu.election.service.ElecRoundStuService;
import com.server.edu.util.excel.GeneralExcelUtil;
import com.server.edu.util.excel.parse.ExcelParseConfig;
import com.server.edu.util.excel.parse.ExcelParseDesigner;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "可选课学生管理", version = ""))
@RestSchema(schemaId = "ElecRoundStuController")
@RequestMapping("elecRoundStu")
public class ElecRoundStuController
{
    static Logger logger =
        LoggerFactory.getLogger(ElecRoundStuController.class);
    
    @Autowired
    private ElecRoundStuService elecRoundStuService;
    
    /**
     * 分页查询
     *
     * @param condition
     * @return
     */
    @ApiOperation(value = "分页查询可选课学生信息")
    @PostMapping("/page")
    public RestResult<PageResult<Student4Elc>> page(
        @RequestBody PageCondition<ElecRoundStuQuery> query)
    {
        ValidatorUtil.validateAndThrow(query.getCondition());
        PageResult<Student4Elc> page = elecRoundStuService.listPage(query);
        
        return RestResult.successData(page);
    }
    
    /**
     * 添加可选课学生
     *
     * @param round
     * @return
     */
    @ApiOperation(value = "添加可选课学生")
    @PutMapping("/{roundId}")
    public RestResult<?> add(@PathVariable("roundId") @NotNull Long roundId,
        @RequestBody @NotNull List<String> studentCodes)
    {
        String add = elecRoundStuService.add(roundId, studentCodes);
        if (StringUtils.isNotBlank(add))
        {
            add = "学号" + add + "已经添加或不存在";
        }
        return RestResult.success(add);
    }
    
    /**
     * 条件添加可选课学生
     *
     * @param round
     * @return
     */
    @ApiOperation(value = "条件添加可选课学生")
    @PutMapping("/addByCondition")
    public RestResult<?> addByCondition(
        @RequestBody @Valid @NotNull ElecRoundStuQuery condition)
    {
        elecRoundStuService.addByCondition(condition);
        
        return RestResult.success();
    }
    
    /**
     * 删除可选课学生
     *
     * @param ids
     * @return
     */
    @ApiOperation(value = "删除可选课学生")
    @DeleteMapping("/{roundId}")
    public RestResult<?> delete(@PathVariable("roundId") @NotNull Long roundId,
        @RequestBody @NotEmpty List<String> studentCodes)
    {
        elecRoundStuService.delete(roundId, studentCodes);
        
        return RestResult.success();
    }
    
    /**
     * 条件删除可选课学生
     *
     * @param round
     * @return
     */
    @ApiOperation(value = "条件删除可选课学生")
    @DeleteMapping("/deleteByCondition")
    public RestResult<?> deleteByCondition(
        @RequestBody @Valid @NotNull ElecRoundStuQuery condition)
    {
        elecRoundStuService.deleteByCondition(condition);
        
        return RestResult.success();
    }
    
    @PostMapping(value = "/upload")
    public RestResult<?> upload(@RequestPart(name = "file") MultipartFile file,
        @RequestPart(name = "roundId") @NotNull Long roundId)
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
            
            List<Student4Elc> parseExcel = GeneralExcelUtil
                .parseExcel(workbook, designer, Student4Elc.class);
            
            List<String> codes = new ArrayList<>();
            for (Student4Elc student4Elc : parseExcel)
            {
                String studentId = StringUtils.trim(student4Elc.getStudentId());
                if (StringUtils.isNotBlank(studentId))
                {
                    codes.add(studentId);
                }
            }
            return this.add(roundId, codes);
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
        Resource resource = new ClassPathResource("/excel/keXuanKeMingDan.xls");
        
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, "application/vnd.ms-excel")
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment;filename=" + "keXuanKeMingDan.xls")
            .body(resource);
    }
}
