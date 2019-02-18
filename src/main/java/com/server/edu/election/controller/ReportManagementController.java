package com.server.edu.election.controller;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.dto.*;
import com.server.edu.election.entity.RollBookList;
import com.server.edu.election.service.ReportManagementService;
import com.server.edu.election.vo.StudentSchoolTimetabVo;
import com.server.edu.election.vo.StudentVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import org.apache.commons.lang3.StringUtils;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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


    /*@ApiOperation(value = "查询学生选课名单")
    @PostMapping("/findElectCourseList")
    public RestResult<> findElectCourseList(@RequestBody PageCondition<> condition) {

        return RestResult.successData();
    }*/

    @ApiOperation(value = "查询点名册")
    @PostMapping("/findRollBookList")
    public RestResult<PageResult<RollBookList>> findRollBookList(@RequestBody PageCondition<ReportManagementCondition> condition){
        PageResult<RollBookList> bookList = managementService.findRollBookList(condition);
        return RestResult.successData(bookList);
    }


    @ApiOperation(value = "预览点名册")
    @PostMapping("/previewRollBookList")
    public RestResult<PreviewRollBookList> findPreviewRollBookList(@RequestBody RollBookList bookList){
        if(bookList.getCalssCode()==null||bookList.getTeachingClassId()==null){
            return RestResult.fail("common.parameterError");
        }
        PreviewRollBookList previewRollBookList = managementService.findPreviewRollBookList(bookList);
        return RestResult.successData(previewRollBookList);
    }

    //导出待做

    @ApiOperation(value = "查询学生个人课表")
    @GetMapping("/findSchoolTimetab")
    public RestResult<StudentSchoolTimetabVo> findSchoolTimetab(@RequestParam Long calendarId,@RequestParam String studentCode){
        if(calendarId==null||studentCode==null){
            return RestResult.fail("common.parameterError");
        }
        StudentSchoolTimetabVo schoolTimetab = managementService.findSchoolTimetab(calendarId, studentCode);
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
    public RestResult<?> findTeacherTimetable(@RequestParam Long calendarId,@RequestParam String teacherCode){
        if(calendarId==null|| StringUtils.isBlank(teacherCode)){
            return RestResult.fail("common.parameterError");
        }
        List<ClassTeacherDto> teacherTimetable = managementService.findTeacherTimetable(calendarId, teacherCode);
        return RestResult.successData(teacherTimetable);
    }




}
