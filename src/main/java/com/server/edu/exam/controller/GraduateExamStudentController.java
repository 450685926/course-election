package com.server.edu.exam.controller;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.exam.dto.*;
import com.server.edu.exam.query.GraduateExamStudentQuery;
import com.server.edu.exam.service.GraduateExamStudentService;
import com.server.edu.exam.vo.GraduateExamApplyExaminationVo;
import com.server.edu.util.excel.export.ExcelResult;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description: 应考学生管理
 * @author: bear
 * @create: 2019-09-10 09:26
 */

@SwaggerDefinition(info = @Info(title = "研究生排考应考学生管理", version = ""))
@RestSchema(schemaId = "GraduateExamStudentController")
@RequestMapping("/graduateExamStudentManage")
public class GraduateExamStudentController {

    private static Logger LOG =
            LoggerFactory.getLogger(GraduateExamStudentController.class);

    @Autowired
    private GraduateExamStudentService examStudentService;

    @PostMapping("/listRoom")
    public RestResult<PageResult<SelectDto>> listRoom(@RequestBody PageCondition<SelectDto> condition){
       PageResult<SelectDto> pageResult = examStudentService.listRoom(condition);
       return RestResult.successData(pageResult);
    }

    @PostMapping("/listStudent")
    public RestResult<PageResult<SelectDto>> listStudent(@RequestBody PageCondition<SelectDto> condition){
        PageResult<SelectDto> pageResult = examStudentService.listStudent(condition);
        return RestResult.successData(pageResult);
    }

    @PostMapping("/listCourse")
    public RestResult<PageResult<SelectDto>> listCourse(@RequestBody PageCondition<SelectDto> condition){
        PageResult<SelectDto> pageResult = examStudentService.listCourse(condition);
        return RestResult.successData(pageResult);
    }

    @PostMapping("/listExamStudent")
    public RestResult<PageResult<GraduateExamStudentDto>> listExamStudent(@RequestBody PageCondition<GraduateExamStudentQuery> condition){
        PageResult<GraduateExamStudentDto> pageResult = examStudentService.listExamStudent(condition);
        return RestResult.successData(pageResult);
    }


    @PostMapping("/export")
    public RestResult<?> export(@RequestBody GraduateExamStudentQuery condition){
        ExcelResult result = examStudentService.export(condition);
        return RestResult.successData(result);
    }

    @PostMapping("/deleteExamStudent")
    public RestResult<?> deleteExamStudent(@RequestBody List<GraduateExamStudentDto> condition){
         examStudentService.deleteExamStudent(condition);
        return RestResult.success();
    }

    @PostMapping("/changeExamStudentRoom")
    public RestResult<?> changeExamStudentRoom(@RequestBody List<GraduateExamStudentDto> condition,@RequestParam Long examRoomId){
        examStudentService.changeExamStudentRoom(condition,examRoomId);
        return RestResult.success();
    }

    @PostMapping("/addExamStudent")
    public RestResult<?> addExamStudent(@RequestBody ExamStudentAddDto condition){
        Restrict restrict = examStudentService.addExamStudent(condition);
        return RestResult.successData(restrict);
    }

    @PostMapping("/getRoomsByCourseCode")
    public RestResult<List<ExamRoomDto>> getRoomsByCourseCode(@RequestBody ExamStudentAddDto condition){
        List<ExamRoomDto> list = examStudentService.getRoomsByInfoId(condition);
        return RestResult.successData(list);
    }


    @GetMapping("/getExamRoomByExamInfoId")
    public RestResult<List<ExamRoomDto>> getExamRoomByExamInfoId(@RequestParam String examInfoIds){
        List<ExamRoomDto> list = examStudentService.getExamRoomByExamInfoId(examInfoIds);
        return RestResult.successData(list);
    }

    @PostMapping("/setExamStudentSituatiion")
    public RestResult<?> setExamStudentSituatiion(@RequestBody List<GraduateExamStudentDto> condition,@RequestParam Integer examSituation){
        examStudentService.setExamStudentSituatiion(condition,examSituation);
        return RestResult.success();
    }

}
