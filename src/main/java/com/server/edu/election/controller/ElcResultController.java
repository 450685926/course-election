package com.server.edu.election.controller;

import java.io.File;

import javax.validation.Valid;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.validator.ValidatorUtil;
import com.server.edu.dictionary.service.DictionaryService;
import com.server.edu.election.dto.AutoRemoveDto;
import com.server.edu.election.dto.ReserveDto;
import com.server.edu.election.dto.Student4Elc;
import com.server.edu.election.entity.TeachingClass;
import com.server.edu.election.query.ElcResultQuery;
import com.server.edu.election.service.ElcResultService;
import com.server.edu.election.vo.ElcResultCountVo;
import com.server.edu.election.vo.TeachingClassVo;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.ExportUtil;
import com.server.edu.util.excel.export.ExcelResult;
import com.server.edu.util.excel.export.ExportExcelUtils;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "选课结果", version = ""))
@RestSchema(schemaId = "ElcResultController")
@RequestMapping("elcResult")
public class ElcResultController
{
    Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private ElcResultService elcResultService;
    
    @Autowired
    private DictionaryService dictionaryService;
     
    @Value("${cache.directory}")
    private String cacheDirectory;
    
    /**
     * 上课名单列表
     * 
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "教学班信息")
    @PostMapping("/teachClassPage")
    public RestResult<PageResult<TeachingClassVo>> page(
        @RequestBody PageCondition<ElcResultQuery> condition)
        throws Exception
    {
        ValidatorUtil.validateAndThrow(condition.getCondition());
        
        PageResult<TeachingClassVo> list = elcResultService.listPage(condition);
        
        return RestResult.successData(list);
    }
    
    /**
     * 调整教学班容量
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "调整教学班容量")
    @PostMapping("/adjustClassNumber")
    public RestResult<?> adjustClassNumber(
        @RequestBody TeachingClassVo teachingClassVo)
    {
        elcResultService.adjustClassNumber(teachingClassVo);
        
        return RestResult.success();
    }
    
    /**
     * 设置具体预留人数
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "设置具体预留人数")
    @PostMapping("/setReserveNum")
    public RestResult<?> setReserveNum(
        @RequestBody TeachingClass teachingClass)
    {
        elcResultService.setReserveNum(teachingClass);
        
        return RestResult.success();
    }
    
    /**
     * 批量设置预留人数
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "批量设置预留人数")
    @PostMapping("/batchSetReserveNum")
    public RestResult<?> batchSetReserveNum(
        @RequestBody @Valid ReserveDto reserveDto)
    {
        elcResultService.batchSetReserveNum(reserveDto);
        
        return RestResult.success();
    }
    
    /**
     * 设置具体预留人数比例
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "设置具体预留人数比例")
    @PostMapping("/setReserveProportion")
    public RestResult<?> setReserveProportion(
        @RequestBody @Valid ReserveDto reserveDto)
    {
        elcResultService.setReserveProportion(reserveDto);
        
        return RestResult.success();
    }
    
    /**
     * 释放人数
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "释放人数")
    @PostMapping("/release")
    public RestResult<?> release(
        @RequestBody @Valid ReserveDto reserveDto)
    {
        elcResultService.release(reserveDto);
        
        return RestResult.success();
    }
    
    
    /**
     * 释放所有
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "释放所有")
    @PostMapping("/releaseAll")
    public RestResult<?> releaseAll(@RequestBody ElcResultQuery condition)
    {
        elcResultService.releaseAll(condition);
        
        return RestResult.success();
    }
    
    @ApiOperation(value = "自动剔除超过人数")
    @PostMapping("/autoRemove")
    public RestResult<?> autoRemove(@RequestBody AutoRemoveDto dto)
        throws Exception
    {
        elcResultService.autoRemove(dto);
        return RestResult.success();
    }
    
    @ApiOperation(value = "学生选课结果统计")
    @PostMapping("/elcResultCountByStudent")
    public RestResult<ElcResultCountVo> elcResultCountByStudent(
    		@RequestBody PageCondition<ElcResultQuery> condition)
    		throws Exception
    {
    	ValidatorUtil.validateAndThrow(condition.getCondition());
    	Session session = SessionUtils.getCurrentSession();
    	if (!session.isAdmin()) {
    		return RestResult.fail("elec.mustBeAdmin");
        }
    	ElcResultCountVo result = elcResultService.elcResultCountByStudent(condition);
    	return RestResult.successData(result);
    }
    
    @ApiOperation(value = "选课学生统计导出")
    @PostMapping("/elcResultCountByStudentExport")
    public RestResult<?> elcResultCountByStudentExport(
    		@RequestBody ElcResultQuery condition)
    				throws Exception
    {
    	ExcelResult result = elcResultService.elcResultCountByStudentExport(condition);
        return RestResult.successData(result);
    	
    }
    
    
    
    @ApiOperation(value = "未选课学生名单")
    @PostMapping("/elcResultNonSelectedStudent")
    public RestResult<PageResult<Student4Elc>> studentPage(
    		@RequestBody PageCondition<ElcResultQuery> condition)
    				throws Exception
    {
    	ValidatorUtil.validateAndThrow(condition.getCondition());
    	Session session = SessionUtils.getCurrentSession();
        
    	if (!session.isAdmin()) {
    		return RestResult.fail("elec.mustBeAdmin");
        }
        PageResult<Student4Elc> result = elcResultService.getStudentPage(condition);
    	return RestResult.successData(result);
    }
    
    /**
     * 研究生未选课学生名单
     * @param condition
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/export")
    public RestResult<?> export(
    		@RequestBody ElcResultQuery condition)
    				throws Exception
    {
        ExcelResult result = elcResultService.export(condition);
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
    
    @ApiOperation(value = "导出excel下载文件")
    @GetMapping("/download")
    @ApiResponses({
        @ApiResponse(code = 200, response = File.class, message = "导出excel下载文件")})
    public ResponseEntity<Resource> download(@RequestParam("path") String path)
        throws Exception
    {
        return ExportExcelUtils.export(path);
    }
    
}
