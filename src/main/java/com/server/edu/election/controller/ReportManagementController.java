package com.server.edu.election.controller;

import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.rest.ResultStatus;
import com.server.edu.election.dto.*;
import com.server.edu.election.entity.ElcNoSelectReason;
import com.server.edu.election.service.ReportManagementService;
import com.server.edu.election.vo.*;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.CollectionUtil;
import com.server.edu.util.ExportUtil;
import com.server.edu.util.excel.export.ExcelResult;
import com.server.edu.util.excel.export.ExportExcelUtils;
import io.swagger.annotations.*;
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
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

/**
 * @description: 报表管理
 * @author: bear
 * @create: 2019-02-14 14:49
 */

@SwaggerDefinition(info = @Info(title = "报表管理", version = ""))
@RestSchema(schemaId = "ReportManagementController")
@RequestMapping("/reportManagement")
public class ReportManagementController
{
    
    @Autowired
    private ReportManagementService managementService;
    
    private static Logger LOG =
        LoggerFactory.getLogger(ExemptionController.class);
    
    @Value("${cache.directory}")
    private String cacheDirectory;
    
    @ApiOperation(value = "查询未选课学生名单")
    @PostMapping("/findNoSelectCourseList")
    public RestResult<PageResult<NoSelectCourseStdsDto>> findElectCourseList(
        @RequestBody PageCondition<NoSelectCourseStdsDto> condition)
    {
        PageResult<NoSelectCourseStdsDto> electCourseList =
            managementService.findElectCourseList(condition);
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
        String s = managementService.addNoSelectReason(noSelectReason);
        return RestResult.success(I18nUtil.getMsg(s, ""));
    }
    
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
            managementService.findNoSelectReason(calendarId, studentCode);
        return RestResult.successData(noSelectReason);
    }
    
    @ApiOperation(value = "查询点名册")
    @PostMapping("/findRollBookList2")
    public RestResult<PageResult<RollBookList>> findRollBookList2(
        @RequestBody PageCondition<ReportManagementCondition> condition)
    {
        PageResult<RollBookList> bookList =
            managementService.findRollBookList2(condition);
        return RestResult.successData(bookList);
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

    @ApiOperation(value = "导出研究生点名册详情（学生名单）")
    @GetMapping("/exportGraduteRollBook")
    public File exportGraduteRollBook(@RequestParam Long teachingClassId) {
        try {
            RestResult<String> restResult = managementService.exportGraduteRollBook(teachingClassId);
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
    
    @ApiOperation(value = "查询学生课表对应老师时间地点") //不用
    @GetMapping("/findStudentAndTeacherTime")
    public RestResult<List<ClassTeacherDto>> findStudentAndTeacherTime(
        @RequestParam Long teachingClassId)
    {
        if (teachingClassId == null)
        {
            return RestResult.fail("common.parameterError");
        }
        List<ClassTeacherDto> studentAndTeacherTime =
            managementService.findStudentAndTeacherTime(teachingClassId);
        return RestResult.successData(studentAndTeacherTime);
    }
    
    @ApiOperation(value = "查询所有教学班对应老师信息")
    @PostMapping("/findAllClassTeacher")
    public RestResult<PageResult<ClassCodeToTeacher>> findAllClassTeacher(
        @RequestBody PageCondition<ClassCodeToTeacher> condition)
    {
        PageResult<ClassCodeToTeacher> allClassTeacher =
            managementService.findAllClassTeacher(condition);
        return RestResult.successData(allClassTeacher);
    }
    
    @ApiOperation(value = "查询所有教师课表")
    @PostMapping("/findAllTeacherTimeTable")
    public RestResult<PageResult<ClassCodeToTeacher>> findAllTeacherTimeTable(
        @RequestBody PageCondition<ClassCodeToTeacher> condition)
    {
        PageResult<ClassCodeToTeacher> allClassTeacher =
            managementService.findAllTeacherTimeTable(condition);
        return RestResult.successData(allClassTeacher);
    }

    @ApiOperation(value = "根据用户角色查询教师课表")
    @PostMapping("/findTeacherTimeTableByRole")
    public RestResult<PageResult<ClassCodeToTeacher>> findTeacherTimeTableByRole(
            @RequestBody PageCondition<ClassCodeToTeacher> condition)
    {
        ClassCodeToTeacher classCodeToTeacher = condition.getCondition();
        Session session = SessionUtils.getCurrentSession();
        PageResult<ClassCodeToTeacher> classTeacher = null;
        if (StringUtils.equals(session.getCurrentRole(), "1") && session.isAdmin()) {
            classTeacher = managementService.findTeacherTimeTableByRole(condition);
        }else if (StringUtils.equals(session.getCurrentRole(), "1") && !session.isAdmin() && session.isAcdemicDean()) {
            classCodeToTeacher.setFaculty(session.getFaculty());
            classTeacher = managementService.findTeacherTimeTableByRole(condition);
        }else if (StringUtils.equals(session.getCurrentRole(), "2")) {
            classCodeToTeacher.setTeacherCode(session.realUid());
            classTeacher = managementService.findTeacherTimeTableByRole(condition);
        }
        return RestResult.successData(classTeacher);
    }
    
    @ApiOperation(value = "研究生查询教师课表")
    @GetMapping("/findTeacherTimetable2")
    public RestResult<StudentSchoolTimetabVo> findTeacherTimetable2(
        @RequestParam Long calendarId, @RequestParam String teacherCode)
    {
        if (calendarId == null || StringUtils.isBlank(teacherCode))
        {
            return RestResult.fail("common.parameterError");
        }
        StudentSchoolTimetabVo teacherTimetable =
            managementService.findTeacherTimetable2(calendarId, teacherCode);
        return RestResult.successData(teacherTimetable);
    }
    
    @ApiOperation(value = "查询老师课表")
    @GetMapping("/findTeacherTimetable")
    public RestResult<List<TeacherTimeTable>> findTeacherTimetable(
        @RequestParam Long calendarId, @RequestParam String teacherCode)
    {
        if (calendarId == null || StringUtils.isBlank(teacherCode))
        {
            return RestResult.fail("common.parameterError");
        }
        List<TeacherTimeTable> teacherTimetable =
            managementService.findTeacherTimetable(calendarId, teacherCode);
        return RestResult.successData(teacherTimetable);
    }
    
    @ApiOperation(value = "查询当前登录用户的老师课表")
    @GetMapping("/getTeacherTimetable")
    public RestResult<List<TimeTable>> getTeacherTimetable(
        @RequestParam Long calendarId, @RequestParam("week") Integer week)
    {
        if (calendarId == null)
        {
            return RestResult.fail("common.parameterError");
        }
        Session currentSession = SessionUtils.getCurrentSession();
        String teacherCode = currentSession.realUid();
        List<TimeTable> teacherTimetable = managementService
            .getTeacherTimetable(calendarId, teacherCode, week);
        return RestResult.successData(teacherTimetable);
    }
    
    @ApiOperation(value = "导出未选课学生名单本科生")
    @PostMapping("/exportStudentNoCourseList2")
    public RestResult<String> exportStudentNoCourseList(
        @RequestBody NoSelectCourseStdsDto condition)
        throws Exception
    {
        LOG.info("export.start");
        String export = managementService.exportStudentNoCourseList(condition);
        return RestResult.successData(export);
    }

    @ApiOperation(value = "导出未选课学生名单研究生")
    @PostMapping("/exportStudentNoCourseListGradute")
    public RestResult<String> exportStudentNoCourseListGradute(
    		@RequestBody NoSelectCourseStdsDto condition)
    				throws Exception
    {
    	LOG.info("export.gradute.start");
    	String export = managementService.exportStudentNoCourseListGradute(condition);
    	return RestResult.successData(export);
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

    @ApiOperation(value = "导出研究生点名册")
    @GetMapping("/exportGraduteRollBookList")
    public File exportGraduteRollBookList(
            @ModelAttribute RollBookConditionDto rollBookConditionDto)
            throws Exception
    {
        LOG.info("export.start");
        Session session = SessionUtils.getCurrentSession();
       if (session.isAcdemicDean()) {
            rollBookConditionDto.setFaculty(session.getFaculty());
        }else if (session.isTeacher()) {
            rollBookConditionDto.setTeacherCode(session.realUid());
        }
        try {
            RestResult<String> restResult = managementService.exportGraduteRollBookList(rollBookConditionDto);
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
    
    @ApiOperation(value = "导出未选课学生名单")
    @PostMapping("/export")
    public RestResult<ExcelResult> export(
        @RequestBody NoSelectCourseStdsDto condition)
        throws Exception
    {
        LOG.info("export.start");
        ExcelResult result = managementService.export(condition);
        return RestResult.successData(result);
    }
    
    @ApiOperation(value = "导出所有教师课表")
    @PostMapping("/exportTeacher")
    public RestResult<ExcelResult> exportTeacher(
        @RequestBody ClassCodeToTeacher condition)
        throws Exception
    {
        LOG.info("export.start");
        ExcelResult result = managementService.exportTeacher(condition);
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
    
    @GetMapping(value = "/exportTeacherTimetabPdf")
    @ApiResponses({
    	@ApiResponse(code = 200, response = File.class, message = "导出教师课表pdf--研究生")})
    public ResponseEntity<Resource> exportTeacherTimetabPdf(
    		@RequestParam("calendarId") Long calendarId,
    		@RequestParam("calendarName") String calendarName,
    		@RequestParam("teacherCode") String teacherCode, 
    		@RequestParam("teacherName") String teacherName) throws Exception{
    	LOG.info("exportTeacherTimetabPdf.start");
    	
    	StringBuffer name = new StringBuffer();
    	RestResult<String> restResult = managementService.exportTeacherTimetabPdf(calendarId, calendarName, teacherCode,teacherName);
    	
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
    										URLEncoder.encode(name.toString(), "UTF-8"))
    								+ ".pdf")
    				.body(resource);
    	}
    	else
    	{
    		return null;
    	}
    }

}
