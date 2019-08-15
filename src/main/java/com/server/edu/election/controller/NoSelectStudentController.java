package com.server.edu.election.controller;

import java.io.File;
import org.apache.commons.lang3.StringUtils;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.server.edu.election.dto.NoSelectCourseStdsDto;
import com.server.edu.election.entity.ElcNoSelectReason;
import com.server.edu.election.service.NoSelectStudentService;
import com.server.edu.election.vo.ElcNoSelectReasonVo;
import com.server.edu.util.CollectionUtil;
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
    
    @Autowired
    private NoSelectStudentService noSelectStudentService;
    
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
    @GetMapping("/exportStudentNoCourseListGradute2")
    public File exportStudentNoCourseListGradute2(@ModelAttribute NoSelectCourseStdsDto condition)
    {
        LOG.info("exportStudentNoCourseListGradute2");
        try {
            RestResult<String> restResult = noSelectStudentService.exportStudentNoCourseListGradute2(condition);
            if (restResult.getCode() == ResultStatus.SUCCESS.code()
                    && !"".equals(restResult.getData()))
            {
            	return new File(restResult.getData());
            }
            else
            {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
       return null;
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
