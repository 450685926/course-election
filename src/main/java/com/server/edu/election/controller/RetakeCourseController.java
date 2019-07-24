package com.server.edu.election.controller;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.service.RetakeCourseService;
import com.server.edu.election.vo.ElcRetakeSetVo;
import com.server.edu.election.vo.FailedCourseVo;
import com.server.edu.election.vo.RebuildCourseVo;
import com.server.edu.election.vo.RetakeCourseCountVo;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description: 重修
 * @author:
 * @create: 2019-07-18
 */

@SwaggerDefinition(info = @Info(title = "重修", version = ""))
@RestSchema(schemaId = "RetakeCourseController")
@RequestMapping("/retakeCourse")
public class RetakeCourseController {
    @Autowired
    private RetakeCourseService retakeCourseService;

    @ApiOperation(value = "设定重修选课的时间和规则")
    @PutMapping("/setRetakeRules")
    public RestResult setRetakeRules(@RequestBody ElcRetakeSetVo elcRetakeSetVo) {
        retakeCourseService.setRetakeRules(elcRetakeSetVo);
        return RestResult.success();
    }

    @ApiOperation(value = "查询重修选课开关")
    @GetMapping("/getRetakeRule")
    public RestResult<ElcRetakeSetVo> getRetakeRule(@RequestParam("calendarId") Long calendarId, @RequestParam("projectId") String projectId) {
        ElcRetakeSetVo elcRetakeSetVo = retakeCourseService.getRetakeRul(calendarId, projectId);
        return RestResult.successData(elcRetakeSetVo);
    }

    @ApiOperation(value = "查询重修选课门数上限列表")
    @PostMapping("/findRetakeCourseCountList")
    public RestResult<PageResult<RetakeCourseCountVo>> findRetakeCourseCountList(
            @RequestBody PageCondition<RetakeCourseCountVo> condition)
    {
        PageResult<RetakeCourseCountVo> result = retakeCourseService.findRetakeCourseCountList(condition);
        return RestResult.successData(result);
    }

    @ApiOperation(value = "添加修改选课门数上限")
    @PostMapping("/updateRetakeCourseCount")
    public RestResult updateRetakeCourseCount(@RequestBody RetakeCourseCountVo retakeCourseCountVo)
    {
        retakeCourseService.updateRetakeCourseCount(retakeCourseCountVo);
        return RestResult.success();
    }

    @ApiOperation(value = "删除选课门数上限")
    @DeleteMapping("/deleteRetakeCourseCount")
    public RestResult deleteRetakeCourseCount(@RequestParam("retakeCourseCountId") Long retakeCourseCountId)
    {
        retakeCourseService.deleteRetakeCourseCount(retakeCourseCountId);
        return RestResult.success();
    }

    @ApiOperation(value = "学生个人不及格课程列表")
    @GetMapping("/failedCourseList")
    public RestResult<List<FailedCourseVo>> failedCourseList(@RequestParam("calendarId") Long calendarId)
    {
//        Session currentSession = SessionUtils.getCurrentSession();
//        String uid = currentSession.getUid();
//        List<FailedCourseVo> list = retakeCourseService.findRetakeCourseList(uid, calendarId);
        List<FailedCourseVo> list = retakeCourseService.failedCourseList("000104", calendarId);
        return RestResult.successData(list);
    }

    @ApiOperation(value = "可重修课程列表")
    @GetMapping("/findRebuildCourseList")
    public RestResult<List<RebuildCourseVo>> findRebuildCourseList(@RequestParam("calendarId") Long calendarId, @RequestParam("keyWord") String keyWord)
    {
        Session currentSession = SessionUtils.getCurrentSession();
        String uid = currentSession.getUid();
        String currentManageDptId = currentSession.getCurrentManageDptId();
        List<RebuildCourseVo> list = retakeCourseService.findRebuildCourseList("000104", calendarId, keyWord, currentManageDptId);
        return RestResult.successData(list);
    }


}
