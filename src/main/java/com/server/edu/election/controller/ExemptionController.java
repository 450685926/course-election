package com.server.edu.election.controller;

import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.log.LogRecord;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.rest.ResultStatus;
import com.server.edu.common.validator.ValidatorUtil;
import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.service.DictionaryService;
import com.server.edu.dmskafka.entity.AuditType;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dto.ExemptionApplyCondition;
import com.server.edu.election.dto.ExemptionCourseScoreDto;
import com.server.edu.election.dto.Student4Elc;
import com.server.edu.election.entity.ExemptionApplyManage;
import com.server.edu.election.entity.ExemptionCourse;
import com.server.edu.election.entity.ExemptionCourseScore;
import com.server.edu.election.entity.Student;
import com.server.edu.election.query.ElecRoundStuQuery;
import com.server.edu.election.query.ExemptionQuery;
import com.server.edu.election.service.ExemptionCourseService;
import com.server.edu.election.vo.ExemptionApplyManageVo;
import com.server.edu.election.vo.ExemptionCourseMaterialVo;
import com.server.edu.election.vo.ExemptionCourseRuleVo;
import com.server.edu.election.vo.ExemptionCourseScoreVo;
import com.server.edu.election.vo.ExemptionCourseVo;
import com.server.edu.election.vo.ExemptionStudentCountVo;
import com.server.edu.election.vo.StudentAndCourseVo;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.ExportUtil;
import com.server.edu.util.excel.ExcelWriterUtil;
import com.server.edu.util.excel.GeneralExcelCell;
import com.server.edu.util.excel.GeneralExcelDesigner;
import com.server.edu.util.excel.GeneralExcelUtil;
import com.server.edu.util.excel.parse.ExcelParseConfig;
import com.server.edu.util.excel.parse.ExcelParseDesigner;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

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
    @Autowired
    private DictionaryService dictionaryService;

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
    public RestResult<PageResult<ExemptionCourseVo>> findMasterCourse(@RequestBody PageCondition<ExemptionCourseVo> condition) {
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
    public RestResult<PageResult<ExemptionCourseRuleVo>> findExemptionCourseRule(@RequestBody PageCondition<ExemptionCourseRuleVo> courseRule){
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
    public RestResult<String> addExemptionCourseRule(@RequestBody ExemptionCourseRuleVo courseRuleVo){
        String s = exemptionCourseService.addExemptionCourseRule(courseRuleVo);
        return RestResult.success(I18nUtil.getMsg(s,""));
    }

    //修改免修免考规则todo

    @LogRecord(title="编辑免修免考申请规则",type = AuditType.UPDATE)
    @ApiOperation(value = "新增免修免考申请规则")
    @PostMapping("/editExemptionCourseRule")
    public RestResult<String> editExemptionCourseRule(@RequestBody ExemptionCourseRuleVo courseRuleVo){
        String s = exemptionCourseService.editExemptionCourseRule(courseRuleVo);
        return RestResult.success(I18nUtil.getMsg(s,""));
    }


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
    	Session session = SessionUtils.getCurrentSession();
    	applyManage.setManagerDeptId(session.getCurrentManageDptId());
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
    public RestResult<?> deleteExemptionApply(@RequestBody List<Long>  ids){
    	Session session = SessionUtils.getCurrentSession();
    	if (!StringUtils.equalsIgnoreCase(session.getCurrentManageDptId(), Constants.PROJ_UNGRADUATE)) {
    		RestResult<?> result= exemptionCourseService.deleteGraduteExemptionApply(ids);
    		return result;
		}else{
			String s= exemptionCourseService.deleteExemptionApply(ids);
			return RestResult.success(I18nUtil.getMsg(s,""));
		}
    }

    @LogRecord(title="审批免修免考申请",type = AuditType.UPDATE)
    @ApiOperation(value = "审批免修免考申请")
    @PostMapping("/approvalExemptionApply")
    public RestResult<String> approvalExemptionApply(@RequestBody List<Long>  ids,@RequestParam Integer status,@RequestParam String auditor){
        String s= exemptionCourseService.approvalExemptionApply(ids,status,auditor);
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

    /**
    *@Description: 导入成绩
    *@Param:
    *@return: 
    *@Author: bear
    *@date: 2019/3/1 10:24
    */
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

    /**
    *@Description: 下载成绩模板
    *@Param:
    *@return: 
    *@Author: bear
    *@date: 2019/3/1 10:25
    */
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

    /**
    *@Description: 免修申请模板
    *@Param:
    *@return: 
    *@Author: bear
    *@date: 2019/3/1 10:24
    */

    @ApiResponses({
            @ApiResponse(code = 200, response = File.class, message = "下载模版")})
    @GetMapping(value = "/downloadApplyTemplate")
    public ResponseEntity<Resource> downloadApplyTemplate()
    {
        Resource resource = new ClassPathResource("/excel/mianXiuApply.xls");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/vnd.ms-excel")
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment;filename=" + "mianXiuApply.xls")
                .body(resource);
    }

    /**
    *@Description: 导入免修申请
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/3/1 10:44
    */
    @PostMapping(value = "/uploadApply")
    public RestResult<?> uploadApply(@RequestPart(name = "file") MultipartFile file,
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
            designer.getConfigs().add(new ExcelParseConfig("applyType", 2){
                public Object handler(String value)
                {
                    if(StringUtils.isNotBlank(value)){
                        return Integer.valueOf(value);
                    }
                    return null;
                }
            });
            List<ExemptionApplyManage> datas = GeneralExcelUtil
                    .parseExcel(workbook, designer, ExemptionApplyManage.class);

            String msg =exemptionCourseService.addExcelApply(datas,calendarId);
            return RestResult.success(msg);

        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return RestResult.error("解析文件错误" + e.getMessage());
        }
    }



    @ApiOperation(value = "免修免考下拉代码取值")
    @PostMapping("/findCourseCode")
    public RestResult<List<ExemptionCourseVo>> findCourseCode(@RequestBody ExemptionCourseRuleVo courseRuleVo,@RequestParam Integer applyType){
        if(courseRuleVo.getCalendarId()==null||applyType==null){
            return RestResult.fail("common.parameterError");
        }
        return exemptionCourseService.filterCourseCode(courseRuleVo,applyType);
    }
    
    /**
     * 免修免考统计
     */
    @ApiOperation(value = "免修免考统计")
    @PostMapping("/exemptionCount")
    public RestResult<PageResult<ExemptionStudentCountVo>> exemptionCount(@RequestBody PageCondition<ExemptionQuery> page){
    	ValidatorUtil.validateAndThrow(page.getCondition());
    	Session session = SessionUtils.getCurrentSession();
        if (!session.isAdmin()) {
        	return RestResult.fail("elec.mustBeAdmin");
		}
		Session currentSession = SessionUtils.getCurrentSession();
        String dptId = currentSession.getCurrentManageDptId();
        page.getCondition().setProjectId(dptId);
        PageResult<ExemptionStudentCountVo> countResult = exemptionCourseService.exemptionCount(page); 
        return RestResult.successData(countResult);
    }
    
    /**
     * @throws Exception 
     * 免修免考
     */
    @LogRecord(title="研究生审批免修免考申请",type = AuditType.UPDATE)
    @ApiOperation(value = "研究生审批免修免考申请")
    @PostMapping("/approvalGraduteExemptionApply")
    public RestResult<String> approvalGraduateExemptionApply(@RequestBody List<Long>  ids,@RequestParam Integer status){
        String s= exemptionCourseService.approvalGraduateExemptionApply(ids,status);
        return RestResult.success(I18nUtil.getMsg(s,""));
    }
    
    /**
    *@Description: 查询免修免考审批列表
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/1/31 9:29
    */
    @ApiOperation(value = "查询免修免考审批列表")
    @PostMapping("/findGraduateExemptionApply")
    public RestResult<PageResult<ExemptionApplyManageVo>> findGraduateExemptionApply(@RequestBody PageCondition<ExemptionQuery> condition) {
    	Session currentSession = SessionUtils.getCurrentSession();
		String dptId = currentSession.getCurrentManageDptId();
		condition.getCondition().setProjectId(dptId);
    	PageResult<ExemptionApplyManageVo> exemptionCourse = exemptionCourseService.findGraduateExemptionApply(condition);
        return RestResult.successData(exemptionCourse);
    }
    
//    @ApiOperation(value = "研究生免修免考统计导出")
//    @PostMapping("/exemptionCountExport")
//    public File exemptionCountExport(
//    		@RequestBody ExemptionQuery page)
//    {
//    	try {
//    		ValidatorUtil.validateAndThrow(page);
//    		LOG.info("export.start");
//    		RestResult<String> restResult = exemptionCourseService.exemptionCountExport(page);
//    		
//    		if (restResult.getCode() == ResultStatus.SUCCESS.code()
//    				&& !"".equals(restResult.getData()))
//    		{
//    			return new File(restResult.getData());
//    		}
//    		else
//    		{
//    			return null;
//    		}
//    		
//    	} catch (Exception e) {
//    		e.printStackTrace();
//    	}
//    	return null;
//    	
//    }
    @ApiResponses({
        @ApiResponse(code = 200, response = File.class, message = "研究生免修免考统计导出")})
    @PostMapping(value = "/exemptionCountExport")
    public ResponseEntity<Resource> exemptionCountExport(
        @RequestBody ExemptionQuery condition)
        throws Exception
    {
    	Session currentSession = SessionUtils.getCurrentSession();
//		String dptId = currentSession.getCurrentManageDptId();
    	condition.setProjectId("2");
        
        PageCondition<ExemptionQuery> page = new PageCondition<>();
        page.setCondition(condition);
        page.setPageNum_(1);
        page.setPageSize_(1000);
        
        List<ExemptionStudentCountVo> datas = new ArrayList<>();
        
        PageResult<ExemptionStudentCountVo> res = exemptionCourseService.exemptionCount(page);
        while (datas.size() < res.getTotal_())
        {
            datas.addAll(res.getList());
            page.setPageNum_(page.getPageNum_() + 1);
            if (datas.size() < res.getTotal_())
            {
                res = exemptionCourseService.exemptionCount(page);
            }
        }
        
        GeneralExcelDesigner design = new GeneralExcelDesigner();
        design.addCell("课程代码", "courseCode");
        design.addCell("课程名称", "courseName");
        design.addCell("培养层次", "trainingLevel").setValueHandler(
                (String value, Object rawData, GeneralExcelCell cell) -> {
                    String dict = dictionaryService
                        .query(DictTypeEnum.X_PYCC.getType(), value);
                    return dict;
                });
        design.addCell("培养类别", "degreeCategory").setValueHandler(
                (String value, Object rawData, GeneralExcelCell cell) -> {
                    String dict = dictionaryService
                        .query(DictTypeEnum.X_PYLB.getType(), value);
                    return dict;
                });
        design.addCell("学位类型", "degreeType").setValueHandler(
                (String value, Object rawData, GeneralExcelCell cell) -> {
                    String dict = dictionaryService
                        .query(DictTypeEnum.X_XWLX.getType(), value);
                    return dict;
                });
        design.addCell("学习形式", "formLearning").setValueHandler(
                (String value, Object rawData, GeneralExcelCell cell) -> {
                    String dict = dictionaryService
                        .query(DictTypeEnum.X_XXXS.getType(), value);
                    return dict;
                });
        design.addCell("申请条件", "applyCondition");
        design.addCell("申请人数", "applyNum");
        
        design.setDatas(datas);
        ExcelWriterUtil excelUtil = GeneralExcelUtil.generalExcelHandle(design);
        
        return ExportUtil
            .exportExcel(excelUtil, cacheDirectory, "MianXiuMianKaoTongJi.xls");
    }
    
//    @ApiOperation(value = "研究生审批免修免考导出")
//    @GetMapping("/findGraduateExemptionApplyExport")
//    public File findGraduateExemptionApplyExport(
//    		@ModelAttribute ExemptionQuery page)
//    {
//    	Session currentSession = SessionUtils.getCurrentSession();
////		String dptId = currentSession.getCurrentManageDptId();
//		page.setProjectId("2");
//    	try {
//    		ValidatorUtil.validateAndThrow(page);
//        	LOG.info("export.start");
//        	RestResult<String> restResult = exemptionCourseService.findGraduateExemptionApplyExport(page);
//
//            if (restResult.getCode() == ResultStatus.SUCCESS.code()
//                    && !"".equals(restResult.getData()))
//            {
//            	return new File(restResult.getData());
//            }
//            else
//            {
//                return null;
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//       return null;
//    }
    
    @ApiResponses({
        @ApiResponse(code = 200, response = File.class, message = "研究生审批免修免考导出")})
    @PostMapping(value = "/findGraduateExemptionApplyExport")
    public ResponseEntity<Resource> findGraduateExemptionApplyExport(
        @RequestBody ExemptionQuery condition)
        throws Exception
    {
    	Session currentSession = SessionUtils.getCurrentSession();
//		String dptId = currentSession.getCurrentManageDptId();
    	condition.setProjectId("2");
        
        PageCondition<ExemptionQuery> page = new PageCondition<>();
        page.setCondition(condition);
        page.setPageNum_(1);
        page.setPageSize_(1000);
        
        List<ExemptionApplyManageVo> datas = new ArrayList<>();
        
        PageResult<ExemptionApplyManageVo> res = exemptionCourseService.findGraduateExemptionApply(page);
        while (datas.size() < res.getTotal_())
        {
            datas.addAll(res.getList());
            page.setPageNum_(page.getPageNum_() + 1);
            if (datas.size() < res.getTotal_())
            {
                res = exemptionCourseService.findGraduateExemptionApply(page);
            }
        }
        
        GeneralExcelDesigner design = new GeneralExcelDesigner();
        design.addCell("学号", "studentCode");
        design.addCell("姓名", "name");
        design.addCell("学院", "faculty").setValueHandler(
        		(String value, Object rawData, GeneralExcelCell cell) -> {
        			String dict = dictionaryService
        					.query(DictTypeEnum.X_YX.getType(), value);
        			return dict;
        		});
        design.addCell("专业", "profession").setValueHandler(
        		(String value, Object rawData, GeneralExcelCell cell) -> {
        			String dict = dictionaryService
        					.query(DictTypeEnum.G_ZY.getType(), value);
        			return dict;
        		});
        design.addCell("培养层次", "trainingLevel").setValueHandler(
        		(String value, Object rawData, GeneralExcelCell cell) -> {
        			String dict = dictionaryService
        					.query(DictTypeEnum.X_PYCC.getType(), value);
        			return dict;
        		});
        design.addCell("申请课程", "applyCourse");
        design.addCell("审核状态", "examineResult").setValueHandler(
                (String value, Object rawData, GeneralExcelCell cell) -> {
                    if ("0".equals(value))
                    {
                        return "未审核";
                    }
                    else if ("1".equals(value))
                    {
                        return "审核通过";
                    }
                    else if ("2".equals(value))
                    {
                        return "审核未通过";
                    }
                    return value;
                });
        design.setDatas(datas);
        ExcelWriterUtil excelUtil = GeneralExcelUtil.generalExcelHandle(design);
        
        return ExportUtil
            .exportExcel(excelUtil, cacheDirectory, "YanJiuShengMianXiuMianKaoShenHeLieBiao.xls");
    }
    /**
     * 根据学生Id与课程编码找出学生课程
     */
    @ApiOperation(value = "管理员免修免考添加下拉")
    @GetMapping("/findCourseAdd")
    public RestResult<StudentAndCourseVo> findCourseCodeAdd(
    		@RequestParam  String studentId,
    		@RequestParam  Long calendarId){
    	StudentAndCourseVo result = exemptionCourseService.findCourseCode(studentId,calendarId);
        return RestResult.successData(result);
    }
    
    /**
     * 根据学生Id与课程编码找出学生课程
     */
    @ApiOperation(value = "管理员添加免修免考")
    @PostMapping("/adminAddApply")
    public RestResult<String> adminAddApply(
    		@RequestBody ExemptionApplyManage applyManage){
    	Session session = SessionUtils.getCurrentSession();
    	applyManage.setManagerDeptId(session.getCurrentManageDptId());
    	String s = exemptionCourseService.adminAddApply(applyManage);
        return RestResult.success(I18nUtil.getMsg(s,""));
    }
    
    /**
     * 研究生免修免考列表
     */
    @ApiOperation(value = "研究生免修免考申请列表")
    @GetMapping("/findStudentCourse")
    public RestResult<StudentAndCourseVo> findStudentCourse(@RequestParam Long calendarId){
    	
    	Session session = SessionUtils.getCurrentSession();
    	if (!session.isStudent()) {
    		return RestResult.fail("elec.mustBeStu");
		}
    	String studentId = session.realUid();
    	
    	StudentAndCourseVo result = exemptionCourseService.findStudentApplyCourse(studentId,calendarId);
    	return RestResult.successData(result);
    } 
    
    @LogRecord(title="新增研究生免修免考申请",type = AuditType.INSERT)
    @ApiOperation(value = "新增免修免考申请管理")
    @PostMapping("/addGraduteExemptionApply")
    public RestResult<?> addGraduteExemptionApply(@RequestBody ExemptionApplyManage applyManage){
    	Session session = SessionUtils.getCurrentSession();
    	applyManage.setManagerDeptId(session.getCurrentManageDptId());
    	applyManage.setStudentCode(session.realUid());
    	applyManage.setName(session.realName());
    	RestResult<?> result = exemptionCourseService.addGraduateExemptionApply(applyManage);
        return result;
    }
    
    
}
