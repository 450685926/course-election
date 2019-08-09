package com.server.edu.election.controller;

import java.io.File;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.server.edu.common.validator.ValidatorUtil;
import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.service.DictionaryService;
import com.server.edu.util.excel.ExcelWriterUtil;
import com.server.edu.util.excel.GeneralExcelCell;
import com.server.edu.util.excel.GeneralExcelDesigner;
import com.server.edu.util.excel.GeneralExcelUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.rest.ResultStatus;
import com.server.edu.election.dto.ExportPreCondition;
import com.server.edu.election.dto.PreViewRollDto;
import com.server.edu.election.dto.PreviewRollBookList;
import com.server.edu.election.dto.ReportManagementCondition;
import com.server.edu.election.dto.RollBookConditionDto;
import com.server.edu.election.dto.StudentSelectCourseList;
import com.server.edu.election.dto.StudnetTimeTable;
import com.server.edu.election.service.ReportManagementService;
import com.server.edu.election.vo.RollBookList;
import com.server.edu.election.vo.StudentSchoolTimetabVo;
import com.server.edu.election.vo.StudentVo;
import com.server.edu.election.vo.TimeTable;
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

/**
 * @description: 报表管理
 * @author: bear
 * @create: 2019-02-14 14:49
 */

@SwaggerDefinition(info = @Info(title = "报表管理 ", version = ""))
@RestSchema(schemaId = "ReportManagementController")
@RequestMapping("/reportManagement")
public class ReportManagementController
{
    
    @Autowired
    private ReportManagementService managementService;

    private DictionaryService dictionaryService;

    private static Logger LOG =
        LoggerFactory.getLogger(ExemptionController.class);

    @Value("${cache.directory}")
    private String cacheDirectory;
    
    
    @ApiOperation(value = "代选课")
    @PostMapping("/otherSelectCourse")
    public RestResult<String> otherSelectCourse(
        @RequestBody StudentSelectCourseList studentSelectCourseList)
    {
        if (studentSelectCourseList == null)
        {
            return RestResult.fail("common.parameterError");
        }
        String s = managementService.otherSelectCourse(studentSelectCourseList);
        return RestResult.success(I18nUtil.getMsg(s, ""));
    }
    
    @ApiOperation(value = "查询点名册")
    @PostMapping("/findRollBookList")
    public RestResult<PageResult<RollBookList>> findRollBookList(
        @RequestBody PageCondition<RollBookConditionDto> condition)
    {
        if (condition.getCondition().getCalendarId() == null)
        {
            return RestResult.fail("common.parameterError");
        }
        PageResult<RollBookList> bookList =
            managementService.findRollBookList(condition);
        return RestResult.successData(bookList);
    }

    @ApiOperation(value = "研究生点名册查询列表")
    @PostMapping("/findGraduteRollBookList")
    public RestResult<PageResult<RollBookList>> findGraduteRollBookList(
            @RequestBody PageCondition<RollBookConditionDto> condition)
    {
        RollBookConditionDto rollBookConditionDto = condition.getCondition();
        if (rollBookConditionDto.getCalendarId() == null)
        {
            return RestResult.fail("common.parameterError");
        }
        Session session = SessionUtils.getCurrentSession();
        PageResult<RollBookList> bookList = null;
        if (session.isAdmin()) {
            bookList = managementService.findRollBookList(condition);
        }else if (session.isAcdemicDean()) {
            rollBookConditionDto.setFaculty(session.getFaculty());
            bookList = managementService.findRollBookList(condition);
        }else if (session.isTeacher()) {
            rollBookConditionDto.setTeacherCode(session.realUid());
            bookList = managementService.findRollBookList(condition);
        }
        return RestResult.successData(bookList);
    }

    @ApiResponses({
            @ApiResponse(code = 200, response = File.class, message = "导出")})
    @PostMapping("/exportGraduteRollBookList")
    public ResponseEntity<Resource> exportGraduteRollBookList(
            @RequestBody PageCondition<RollBookConditionDto> condition)
            throws Exception
    {
        ValidatorUtil.validateAndThrow(condition);
        RestResult<PageResult<RollBookList>> graduteRollBookList = findGraduteRollBookList(condition);
        GeneralExcelDesigner design = graduteRollBookList();
        PageResult<RollBookList> data = graduteRollBookList.getData();
        List<RollBookList> list = new ArrayList<>();
        if (data != null) {
            list = data.getList();
        }
        design.setDatas(list);
        ExcelWriterUtil excelUtil = GeneralExcelUtil.generalExcelHandle(design);
        return ExportUtil.exportExcel(excelUtil, cacheDirectory, "graduateRollBookList.xls");
    }

    @ApiOperation(value = "研究生点名册（学生名单）详情")
    @GetMapping("/previewGraduteRollBook")
    public RestResult<PreViewRollDto> previewGraduteRollBook(
            @RequestParam Long teachingClassId)
    {
        if (teachingClassId == null)
        {
            return RestResult.fail("common.parameterError");
        }
        PreViewRollDto previewRollBookList = managementService
                .previewGraduteRollBook(teachingClassId);
        return RestResult.successData(previewRollBookList);
    }

    @ApiOperation(value = "预览点名册")
    @PostMapping("/previewRollBookList2")
    public RestResult<PreviewRollBookList> findPreviewRollBookList2(
        @RequestBody RollBookList bookList)
    {
        if (bookList.getClassCode() == null
            || bookList.getTeachingClassId() == null)
        {
            return RestResult.fail("common.parameterError");
        }
        PreviewRollBookList previewRollBookList =
            managementService.findPreviewRollBookList(bookList);
        return RestResult.successData(previewRollBookList);
    }
    
    @ApiOperation(value = "预览点名册")
    @GetMapping("/previewRollBookList")
    public RestResult<PreViewRollDto> findPreviewRollBookList(
        @RequestParam Long teachingClassId, @RequestParam Long calendarId)
    {
        LOG.info("findPreviewRollBookList.start");
        if (teachingClassId == null)
        {
            return RestResult.fail("common.parameterError");
        }
        PreViewRollDto previewRollBookList = managementService
            .findPreviewRollBookListById(teachingClassId, calendarId);
        return RestResult.successData(previewRollBookList);
    }
    
    //导出待做
    @ApiOperation(value = "研究生查询学生个人课表")
    @GetMapping("/findSchoolTimetab2")
    public RestResult<StudentSchoolTimetabVo> findSchoolTimetab2(
        @RequestParam Long calendarId, @RequestParam String studentCode)
    {
        if (calendarId == null || studentCode == null)
        {
            return RestResult.fail("common.parameterError");
        }
        StudentSchoolTimetabVo schoolTimetab =
            managementService.findSchoolTimetab2(calendarId, studentCode);
        return RestResult.successData(schoolTimetab);
    }
    
    @ApiOperation(value = "查询学生个人课表")
    @GetMapping("/findStudentTimetab")
    public RestResult<List<StudnetTimeTable>> findStudentTimetab(
        @RequestParam("calendarId") Long calendarId,
        @RequestParam(value = "studentCode", required = false) String studentCode)
    {
        if (calendarId == null)
        {
            return RestResult.fail("common.parameterError");
        }
        if (StringUtils.isBlank(studentCode))
        {//是否学生登陆
            Session currentSession = SessionUtils.getCurrentSession();
            String code = currentSession.realUid();
            int type = currentSession.realType();
            if (type == 2)
            {//当前用户是学生
                studentCode = code;
            }
        }
        
        List<StudnetTimeTable> schoolTimetab =
            managementService.findStudentTimetab(calendarId, studentCode);
        return RestResult.successData(schoolTimetab);
    }
    
    @ApiOperation(value = "查询当前登录学生个人课表")
    @GetMapping("/getStudentTimetab")
    public RestResult<List<TimeTable>> getStudentTimetab(
        @RequestParam("calendarId") Long calendarId,
        @RequestParam("week") Integer week)
    {
        if (calendarId == null)
        {
            return RestResult.fail("common.parameterError");
        }
        Session currentSession = SessionUtils.getCurrentSession();
        String studentCode = currentSession.realUid();
        List<TimeTable> list =
            managementService.getStudentTimetab(calendarId, studentCode, week);
        return RestResult.successData(list);
    }
    
    @ApiOperation(value = "查询所有学生课表")
    @PostMapping("/findAllSchoolTimetab")
    public RestResult<PageResult<StudentVo>> findAllSchoolTimetab(
        @RequestBody PageCondition<ReportManagementCondition> condition)
    {
        PageResult<StudentVo> allSchoolTimetab =
            managementService.findAllSchoolTimetab(condition);
        return RestResult.successData(allSchoolTimetab);
    }

    @ApiOperation(value = "根据用户角色查询学生课表")
    @PostMapping("/findStudentTimeTableByRole")
    public RestResult<PageResult<StudentVo>> findStudentTimeTableByRole(
            @RequestBody PageCondition<ReportManagementCondition> condition)
    {
        LOG.info("findStudentTimeTableByRole.start");
        ReportManagementCondition reportManagementCondition = condition.getCondition();
        Session session = SessionUtils.getCurrentSession();
        PageResult<StudentVo> schoolTimetab = null;
        if (StringUtils.equals(session.getCurrentRole(), "1") && session.isAdmin()) {
            schoolTimetab = managementService.findStudentTimeTableByRole(condition);
        }else if (StringUtils.equals(session.getCurrentRole(), "1") && !session.isAdmin() && session.isAcdemicDean()) {
            reportManagementCondition.setFaculty(session.getFaculty());
            schoolTimetab = managementService.findStudentTimeTableByRole(condition);
        }else if (session.isStudent()) {
            reportManagementCondition.setStudentCode(session.realUid());
            schoolTimetab = managementService.findStudentTimeTableByRole(condition);
        }
        return RestResult.successData(schoolTimetab);
    }
    
    @ApiOperation(value = "导出点名册")
    @PostMapping("/exportRollBookList")
    public RestResult<ExcelResult> exportRollBookList(
        @RequestBody RollBookConditionDto condition)
        throws Exception
    {
        LOG.info("export.start");
        ExcelResult export = managementService.exportRollBookList(condition);
        return RestResult.successData(export);
    }

    /**
     * 从磁盘中下载
     * 
     * @param fileName
     * @return
     * @throws Exception
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "导出excel下载文件")
    @GetMapping("/download2")
    @ApiResponses({
        @ApiResponse(code = 200, response = File.class, message = "导出excel下载文件")})
    public ResponseEntity<Resource> download2(@RequestParam("path") String path)
        throws Exception
    {
        return ExportUtil.export(path, "DianMingCe.xls");
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
    
    @ApiOperation(value = "导出预览点名册")
    @PostMapping("/exportPreRollBookList")
    public RestResult<String> exportPreRollBookList(
        @RequestBody ExportPreCondition condition)
        throws Exception
    {
        LOG.info("exportPreRollBookList.start");
        String fileName = managementService.exportPreRollBookList(condition);
        return RestResult.successData(fileName);
    }

    @GetMapping(value = "/exportStudentTimetabPdf")
    @ApiResponses({
        @ApiResponse(code = 200, response = File.class, message = "导出学生课表pdf--研究生")})
    public ResponseEntity<Resource> exportStudentTimetabPdf(
    		@RequestParam("calendarId") Long calendarId,
    		@RequestParam("calendarName") String calendarName,
    		@RequestParam("studentCode") String studentCode, 
    		@RequestParam("studentName") String studentName) throws Exception{
    	LOG.info("exportStudentTimetabPdf.start");
    	RestResult<String> restResult = managementService.exportStudentTimetabPdf(calendarId, calendarName, studentCode, studentName);
    	
    	if (ResultStatus.SUCCESS.code() == restResult.getCode()
                && !"".equals(restResult.getData()))
            {
                Resource resource = new FileSystemResource(
                    URLDecoder.decode(restResult.getData(), "utf-8"));// 绝对路径
                return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE,
                        "application/pdf; charset=utf-8")
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment;filename="
                            + String.valueOf(
                                URLEncoder.encode(studentName, "UTF-8"))
                            + ".pdf")
                    .body(resource);
            }
            else
            {
                return null;
            }
    }

    /**
     * 点名册excel拼装返回
     * @return
     */
    private GeneralExcelDesigner graduteRollBookList() {
        GeneralExcelDesigner design = new GeneralExcelDesigner();
        design.setNullCellValue("");
        design.addCell("课程序号", "classCode");
        design.addCell("课程代码", "courseCode");
        design.addCell("课程名称", "courseName");
        design.addCell("教学班", "className");
        design.addCell("课程性质", "courseNature").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query("X_KCXZ", value);
                });
        design.addCell("实际人数", "selectCourseNumber");
        design.addCell("人数上限", "numberLimit");
        design.addCell("开课学院", "faculty").setValueHandler(
                (String value, Object rawData, GeneralExcelCell cell) -> {
                    return dictionaryService
                            .query(DictTypeEnum.X_YX.getType(), value);
                });
        design.addCell("教师", "teacherName");
        return design;
    }

}
