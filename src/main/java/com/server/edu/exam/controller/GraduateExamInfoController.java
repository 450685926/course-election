package com.server.edu.exam.controller;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.exam.dto.*;
import com.server.edu.exam.entity.GraduateExamInfo;
import com.server.edu.exam.entity.GraduateExamRoom;
import com.server.edu.exam.entity.GraduateExamTeacher;
import com.server.edu.exam.query.GraduateExamAutoStudent;
import com.server.edu.exam.query.GraduateExamRoomsQuery;
import com.server.edu.exam.query.StudentQuery;
import com.server.edu.exam.service.GraduateExamInfoService;
import com.server.edu.exam.vo.ExamStudent;
import com.server.edu.exam.vo.GraduateExamInfoVo;
import com.server.edu.exam.vo.GraduateExamRoomVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.util.CollectionUtil;
import com.server.edu.util.async.AsyncExecuter;
import com.server.edu.util.async.AsyncProcessUtil;
import com.server.edu.util.async.AsyncResult;
import com.server.edu.util.excel.export.ExcelResult;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description: 研究生排考信息
 * @author: bear
 * @create: 2019-09-02 14:44
 */

@SwaggerDefinition(info = @Info(title = "研究生排考信息设置", version = ""))
@RestSchema(schemaId = "GraduateExamInfoController")
@RequestMapping("/graduateInfo")
public class GraduateExamInfoController {
    private static Logger LOG =
            LoggerFactory.getLogger(GraduateExamInfoController.class);

    @Autowired
    private GraduateExamInfoService examInfoService;

    @PostMapping("/listGraduateExamInfo")
    public RestResult<PageResult<GraduateExamInfoVo>> listGraduateExamInfo(@RequestBody PageCondition<GraduateExamInfoVo> condition) {
        PageResult<GraduateExamInfoVo> pageResult = examInfoService.listGraduateExamInfo(condition);
        return RestResult.successData(pageResult);
    }

    /**
     * type 0 撤回 1 发布 支持批量
     */
    @PostMapping("/releaseOrRebackGraduateExamInfo")
    public RestResult<?> releaseGraduateExamInfo(@RequestBody List<Long> ids, @RequestParam String type) {
        examInfoService.releaseOrRebackGraduateExamInfo(ids, type);
        return RestResult.success();
    }

    @PostMapping("/saveExamTime")
    public RestResult<ExamSaveTimeRebackDto> insertTime(@RequestBody List<GraduateExamInfo> examInfo) {
        return examInfoService.insertTime(examInfo);
    }

    @PostMapping("/saveExamRoom")
    public RestResult<?> saveRoom(@RequestBody List<GraduateExamRoom> room, @RequestParam String examInfoIds) {
        examInfoService.insertRoom(room, examInfoIds);
        return RestResult.success();
    }

    @PostMapping("/listExamRoomByExamInfoId")
    public RestResult<PageResult<GraduateExamRoomVo>> listExamRoomByExamInfoId(@RequestBody PageCondition<GraduateExamRoomsQuery> condition) {
        PageResult<GraduateExamRoomVo> pageResult = examInfoService.listExamRoomByExamInfoId(condition);
        return RestResult.successData(pageResult);
    }

    //查询可用教室以及校验todo

    //查询教师以及校验todo
    @PostMapping("/saveTeacher")
    public RestResult<?> saveTeacher(@RequestBody List<GraduateExamTeacher> teachers, @RequestParam Long examRoomId, @RequestParam String examInfoIds) {
        examInfoService.insertTeacher(teachers, examRoomId, examInfoIds);
        return RestResult.success();
    }


    @PostMapping("/listTeachingClass")
    public RestResult<PageResult<TeachingClassDto>> listTeachingClass(@RequestBody PageCondition<GraduateExamRoomsQuery> teachingClassQuery) {
        PageResult<TeachingClassDto> pageResult = examInfoService.listTeachingClass(teachingClassQuery);
        return RestResult.successData(pageResult);
    }

    @PostMapping("/saveTeachingClass")
    public RestResult<Restrict> saveTeachingClass(@RequestBody GraduateExamRoomsQuery condition) {
        Restrict restrict = examInfoService.insertTeachingClass(condition);
        return RestResult.successData(restrict);
    }

    @PostMapping("/listStudent")
    public RestResult<PageResult<NoExamStudent>> listStudent(@RequestBody PageCondition<StudentQuery> studentQuery) {
        PageResult<NoExamStudent> pageResult = examInfoService.listStudent(studentQuery);
        return RestResult.successData(pageResult);
    }

    @PostMapping("/saveStudent")
    public RestResult<Restrict> saveStudent(@RequestBody List<NoExamStudent> examStudents, @RequestParam Long examRoomId) {
        Restrict restrict = examInfoService.insertStudent(examStudents, examRoomId);
        return RestResult.successData(restrict);
    }

    @PostMapping("/cleanStudentByRoomId")
    public RestResult<?> cleanStudentByRoomId(@RequestBody List<Long> examRoomIds) {
        examInfoService.cleanStudentByRoomId(examRoomIds);
        return RestResult.success();
    }

    @PostMapping("/deleteRoom")
    public RestResult<?> deleteRoom(@RequestBody List<Long> examRoomIds, @RequestParam String examInfoIds) {
        examInfoService.deleteRoom(examRoomIds, examInfoIds);
        return RestResult.success();
    }

    @GetMapping("/getExamStudentByRoomId")
    public RestResult<List<ExamStudent>> getExamStudentByRoomId(@RequestParam Long examRoomId,@RequestParam Long examInfoId) {
        List<ExamStudent> list = examInfoService.listExamStudentById(examRoomId,examInfoId);
        return RestResult.successData(list);
    }

    @PostMapping("/export")
    public RestResult<?> export(@RequestBody GraduateExamInfoVo condition) {
        ExcelResult result = examInfoService.export(condition);
        return RestResult.successData(result);
    }

    @PostMapping("/autoAllocation")
    public RestResult<?> autoAllocationExamRoom(@RequestBody GraduateExamAutoStudent roomsQuery) {
        if (CollectionUtil.isEmpty(roomsQuery.getExamInfoIds()) || roomsQuery.getExamType() == null
                || roomsQuery.getCalendarId() == null) {
            throw new ParameterValidateException("入参有误");
        }
        AsyncResult result = AsyncProcessUtil.submitTask( "autoGraduateExamStudent", new AsyncExecuter() {
            @Override
            public void execute() {
                this.getResult().setSuccess(false);
                String msg = examInfoService.autoAllocationExamRoom(roomsQuery);
                this.getResult().setMsg(msg);
                this.getResult().setSuccess(true);
            }
        });
        return RestResult.successData(result);
    }

    /**
     * @Description: 根据key循环去redis取数据
     * @author kan yuanfeng
     * @date 2019/1/7 19:24
     */
    @GetMapping("result/{key}")
    public RestResult<?> getByKey(@PathVariable("key") @NotBlank String key) {
        AsyncResult result = AsyncProcessUtil.getResult(key);
        return RestResult.successData(result);
    }

    /**
     * 实时查询排考人数
     *
     * @param
     * @return
     * @author bear
     * @date 2019/10/9 16:47
     */

    @PostMapping("/getExamStudentNumber")
    public RestResult<GraduateExamStudentNumber> getExamStudentNumber(@RequestBody GraduateExamStudentNumber studentNumber) {
        GraduateExamStudentNumber number = examInfoService.getExamStudentNumber(studentNumber);
        return RestResult.successData(number);
    }


    @GetMapping("/editGraduateExam")
    public RestResult<EditGraduateExam> editGraduateExam(@RequestParam Long id){
        EditGraduateExam  item = examInfoService.editGraduateExam(id);
        return RestResult.successData(item);
    }

    @PostMapping("/saveExamTimeAndDeleteExamRoom")
    public RestResult<ExamSaveTimeRebackDto> saveExamTimeAndDeleteExamRoom(@RequestBody List<GraduateExamInfo> examInfo) {
        return examInfoService.saveExamTimeAndDeleteExamRoom(examInfo);
    }

    @PostMapping("/saveExamTimeAndBatch")
    public RestResult<ExamSaveTimeRebackDto> saveExamTimeAndBatch(@RequestBody List<GraduateExamInfo> examInfo) {
        return examInfoService.saveExamTimeAndBatch(examInfo);
    }
}
