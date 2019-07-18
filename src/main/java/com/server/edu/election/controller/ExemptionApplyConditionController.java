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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ExemptionApplyGraduteConditionDto;
import com.server.edu.election.entity.CourseOpen;
import com.server.edu.election.entity.ExemptionApplyGraduteCondition;
import com.server.edu.election.service.ExemptionApplyConditionService;
import com.server.edu.util.excel.GeneralExcelUtil;
import com.server.edu.util.excel.parse.ExcelParseConfig;
import com.server.edu.util.excel.parse.ExcelParseDesigner;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "研究生免修免考申请条件", version = ""))
@RestSchema(schemaId = "ExemptionApplyConditionController")
@RequestMapping("/exemptionConditionGradute")
public class ExemptionApplyConditionController {
	static Logger logger = LoggerFactory.getLogger(ExemptionController.class);
	
	@Autowired
	ExemptionApplyConditionService exemptionApplyConditionSerice;
	
	@ApiOperation(value = "添加研究生免修免考申请条件")
    @PostMapping("/addExemptionApplyCondition")
    public RestResult<?> addExemptionApplyCondition(@RequestBody @NotNull @Valid ExemptionApplyGraduteCondition applyCondition){
    	logger.info("addExemptionApplyCondition start");
    	
    	exemptionApplyConditionSerice.addExemptionApplyCondition(applyCondition);
    	return RestResult.success();
    }
	
    @ApiOperation(value = "查询研究生免修免考申请条件列表")
    @PostMapping("/queryExemptionApplyCondition")
    public RestResult<PageResult<ExemptionApplyGraduteCondition>> queryExemptionApplyCondition(
    		@RequestBody PageCondition<ExemptionApplyGraduteConditionDto> applyConditionDto){
    	logger.info("queryExemptionApplyCondition start");
    	
    	PageResult<ExemptionApplyGraduteCondition> page = exemptionApplyConditionSerice.queryExemptionApplyCondition(applyConditionDto);
    	return RestResult.successData(page);
    }
	
    @ApiOperation(value = "根据ID查询免修免考申请条件")
    @GetMapping("/{id}")
    public RestResult<ExemptionApplyGraduteCondition> findExemptionAuditConditionById(@PathVariable("id") Long id){
    	logger.info("findExemptionAuditConditionById start");
    	
    	ExemptionApplyGraduteCondition exemptionApplyAuditSwitch = exemptionApplyConditionSerice.findExemptionAuditConditionById(id);
    	return RestResult.successData(exemptionApplyAuditSwitch);
    }
	
    @ApiOperation(value = "修改免修免考申请条件")
    @PostMapping("/updateExemptionApplyCondition")
    public RestResult<?> updateExemptionApplyCondition(@RequestBody @NotNull @Valid ExemptionApplyGraduteCondition applyCondition){
    	logger.info("updateExemptionApplyCondition start");
    	
    	exemptionApplyConditionSerice.updateExemptionApplyCondition(applyCondition);
    	return RestResult.success();
    }
    
    @ApiOperation(value = "删除免修免考申请条件")
    @DeleteMapping
    public RestResult<?> deleteExemptionApplyCondition(@RequestBody @NotEmpty List<Long> ids){
    	logger.info("deleteExemptionApplyCondition start");
    	
    	exemptionApplyConditionSerice.deleteExemptionApplyCondition(ids);
    	return RestResult.success();
    }
    
    @ApiResponses({
        @ApiResponse(code = 200, response = File.class, message = "免修条件下载模版（研究生）")})
    @GetMapping(value = "/graduateDownload")
    public ResponseEntity<Resource> graduateDownload()
    {
        Resource resource = new ClassPathResource("/excel/yanJiuShengExemptionApplyCondition.xls");
        
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, "application/vnd.ms-excel")
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment;filename=" + "yanJiuShengExemptionApplyCondition.xls")
            .body(resource);
    }
    
    @ApiOperation(value = "导入免修免考申请条件")
    @PostMapping("/importExemptionAuditSwitch")
    public RestResult<?> upload(@RequestPart(name = "file") MultipartFile file){
    	if (file == null)
        {
            return RestResult.error("文件不能为空");
        }
        
        String originalFilename = file.getOriginalFilename();
        if (!originalFilename.endsWith(".xls"))
        {
            return RestResult.error("请使用1999-2003(.xls)类型的Excle");
        }
    	
        try (HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream())){
            ExcelParseDesigner designer = new ExcelParseDesigner();
            designer.setDataStartRowIdx(1);
            designer.setConfigs(new ArrayList<>());
            
            designer.getConfigs().add(new ExcelParseConfig("courseCode", 0));
            designer.getConfigs().add(new ExcelParseConfig("courseName", 1));
            designer.getConfigs().add(new ExcelParseConfig("trainingLevels", 2));
            designer.getConfigs().add(new ExcelParseConfig("trainingCategorys", 3));
            designer.getConfigs().add(new ExcelParseConfig("degreeCategorys", 4));
            designer.getConfigs().add(new ExcelParseConfig("formLearnings", 5));
            designer.getConfigs().add(new ExcelParseConfig("conditions", 6));
            
            List<ExemptionApplyGraduteCondition> parseExcel = GeneralExcelUtil
                .parseExcel(workbook, designer, ExemptionApplyGraduteCondition.class);
            
            List<ExemptionApplyGraduteCondition> list = new ArrayList<ExemptionApplyGraduteCondition>();
            for (ExemptionApplyGraduteCondition condition : parseExcel)
            {
                String courseCode = StringUtils.trim(condition.getCourseCode());
                String courseName = StringUtils.trim(condition.getCourseName());
                String trainingLevels = StringUtils.trim(condition.getTrainingLevels());
                String conditions = StringUtils.trim(condition.getConditions());
                if (StringUtils.isNotBlank(courseCode) 
                		&& StringUtils.isNotBlank(courseName)
                		&& StringUtils.isNotBlank(trainingLevels)
                		&& StringUtils.isNotBlank(conditions))
                {
                	condition.setProjId(Constants.PROJ_GRADUATE);
                	list.add(condition);
                	this.addExemptionApplyCondition(condition);
                }
            }
        } catch (Exception e){
            logger.error(e.getMessage(), e);
            return RestResult.error("解析文件错误" + e.getMessage());
        }
        return RestResult.success();
    }
    
    @ApiOperation(value = "根据课程编号查询名称和培养层次")
    @PostMapping("/queryNameAndTrainingLevelByCode")
    public RestResult<CourseOpen> queryNameAndTrainingLevelByCode(
    		@RequestParam("courseCode") @NotNull String courseCode){
    	logger.info("queryNameAndTrainingLevelByCode start");
    	
    	CourseOpen data = exemptionApplyConditionSerice.queryNameAndTrainingLevelByCode(courseCode);
    	return RestResult.successData(data);
    }
}
