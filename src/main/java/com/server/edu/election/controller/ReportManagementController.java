package com.server.edu.election.controller;

import java.io.File;
import java.net.URLDecoder;
import java.util.List;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.dto.ClassCodeToTeacher;
import com.server.edu.election.dto.ClassTeacherDto;
import com.server.edu.election.dto.ExportPreCondition;
import com.server.edu.election.dto.NoSelectCourseStdsDto;
import com.server.edu.election.dto.PreViewRollDto;
import com.server.edu.election.dto.PreviewRollBookList;
import com.server.edu.election.dto.ReportManagementCondition;
import com.server.edu.election.dto.RollBookConditionDto;
import com.server.edu.election.dto.StudentSelectCourseList;
import com.server.edu.election.dto.StudnetTimeTable;
import com.server.edu.election.dto.TeacherTimeTable;
import com.server.edu.election.entity.ElcNoSelectReason;
import com.server.edu.election.service.ReportManagementService;
import com.server.edu.election.vo.ElcNoSelectReasonVo;
import com.server.edu.election.vo.RollBookList;
import com.server.edu.election.vo.StudentSchoolTimetabVo;
import com.server.edu.election.vo.StudentVo;
import com.server.edu.election.vo.TimeTable;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.CollectionUtil;
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

@SwaggerDefinition(info = @Info(title = "报表管理", version = ""))
@RestSchema(schemaId = "ReportManagementController")
@RequestMapping("/reportManagement")
public class ReportManagementController {

    @Autowired
    private ReportManagementService managementService;

    private static Logger LOG =
            LoggerFactory.getLogger(ExemptionController.class);

    @Value("${cache.directory}")
    private String cacheDirectory;


    @ApiOperation(value = "查询未选课学生名单")
    @PostMapping("/findNoSelectCourseList")
    public RestResult<PageResult<NoSelectCourseStdsDto>> findElectCourseList(@RequestBody PageCondition<NoSelectCourseStdsDto> condition) {
        PageResult<NoSelectCourseStdsDto> electCourseList = managementService.findElectCourseList(condition);
        return RestResult.successData(electCourseList);
    }

    @ApiOperation(value = "新增未选课原因")
    @PostMapping("/addNoSelectReason")
    public RestResult<String> addNoSelectReason(@RequestBody ElcNoSelectReasonVo noSelectReason) {
        if(noSelectReason.getCalendarId()==null|| CollectionUtil.isEmpty(noSelectReason.getStudentIds())){
            return RestResult.fail("common.parameterError");
        }
        String s = managementService.addNoSelectReason(noSelectReason);
        return RestResult.success(I18nUtil.getMsg(s,""));
    }

    @ApiOperation(value = "代选课")
    @PostMapping("/otherSelectCourse")
    public RestResult<String> otherSelectCourse(@RequestBody StudentSelectCourseList studentSelectCourseList) {
        if(studentSelectCourseList==null){
            return RestResult.fail("common.parameterError");
        }
        String s= managementService.otherSelectCourse(studentSelectCourseList);
        return RestResult.success(I18nUtil.getMsg(s,""));
    }

    @ApiOperation(value = "查找未选课原因")
    @PostMapping("/findNoSelectReason")
    public RestResult<ElcNoSelectReason> findNoSelectReason(@RequestParam Long calendarId,String studentCode) {
        if(calendarId==null||StringUtils.isEmpty(studentCode)){
            return RestResult.fail("common.parameterError");
        }
        ElcNoSelectReason noSelectReason = managementService.findNoSelectReason(calendarId, studentCode);
        return RestResult.successData(noSelectReason);
    }

    @ApiOperation(value = "查询点名册")
    @PostMapping("/findRollBookList2")
    public RestResult<PageResult<RollBookList>> findRollBookList2(@RequestBody PageCondition<ReportManagementCondition> condition){
        PageResult<RollBookList> bookList = managementService.findRollBookList2(condition);
        return RestResult.successData(bookList);
    }

    @ApiOperation(value = "查询点名册")
    @PostMapping("/findRollBookList")
    public RestResult<PageResult<RollBookList>> findRollBookList(@RequestBody PageCondition<RollBookConditionDto> condition){
        if(condition.getCondition().getCalendarId()==null){
            return RestResult.fail("common.parameterError");
        }
        PageResult<RollBookList> bookList = managementService.findRollBookList(condition);
        return RestResult.successData(bookList);
    }


    @ApiOperation(value = "预览点名册")
    @PostMapping("/previewRollBookList2")
    public RestResult<PreviewRollBookList> findPreviewRollBookList2(@RequestBody RollBookList bookList){
        if(bookList.getClassCode()==null||bookList.getTeachingClassId()==null){
            return RestResult.fail("common.parameterError");
        }
        PreviewRollBookList previewRollBookList = managementService.findPreviewRollBookList(bookList);
        return RestResult.successData(previewRollBookList);
    }


    @ApiOperation(value = "预览点名册")
    @GetMapping("/previewRollBookList")
    public RestResult<PreViewRollDto> findPreviewRollBookList(@RequestParam Long teachingClassId,@RequestParam Long calendarId){
        if(teachingClassId==null){
            return RestResult.fail("common.parameterError");
        }
        PreViewRollDto previewRollBookList = managementService.findPreviewRollBookListById(teachingClassId,calendarId);
        return RestResult.successData(previewRollBookList);
    }


    //导出待做

    @ApiOperation(value = "查询学生个人课表")
    @GetMapping("/findSchoolTimetab2")
    public RestResult<StudentSchoolTimetabVo> findSchoolTimetab2(@RequestParam Long calendarId,@RequestParam String studentCode){
        if(calendarId==null||studentCode==null){
            return RestResult.fail("common.parameterError");
        }
        StudentSchoolTimetabVo schoolTimetab = managementService.findSchoolTimetab(calendarId, studentCode);
        return RestResult.successData(schoolTimetab);
    }

    @ApiOperation(value = "查询学生个人课表")
    @GetMapping("/findStudentTimetab")
    public RestResult<List<StudnetTimeTable>> findStudentTimetab(@RequestParam("calendarId") Long calendarId,@RequestParam(value="studentCode",required = false) String studentCode){
        if(calendarId==null){
            return RestResult.fail("common.parameterError");
        }
        if(StringUtils.isBlank(studentCode)){//是否学生登陆
            Session currentSession = SessionUtils.getCurrentSession();
            String code = currentSession.realUid();
            int type = currentSession.realType();
            if(type==2){//当前用户是学生
                studentCode=code;
            }
        }

        List<StudnetTimeTable> schoolTimetab = managementService.findStudentTimetab(calendarId, studentCode);
        return RestResult.successData(schoolTimetab);
    }

    @ApiOperation(value = "查询当前登录学生个人课表")
    @GetMapping("/getStudentTimetab")
    public RestResult<List<TimeTable>> getStudentTimetab(@RequestParam("calendarId") Long calendarId, 
        @RequestParam("week") Integer week){
        if(calendarId==null){
            return RestResult.fail("common.parameterError");
        }
        Session currentSession = SessionUtils.getCurrentSession();
        String studentCode = currentSession.realUid();
       List<TimeTable> list = managementService.getStudentTimetab(calendarId,studentCode, week);
        return RestResult.successData(list);
    }


    @ApiOperation(value = "查询所有学生课表")
    @PostMapping("/findAllSchoolTimetab")
    public RestResult<PageResult<StudentVo>> findAllSchoolTimetab(@RequestBody PageCondition<ReportManagementCondition> condition){
        PageResult<StudentVo> allSchoolTimetab = managementService.findAllSchoolTimetab(condition);
        return RestResult.successData(allSchoolTimetab);
    }


    @ApiOperation(value = "查询学生课表对应老师时间地点")//不用
    @GetMapping("/findStudentAndTeacherTime")
    public RestResult<List<ClassTeacherDto>> findStudentAndTeacherTime(@RequestParam Long teachingClassId){
        if(teachingClassId==null){
            return RestResult.fail("common.parameterError");
        }
        List<ClassTeacherDto> studentAndTeacherTime = managementService.findStudentAndTeacherTime(teachingClassId);
        return RestResult.successData(studentAndTeacherTime);
    }


    @ApiOperation(value = "查询所有教学班对应老师信息")
    @PostMapping("/findAllClassTeacher")
    public RestResult<PageResult<ClassCodeToTeacher>> findAllClassTeacher(@RequestBody PageCondition<ClassCodeToTeacher> condition){
        PageResult<ClassCodeToTeacher> allClassTeacher = managementService.findAllClassTeacher(condition);
        return RestResult.successData(allClassTeacher);
    }

    @ApiOperation(value = "查询所有教师课表")
    @PostMapping("/findAllTeacherTimeTable")
    public RestResult<PageResult<ClassCodeToTeacher>> findAllTeacherTimeTable(@RequestBody PageCondition<ClassCodeToTeacher> condition){
        PageResult<ClassCodeToTeacher> allClassTeacher = managementService.findAllTeacherTimeTable(condition);
        return RestResult.successData(allClassTeacher);
    }

    //学生课表调用预览点名册

    @ApiOperation(value = "查询老师课表")
    @GetMapping("/findTeacherTimetable2")
    public RestResult<StudentSchoolTimetabVo> findTeacherTimetable2(@RequestParam Long calendarId,@RequestParam String teacherCode){
        if(calendarId==null|| StringUtils.isBlank(teacherCode)){
            return RestResult.fail("common.parameterError");
        }
        StudentSchoolTimetabVo teacherTimetable = managementService.findTeacherTimetable2(calendarId, teacherCode);
        return RestResult.successData(teacherTimetable);
    }

    @ApiOperation(value = "查询老师课表")
    @GetMapping("/findTeacherTimetable")
    public RestResult<List<TeacherTimeTable>> findTeacherTimetable(@RequestParam Long calendarId,@RequestParam String teacherCode){
        if(calendarId==null|| StringUtils.isBlank(teacherCode)){
            return RestResult.fail("common.parameterError");
        }
        List<TeacherTimeTable> teacherTimetable = managementService.findTeacherTimetable(calendarId, teacherCode);
        return RestResult.successData(teacherTimetable);
    }

    @ApiOperation(value = "查询当前登录用户的老师课表")
    @GetMapping("/getTeacherTimetable")
    public RestResult<List<TimeTable>> getTeacherTimetable(@RequestParam Long calendarId, 
        @RequestParam("week") Integer week){
        if(calendarId==null){
            return RestResult.fail("common.parameterError");
        }
        Session currentSession = SessionUtils.getCurrentSession();
        String teacherCode = currentSession.realUid();
        List<TimeTable> teacherTimetable = managementService.getTeacherTimetable(calendarId, teacherCode, week);
        return RestResult.successData(teacherTimetable);
    }

    @ApiOperation(value = "导出未选课学生名单")
    @PostMapping("/exportStudentNoCourseList2")
    public RestResult<String> exportStudentNoCourseList (
            @RequestBody NoSelectCourseStdsDto condition)
            throws Exception
    {
        LOG.info("export.start");
        String export = managementService.exportStudentNoCourseList(condition);
        return RestResult.successData(export);
    }

    @ApiOperation(value = "导出点名册")
    @PostMapping("/exportRollBookList")
    public RestResult<String> exportRollBookList (
            @RequestBody ReportManagementCondition condition)
            throws Exception
    {
        LOG.info("export.start");
        String export = managementService.exportRollBookList(condition);
        return RestResult.successData(export);
    }



    @ApiOperation(value = "导出excel下载文件")
    @GetMapping("/download2")
    @ApiResponses({@ApiResponse(code = 200, response = File.class, message = "导出excel下载文件")})
    public ResponseEntity<Resource> download2(@RequestParam("fileName") String fileName) throws Exception
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



    @ApiOperation(value = "导出未选课学生名单")
    @PostMapping("/export")
    public RestResult<ExcelResult> export(@RequestBody NoSelectCourseStdsDto condition)
            throws Exception {
        LOG.info("export.start");
        ExcelResult result = managementService.export(condition);
        return RestResult.successData(result);
    }

    @ApiOperation(value = "导出所有教师课表")
    @PostMapping("/exportTeacher")
    public RestResult<ExcelResult> exportTeacher(@RequestBody ClassCodeToTeacher condition)
            throws Exception {
        LOG.info("export.start");
        ExcelResult result = managementService.exportTeacher(condition);
        return RestResult.successData(result);
    }

    /**
     * @Description: 根据key循环去redis取数据
     */
    @GetMapping("result/{key}")
    public RestResult<?> getResultByKey(@PathVariable("key") @NotBlank String key) {
        ExcelResult excelResult = ExportExcelUtils.getResultByKey(key);
        return RestResult.successData(excelResult);
    }



    @ApiOperation(value = "导出excel下载文件")
    @GetMapping("/download")
    @ApiResponses({@ApiResponse(code = 200, response = File.class, message = "导出excel下载文件")})
    public ResponseEntity<Resource> download(@RequestParam("path") String path)
            throws Exception {
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


}
