package com.server.edu.election.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.rest.ResultStatus;
import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.service.DictionaryService;
import com.server.edu.election.dto.NoSelectCourseStdsDto;
import com.server.edu.election.entity.ElcNoSelectReason;
import com.server.edu.election.query.ExemptionQuery;
import com.server.edu.election.service.NoSelectStudentService;
import com.server.edu.election.vo.ElcNoSelectReasonVo;
import com.server.edu.election.vo.ExemptionApplyManageVo;
import com.server.edu.util.CollectionUtil;
import com.server.edu.util.ExportUtil;
import com.server.edu.util.excel.ExcelWriterUtil;
import com.server.edu.util.excel.GeneralExcelCell;
import com.server.edu.util.excel.GeneralExcelDesigner;
import com.server.edu.util.excel.GeneralExcelUtil;
import com.server.edu.util.excel.export.ExcelResult;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;


@SwaggerDefinition(info = @Info(title = "未选课学生", version = ""))
@RestSchema(schemaId = "NoSelectStudentController")
@RequestMapping("/reportManagement")
public class NoSelectStudentController
{
    
    private static Logger LOG =
        LoggerFactory.getLogger(NoSelectStudentController.class);
    
    @Value("${cache.directory}")
    private String cacheDirectory;
    
    @Autowired
    private NoSelectStudentService noSelectStudentService;
    
    @Autowired
    private DictionaryService dictionaryService;
    
    @ApiOperation(value = "查询未选课学生名单")
    @PostMapping("/findNoSelectCourseList")
    public RestResult<PageResult<NoSelectCourseStdsDto>> findElectCourseList(
        @RequestBody PageCondition<NoSelectCourseStdsDto> condition)
    {
        PageResult<NoSelectCourseStdsDto> electCourseList =
            noSelectStudentService.findElectCourseList(condition);
        return RestResult.successData(electCourseList);
    }
    
    @ApiOperation(value = "新增未选课原因")
    @PostMapping("/addNoSelectReason")
    public RestResult<String> addNoSelectReason(
        @RequestBody ElcNoSelectReasonVo noSelectReason)
    {
        if (noSelectReason.getCalendarId() == null
            || CollectionUtil.isEmpty(noSelectReason.getStudentIds()))
        {
            return RestResult.fail("common.parameterError");
        }
        String s = noSelectStudentService.addNoSelectReason(noSelectReason);
        return RestResult.success(I18nUtil.getMsg(s, ""));
    }
    
    @ApiOperation(value = "查找未选课原因")
    @PostMapping("/findNoSelectReason")
    public RestResult<ElcNoSelectReason> findNoSelectReason(
        @RequestParam Long calendarId, String studentCode)
    {
        if (calendarId == null || StringUtils.isEmpty(studentCode))
        {
            return RestResult.fail("common.parameterError");
        }
        ElcNoSelectReason noSelectReason =
            noSelectStudentService.findNoSelectReason(calendarId, studentCode);
        return RestResult.successData(noSelectReason);
    }
    
    @ApiOperation(value = "导出未选课学生名单研究生(按查询条件筛选)")
    @PostMapping("/exportStudentNoCourseListGradute")
    public RestResult<String> exportStudentNoCourseListGradute(
            @RequestBody NoSelectCourseStdsDto condition)
                    throws Exception
    {
        LOG.info("export.gradute.start");
        String export = noSelectStudentService.exportStudentNoCourseListGradute(condition);
        return RestResult.successData(export);
    }
    
    @ApiResponses({
        @ApiResponse(code = 200, response = File.class, message = "导出未选课学生名单研究生(按学生ID)")})
    @PostMapping(value = "/exportStudentNoCourseListGradute2")
    public ResponseEntity<Resource> exportStudentNoCourseListGradute2(@RequestBody String ids) throws Exception
    {
//        LOG.info("exportStudentNoCourseListGradute2");
//        try {
//            RestResult<String> restResult = noSelectStudentService.exportStudentNoCourseListGradute2(condition);
//            if (restResult.getCode() == ResultStatus.SUCCESS.code()
//                    && !"".equals(restResult.getData()))
//            {
//            	return new File(restResult.getData());
//            }
//            else
//            {
//                return null;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    	PageCondition<NoSelectCourseStdsDto> page = new PageCondition<NoSelectCourseStdsDto>();
//    	page.setCondition(condition);
//    	page.setPageNum_(1);
//    	page.setPageSize_(1000);
    	
//    	List<NoSelectCourseStdsDto> datas = new ArrayList<NoSelectCourseStdsDto>();
    	List<NoSelectCourseStdsDto> list = noSelectStudentService.findElectCourseListByIds(ids);
//    	while (datas.size() < list.getTotal_())
//    	{
//    		datas.addAll(list.getList());
//    		page.setPageNum_(page.getPageNum_() + 1);
//    		if (datas.size() < list.getTotal_())
//    		{
//    			list = noSelectStudentService.findElectCourseList(page);
//    		}
//    	}
    	
    	GeneralExcelDesigner design = new GeneralExcelDesigner();
    	design.addCell("学号", "studentCode");
    	design.addCell("姓名", "studentName");
    	design.addCell("年级", "grade");
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
    	design.addCell("培养类别", "trainingCategory").setValueHandler(
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
    	design.addCell("学籍变动信息", "stdStatusChanges");
    	design.setDatas(list);
    	ExcelWriterUtil excelUtil = GeneralExcelUtil.generalExcelHandle(design);
    	
    	return ExportUtil
    			.exportExcel(excelUtil, cacheDirectory, "YanJiuShengWeiXuanKeXueShengMingDanLieBiao.xls");
    }
    
    @ApiOperation(value = "导出未选课学生名单")
    @PostMapping("/export")
    public RestResult<ExcelResult> export(
        @RequestBody NoSelectCourseStdsDto condition)
        throws Exception
    {
        LOG.info("export.start");
        ExcelResult result = noSelectStudentService.export(condition);
        return RestResult.successData(result);
    }   
}
