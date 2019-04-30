package com.server.edu.election.controller;

import java.io.File;
import java.net.URLDecoder;
import java.util.List;

import com.server.edu.election.dto.*;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import org.apache.commons.lang3.StringUtils;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.entity.ElcNoSelectReason;
import com.server.edu.election.service.ElcLogService;
import com.server.edu.election.service.ReportManagementService;
import com.server.edu.election.vo.ElcLogVo;
import com.server.edu.election.vo.RollBookList;
import com.server.edu.election.vo.StudentSchoolTimetabVo;
import com.server.edu.election.vo.StudentVo;

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

    @Autowired
    private ElcLogService elcLogService;

    private static Logger LOG =
            LoggerFactory.getLogger(ExemptionController.class);

    @Value("${cache.directory}")
    private String cacheDirectory;


    @ApiOperation(value = "查询学生未选课名单")
    @PostMapping("/findElectCourseList")
    public RestResult<PageResult<StudentSelectCourseList>> findElectCourseList(@RequestBody PageCondition<ReportManagementCondition> condition) {
        PageResult<StudentSelectCourseList> electCourseList = managementService.findElectCourseList(condition);
        return RestResult.successData(electCourseList);
    }

    @ApiOperation(value = "新增未选课原因")
    @PostMapping("/addNoSelectReason")
    public RestResult<String> addNoSelectReason(@RequestBody ElcNoSelectReason noSelectReason) {
        if(noSelectReason.getCalendarId()==null||StringUtils.isEmpty(noSelectReason.getStudentId())){
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
    public RestResult<StudnetTimeTable> findStudentTimetab(@RequestParam Long calendarId,@RequestParam String studentCode){
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

        StudnetTimeTable schoolTimetab = managementService.findStudentTimetab(calendarId, studentCode);
        return RestResult.successData(schoolTimetab);
    }


    @ApiOperation(value = "查询所有学生课表")
    @PostMapping("/findAllSchoolTimetab")
    public RestResult<PageResult<StudentVo>> findAllSchoolTimetab(@RequestBody PageCondition<ReportManagementCondition> condition){
        PageResult<StudentVo> allSchoolTimetab = managementService.findAllSchoolTimetab(condition);
        return RestResult.successData(allSchoolTimetab);
    }


    @ApiOperation(value = "查询学生课表对应老师时间地点")
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

    //学生课表调用预览点名册

    @ApiOperation(value = "查询老师课表")
    @GetMapping("/findTeacherTimetable")
    public RestResult<StudentSchoolTimetabVo> findTeacherTimetable(@RequestParam Long calendarId,@RequestParam String teacherCode){
        if(calendarId==null|| StringUtils.isBlank(teacherCode)){
            return RestResult.fail("common.parameterError");
        }
        StudentSchoolTimetabVo teacherTimetable = managementService.findTeacherTimetable(calendarId, teacherCode);
        return RestResult.successData(teacherTimetable);
    }

    @ApiOperation(value = "查询选退课日志")
    @PostMapping("/findCourseLog")
    public RestResult<PageResult<ElcLogVo>> findCourseLog(@RequestBody PageCondition<ElcLogVo> condition){
        PageResult<ElcLogVo> courseLog = managementService.findCourseLog(condition);
        return RestResult.successData(courseLog);
    }


    @ApiOperation(value = "导出未选课学生名单")
    @PostMapping("/exportStudentNoCourseList")
    public RestResult<String> exportStudentNoCourseList (
            @RequestBody ReportManagementCondition condition)
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
}
