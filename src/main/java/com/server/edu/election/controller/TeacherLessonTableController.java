package com.server.edu.election.controller;

import java.io.File;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.rest.ResultStatus;
import com.server.edu.election.dto.ClassCodeToTeacher;
import com.server.edu.election.dto.ClassTeacherDto;
import com.server.edu.election.dto.TeacherTimeTable;
import com.server.edu.election.service.TeacherLessonTableService;
import com.server.edu.election.vo.StudentSchoolTimetabVo;
import com.server.edu.election.vo.TimeTable;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.excel.export.ExcelResult;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

/**
 * 教师课表
 */
@SwaggerDefinition(info = @Info(title = "教师课表", version = ""))
@RestSchema(schemaId = "TeacherLessonTableController")
@RequestMapping("/reportManagement")
public class TeacherLessonTableController
{
    private static Logger LOG =
        LoggerFactory.getLogger(TeacherLessonTableController.class);
    
    @Autowired
    private TeacherLessonTableService lessonTableService;
    
    @ApiOperation(value = "查询所有教师课表")
    @PostMapping("/findAllTeacherTimeTable")
    public RestResult<PageResult<ClassCodeToTeacher>> findAllTeacherTimeTable(
        @RequestBody PageCondition<ClassCodeToTeacher> condition)
    {
        PageResult<ClassCodeToTeacher> allClassTeacher =
            lessonTableService.findAllTeacherTimeTable(condition);
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
        if (StringUtils.equals(session.getCurrentRole(), "1")
            && session.isAdmin())
        {
            classTeacher =
                lessonTableService.findTeacherTimeTableByRole(condition);
        }
        else if (StringUtils.equals(session.getCurrentRole(), "1")
            && !session.isAdmin() && session.isAcdemicDean())
        {
            classCodeToTeacher.setFaculty(session.getFaculty());
            classTeacher =
                lessonTableService.findTeacherTimeTableByRole(condition);
        }
        else if (StringUtils.equals(session.getCurrentRole(), "2"))
        {
            classCodeToTeacher.setTeacherCode(session.realUid());
            classTeacher =
                lessonTableService.findTeacherTimeTableByRole(condition);
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
            lessonTableService.findTeacherTimetable2(calendarId, teacherCode);
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
            lessonTableService.findTeacherTimetable(calendarId, teacherCode);
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
        List<TimeTable> teacherTimetable = lessonTableService
            .getTeacherTimetable(calendarId, teacherCode, week);
        return RestResult.successData(teacherTimetable);
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
            lessonTableService.findStudentAndTeacherTime(teachingClassId);
        return RestResult.successData(studentAndTeacherTime);
    }
    
    @ApiOperation(value = "查询所有教学班对应老师信息")
    @PostMapping("/findAllClassTeacher")
    public RestResult<PageResult<ClassCodeToTeacher>> findAllClassTeacher(
        @RequestBody PageCondition<ClassCodeToTeacher> condition)
    {
        PageResult<ClassCodeToTeacher> allClassTeacher =
            lessonTableService.findAllClassTeacher(condition);
        return RestResult.successData(allClassTeacher);
    }
    
    @ApiOperation(value = "导出所有教师课表")
    @PostMapping("/exportTeacher")
    public RestResult<ExcelResult> exportTeacher(
        @RequestBody ClassCodeToTeacher condition)
        throws Exception
    {
        LOG.info("export.start");
        ExcelResult result = lessonTableService.exportTeacher(condition);
        return RestResult.successData(result);
    }
    
    @GetMapping(value = "/exportTeacherTimetabPdf")
    @ApiResponses({
        @ApiResponse(code = 200, response = File.class, message = "导出教师课表pdf--研究生")})
    public ResponseEntity<Resource> exportTeacherTimetabPdf(
        @RequestParam("calendarId") Long calendarId,
        @RequestParam("calendarName") String calendarName,
        @RequestParam("teacherCode") String teacherCode,
        @RequestParam("teacherName") String teacherName)
        throws Exception
    {
        LOG.info("exportTeacherTimetabPdf.start");
        
        StringBuffer name = new StringBuffer();
        RestResult<String> restResult =
            lessonTableService.exportTeacherTimetabPdf(calendarId,
                calendarName,
                teacherCode,
                teacherName);
        
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
