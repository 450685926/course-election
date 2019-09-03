package com.server.edu.election.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.service.DictionaryService;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ExemptionApplyGraduteConditionDto;
import com.server.edu.election.entity.Course;
import com.server.edu.election.entity.CourseOpen;
import com.server.edu.election.entity.ExemptionApplyGraduteCondition;
import com.server.edu.election.service.ExemptionApplyConditionService;
import com.server.edu.election.vo.ExemptionApplyManageVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.util.CollectionUtil;
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
@RequestMapping("/exemptionGradute/applyCondition")
public class ExemptionApplyConditionController {
	static Logger logger = LoggerFactory.getLogger(ExemptionApplyConditionController.class);
	
	@Autowired
	ExemptionApplyConditionService exemptionApplyConditionSerice;
	
	@Autowired
	DictionaryService dictionaryService;
	
	@ApiOperation(value = "添加研究生免修免考申请条件")
    @PostMapping
    public RestResult<?> addExemptionApplyCondition(@RequestBody @NotNull @Valid ExemptionApplyGraduteCondition applyCondition){
    	logger.info("add applyCondition start");
    	
    	String conditions = applyCondition.getConditions();
    	if (StringUtils.isNotBlank(conditions)) {
    		applyCondition.setConditions(conditions.trim());
		}
    	
    	exemptionApplyConditionSerice.addExemptionApplyCondition(applyCondition);
    	return RestResult.success();
    }
	
    @ApiOperation(value = "查询研究生免修免考申请条件列表")
    @PostMapping("/list")
    public RestResult<PageResult<ExemptionApplyGraduteCondition>> queryExemptionApplyCondition(
    		@RequestBody PageCondition<ExemptionApplyGraduteConditionDto> applyConditionDto){
    	logger.info("query applyCondition list start");
    	
    	PageResult<ExemptionApplyGraduteCondition> page = exemptionApplyConditionSerice.queryExemptionApplyCondition(applyConditionDto);
    	return RestResult.successData(page);
    }
	
    @ApiOperation(value = "根据ID查询免修免考申请条件")
    @GetMapping("/{id}")
    public RestResult<ExemptionApplyGraduteCondition> findExemptionAuditConditionById(@PathVariable("id") Long id){
    	logger.info("query applyCondition by id start");
    	
    	ExemptionApplyGraduteCondition exemptionApplyAuditSwitch = exemptionApplyConditionSerice.findExemptionAuditConditionById(id);
    	return RestResult.successData(exemptionApplyAuditSwitch);
    }
	
    @ApiOperation(value = "修改免修免考申请条件")
    @PutMapping
    public RestResult<?> updateExemptionApplyCondition(@RequestBody @NotNull @Valid ExemptionApplyGraduteCondition applyCondition){
    	logger.info("update applyCondition start");
    	
    	String conditions = applyCondition.getConditions();
    	if (StringUtils.isNotBlank(conditions)) {
    		applyCondition.setConditions(conditions.trim());
		}
    	
    	exemptionApplyConditionSerice.updateExemptionApplyCondition(applyCondition);
    	return RestResult.success();
    }
    
    @ApiOperation(value = "删除免修免考申请条件")
    @DeleteMapping
    public RestResult<?> deleteExemptionApplyCondition(@RequestBody @NotEmpty List<Long> ids){
    	logger.info("delete applyCondition start");
    	
    	exemptionApplyConditionSerice.deleteExemptionApplyCondition(ids);
    	return RestResult.success();
    }
    
    @ApiResponses({
        @ApiResponse(code = 200, response = File.class, message = "免修条件下载模版（研究生）")})
    @GetMapping(value = "/export")
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
    @PostMapping("/import")
    public RestResult<?> upload(@RequestPart(name = "file") MultipartFile file){
    	if (file == null)
        {
            return RestResult.error("文件不能为空");
        }
        
        String originalFilename = file.getOriginalFilename();
        if (!originalFilename.endsWith(".xls"))
        {
            return RestResult.error("请使用1999-2003(.xls)类型的Excel");
        }
    	
        try (HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream())){
            ExcelParseDesigner designer = new ExcelParseDesigner();
            designer.setDataStartRowIdx(1);
            designer.setConfigs(new ArrayList<>());
            
            designer.getConfigs().add(new ExcelParseConfig("courseCode", 0));
            designer.getConfigs().add(new ExcelParseConfig("courseName", 1));
            designer.getConfigs().add(new ExcelParseConfig("trainingLevels", 2));
            designer.getConfigs().add(new ExcelParseConfig("trainingCategorys", 3));
            designer.getConfigs().add(new ExcelParseConfig("degreeTypes", 4));
            designer.getConfigs().add(new ExcelParseConfig("formLearnings", 5));
            designer.getConfigs().add(new ExcelParseConfig("conditions", 6));
            
            List<ExemptionApplyGraduteCondition> parseExcel = GeneralExcelUtil
                .parseExcel(workbook, designer, ExemptionApplyGraduteCondition.class);
            
            String projectId = SessionUtils.getCurrentSession().getCurrentManageDptId();
            
            List<ExemptionApplyGraduteCondition> list = new ArrayList<ExemptionApplyGraduteCondition>();
            
            Map<String, Map<String, String>> mapList = dictionaryService.queryByTypeList(Arrays.asList(DictTypeEnum.X_PYCC.getType(),DictTypeEnum.X_PYLB.getType(),DictTypeEnum.X_XWLX.getType(),DictTypeEnum.X_XXXS.getType()), SessionUtils.CN);
            
            for (ExemptionApplyGraduteCondition condition : parseExcel)
            {
                String courseCode = StringUtils.trim(condition.getCourseCode());
                String courseName = StringUtils.trim(condition.getCourseName());
                String trainingLevels = StringUtils.trim(condition.getTrainingLevels());
                String trainingCategorys = StringUtils.trim(condition.getTrainingCategorys());
                String degreeTypes = StringUtils.trim(condition.getDegreeTypes());
                String formLearnings = StringUtils.trim(condition.getFormLearnings());
                String conditions = StringUtils.trim(condition.getConditions());
                
                // ************************* 校验excel数据完整性及准确性  start ****************************/
                // 1.excel中数据不能有空值
                if (StringUtils.isBlank(courseCode) 
                		|| StringUtils.isBlank(courseName)
                		|| StringUtils.isBlank(trainingLevels)
                		|| StringUtils.isBlank(trainingCategorys)
                		|| StringUtils.isBlank(degreeTypes)
                		|| StringUtils.isBlank(formLearnings)
                		|| StringUtils.isBlank(conditions))
                {
                	throw new ParameterValidateException(I18nUtil.getMsg("exemptionApply.condition.dataError"));
                }
                
                // 2.培养层次、培养类别、学位类型、学习形式需在字典表中
                String trainingLevelCodes = getCodesByNames(trainingLevels, DictTypeEnum.X_PYCC.getType(), mapList);
            	String trainingCategoryCodes = getCodesByNames(trainingCategorys,DictTypeEnum.X_PYLB.getType(),mapList);
            	String degreeTypeCodes = getCodesByNames(degreeTypes, DictTypeEnum.X_XWLX.getType(), mapList);
            	String formLearningCodes = getCodesByNames(formLearnings, DictTypeEnum.X_XXXS.getType(), mapList);

            	if (StringUtils.isBlank(trainingLevelCodes) 
                		|| StringUtils.isBlank(trainingCategoryCodes)
                		|| StringUtils.isBlank(degreeTypeCodes) 
                		|| StringUtils.isBlank(formLearningCodes)) 
                {
                	throw new ParameterValidateException(I18nUtil.getMsg("exemptionApply.condition.dataError"));
				}
                
                // 3.课程名称和课程编号需在系统中可用
                Course course = new Course();
                course.setCode(courseCode);
                course.setName(courseName);
                Integer courseCount = exemptionApplyConditionSerice.getCourseByCodeOrName(course);
                if (courseCount.intValue() == 0) {
                	throw new ParameterValidateException(I18nUtil.getMsg("exemptionApply.condition.dataError"));
				}
                // ************************* 校验excel数据完整性及准确性  end ****************************/
                
        		Double double1 = Double.valueOf(courseCode);
        		int floor = (int)Math.floor(double1);
        		String courseCodeStr = String.valueOf(floor);
            	condition.setCourseCode(courseCodeStr);
            	
            	condition.setTrainingLevels(trainingLevelCodes);
            	condition.setTrainingCategorys(trainingCategoryCodes);
            	condition.setDegreeTypes(degreeTypeCodes);
            	condition.setFormLearnings(formLearningCodes);
            	
            	condition.setProjId(projectId);
            	condition.setDeleteStatus(Constants.DELETE_FALSE);
            	list.add(condition);
            	this.addExemptionApplyCondition(condition);
            }
        }catch (ParameterValidateException e){
            logger.error(e.getMessage(), e);
            return RestResult.error(e.getMessage());
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            return RestResult.error("解析文件错误" + e.getMessage());
        }
        return RestResult.success();
    }
    
    /**
     * @param names 名称(逗号分隔)
     * @param type  数据字典类型
     * @param mapList 数字字典类型集合
     * @return 
     */
    public String getCodesByNames(String names, String type, Map<String, Map<String, String>> mapList) {
    	String codes = "";
    	if (StringUtils.isNotBlank(names)) {
    		String replace = names.replace("，", ",");
    		String[] splits = replace.split(",");
    		Map<String, String> map = mapList.get(type);
    		Set<Entry<String, String>> set = map.entrySet();
    		
    		StringBuffer buffer = new StringBuffer();
    		
    		for (Entry<String, String> entry : set) {
    			for (String split : splits) {
    				if (entry.getValue().equals(split)) {
    					buffer.append(entry.getKey()).append(",");
    				}
				}
			}
    		String string = buffer.toString();
    		if (StringUtils.isNotBlank(string)) {
    			codes = string.substring(0, string.length()-1);
    		}
		}
    	return codes;
    }
    
    @ApiOperation(value = "根据课程编号查询名称和培养层次")
    @PostMapping("/courseInfo/{code}")
    public RestResult<CourseOpen> queryNameAndTrainingLevelByCode(
    		@PathVariable("code") String code){
    	logger.info("courseInfo by code start");
    	
    	CourseOpen data = exemptionApplyConditionSerice.queryNameAndTrainingLevelByCode(code);
    	return RestResult.successData(data);
    }
   
    @ApiOperation(value = "根据课程编号和学籍信息查询所有符合的申请条件")
    @PostMapping("/matchedConditions")
    public RestResult<?> queryApplyConditionByCourseCodeAndStudentId(
    		@RequestBody(required=false) ExemptionApplyManageVo applyManage){
    	logger.info("matchedConditions by CourseCode and studentCode start:"+applyManage.getStudentCode());
    	
    	List<ExemptionApplyGraduteCondition> list = exemptionApplyConditionSerice.
    			queryApplyConditionByCourseCodeAndStudentId(applyManage.getCourseCode(),applyManage.getStudentCode());
    	Map<String, List<ExemptionApplyGraduteCondition>> collect = list.stream().collect(Collectors.groupingBy(ExemptionApplyGraduteCondition::getConditions));
    	List<ExemptionApplyGraduteCondition> newList = new ArrayList<>();
    	for (Entry<String, List<ExemptionApplyGraduteCondition>> entry : collect.entrySet()) {
			if (CollectionUtil.isNotEmpty(entry.getValue())) {
				newList.add(entry.getValue().get(0));
			}
		}
    	return RestResult.successData(newList);
    }
    
}
