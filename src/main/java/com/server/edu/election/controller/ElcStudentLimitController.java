package com.server.edu.election.controller;

import java.io.File;
import java.util.List;

import javax.validation.Valid;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.dto.ElcStudentLimitDto;
import com.server.edu.election.dto.StudentDto;
import com.server.edu.election.entity.Student;
import com.server.edu.election.service.ElcStudentLimitService;
import com.server.edu.election.vo.ElcStudentLimitVo;
import com.server.edu.util.excel.export.ExcelResult;
import com.server.edu.util.excel.export.ExportExcelUtils;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
@SwaggerDefinition(info = @Info(title = "可选容量设置", version = ""))
@RestSchema(schemaId = "ElcStudentLimitController")
@RequestMapping("elcStudentLimit")
public class ElcStudentLimitController {
	private static Logger LOG =
	        LoggerFactory.getLogger(ElcStudentLimitController.class);
	@Autowired
	private ElcStudentLimitService elcStudentLimitService;
    /**
     *未限制学生名单
     * 
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "")
    @PostMapping("/getUnLimitStudents")
    public RestResult<PageInfo<Student>> getUnLimitStudents(
    		@RequestBody PageCondition<StudentDto> condition)
        throws Exception
    {
        LOG.info("getUnLimitStudents.start");
        PageInfo<Student> page =elcStudentLimitService.getUnLimitStudents(condition);
        return RestResult.successData(page);
    }
    
    /**
     * 添加限制学生
     * 
     * @param elcStudentLimitDto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "添加限制学生")
    @PostMapping("/add")
    public RestResult<Integer> add(
    		@RequestBody @Valid ElcStudentLimitDto elcStudentLimitDto)
        throws Exception
    {
        LOG.info("save.start");
        int result =elcStudentLimitService.add(elcStudentLimitDto);
        return RestResult.successData(result);
    }
    
    /**
     *限制学生名单
     * 
     * @param studentDto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "限制学生名单")
    @PostMapping("/getLimitStudents")
    public RestResult<PageInfo<ElcStudentLimitVo>> getLimitStudents(
    		@RequestBody PageCondition<ElcStudentLimitDto> condition)
        throws Exception
    {
        LOG.info("getLimitStudents.start");
        PageInfo<ElcStudentLimitVo> page =elcStudentLimitService.getLimitStudents(condition);
        return RestResult.successData(page);
    }
    
    
    /**
     *修改限制
     * 
     * @param elcStudentLimitDto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "修改限制")
    @PostMapping("/update")
    public RestResult<Integer> update(
    		@RequestBody ElcStudentLimitDto elcStudentLimitDto)
        throws Exception
    {
        LOG.info("update.start");
        int result =elcStudentLimitService.update(elcStudentLimitDto);
        return RestResult.successData(result);
    }
    
    /**
     *删除
     * 
     * @param elcStudentLimitDto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "删除")
    @PostMapping("/delete")
    public RestResult<Integer> delete(
    		@RequestBody @NotEmpty List<Long> ids)
        throws Exception
    {
        LOG.info("delete.start");
        int result =elcStudentLimitService.delete(ids);
        return RestResult.successData(result);
    }
    
    /**
     *删除所有
     * 
     * @param elcStudentLimitDto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "删除所有")
    @PostMapping("/deleteAll")
    public RestResult<Integer> deleteAll(
    		@RequestBody ElcStudentLimitDto elcStudentLimitDto)
        throws Exception
    {
        LOG.info("deleteAll.start");
        int result =elcStudentLimitService.deleteAll(elcStudentLimitDto);
        return RestResult.successData(result);
    }
    
    @ApiOperation(value = "导出个选限制")
    @PostMapping("/export")
    public RestResult<ExcelResult> export (
            @RequestBody ElcStudentLimitDto elcStudentLimitDto)
            throws Exception
    {
        LOG.info("export.start");
        ExcelResult export = elcStudentLimitService.export(elcStudentLimitDto);
        return RestResult.successData(export);
    }
   
    @ApiOperation(value = "导出未限制名单")
    @PostMapping("/exportUnLimit")
    public RestResult<ExcelResult> exportUnLimit (
            @RequestBody StudentDto studentDto)
            throws Exception
    {
        LOG.info("exportUnLimit.start");
        ExcelResult export = elcStudentLimitService.exportUnLimit(studentDto);
        return RestResult.successData(export);
    }
    
    @GetMapping("result/{key}")
    public RestResult<?> getResultByKey(@PathVariable("key") @NotBlank String key) {
        ExcelResult excelResult = ExportExcelUtils.getResultByKey(key);
        return RestResult.successData(excelResult);
    }
    
    @ApiOperation(value = "导出excel下载文件")
    @GetMapping("/download")
    @ApiResponses({@ApiResponse(code = 200, response = File.class, message = "导出excel下载文件")})
    public ResponseEntity<Resource> download(@RequestParam("path") String path) throws Exception
    {
        LOG.info("export.start");
        ResponseEntity<Resource> result = ExportExcelUtils.export(path);
        return result;
    }
    
}
