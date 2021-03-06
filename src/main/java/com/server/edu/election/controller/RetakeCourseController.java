package com.server.edu.election.controller;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.dto.RebuildCourseDto;
import com.server.edu.election.dto.RetakeCourseCountDto;
import com.server.edu.election.service.RetakeCourseService;
import com.server.edu.election.vo.ElcRetakeSetVo;
import com.server.edu.election.vo.FailedCourseVo;
import com.server.edu.election.vo.RebuildCourseVo;
import com.server.edu.election.vo.RebuildStuVo;
import com.server.edu.election.vo.RetakeCourseCountVo;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.CollectionUtil;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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

    private static Logger LOG = LoggerFactory.getLogger("com.server.edu.election.Timer.LogToDBTimer");

    @ApiOperation(value = "设定重修选课的时间和规则")
    @PutMapping("/setRetakeRules")
    public RestResult setRetakeRules(@RequestBody @Valid ElcRetakeSetVo elcRetakeSetVo) {
        retakeCourseService.setRetakeRules(elcRetakeSetVo);
        return RestResult.success();
    }

    @ApiOperation(value = "查询重修选课开关")
    @GetMapping("/getRetakeSet")
    public RestResult<ElcRetakeSetVo> getRetakeSet(@RequestParam("calendarId") Long calendarId) {
        Session currentSession = SessionUtils.getCurrentSession();
        ElcRetakeSetVo elcRetakeSetVo = retakeCourseService.getRetakeSet(calendarId, currentSession.getCurrentManageDptId());
        return RestResult.successData(elcRetakeSetVo);
    }

    @ApiOperation(value = "查询重修选课开关状态")
    @GetMapping("/getRetakeRule")
    public RestResult<Boolean> getRetakeRule(@RequestParam("calendarId") Long calendarId) {
        Session currentSession = SessionUtils.getCurrentSession();
        return RestResult.successData(retakeCourseService.getRetakeRule(calendarId, currentSession.getCurrentManageDptId()));
    }

    @ApiOperation(value = "查询重修选课门数上限列表")
    @PostMapping("/findRetakeCourseCountList")
    public RestResult<PageResult<RetakeCourseCountDto>> findRetakeCourseCountList(
            @RequestBody PageCondition<RetakeCourseCountVo> condition) {
        PageResult<RetakeCourseCountDto> result = retakeCourseService.findRetakeCourseCountList(condition);
        return RestResult.successData(result);
    }

    @ApiOperation(value = "添加修改选课门数上限")
    @PostMapping("/updateRetakeCourseCount")
    public RestResult updateRetakeCourseCount(@RequestBody @Valid RetakeCourseCountVo retakeCourseCountVo) {
        LOG.info("updateRetakeCourseCount.start");
        retakeCourseService.updateRetakeCourseCount(retakeCourseCountVo);
        return RestResult.success();
    }

    @ApiOperation(value = "删除选课门数上限")
    @PostMapping("/deleteRetakeCourseCount")
    public RestResult deleteRetakeCourseCount(@RequestBody List<Long> retakeCourseCountIds) {
        retakeCourseService.deleteRetakeCourseCount(retakeCourseCountIds);
        return RestResult.success();
    }

    @ApiOperation(value = "学生个人不及格课程列表")
    @GetMapping("/failedCourseList")
    public RestResult<List<FailedCourseVo>> failedCourseList(@RequestParam("calendarId") Long calendarId) {
        List<FailedCourseVo> list = retakeCourseService.failedCourseList(calendarId);
        return RestResult.successData(list);
    }

    /**
     * 研究生重修可选课程列表
     *
     * @param condition
     * @return
     */
    @ApiOperation(value = "重修课程列表")
    @PostMapping("/findRebuildCourseList")
    public RestResult<PageResult<RebuildCourseVo>> findRebuildCourseList(@RequestBody PageCondition<RebuildCourseDto> condition) {
        PageResult<RebuildCourseVo> rebuildCourseList = retakeCourseService.findRebuildCourseList(condition);
        return RestResult.successData(rebuildCourseList);
    }

    /**
     * 研究生重修选课、退课
     *
     * @param rebuildCourseVo
     * @return
     */
    @ApiOperation(value = "研究生重修选课、退课")
    @PostMapping("/updateRebuildCourse")
    public RestResult updateRebuildCourse(@RequestBody RebuildCourseVo rebuildCourseVo) {
        retakeCourseService.updateRebuildCourse(rebuildCourseVo);
        return RestResult.success();
    }

    /**
     * 研究生重修代选课学生信息查询
     * @param calendarId
     * @param studentId
     * @return
     */
    @ApiOperation(value = "研究生重修代选课学生信息查询")
    @GetMapping("/findRebuildStu")
    public RestResult<RebuildStuVo> findRebuildStu(@RequestParam("calendarId") Long calendarId,
                                                   @RequestParam("studentId") String studentId)
    {
        RebuildStuVo rebuildStuVo = retakeCourseService.findRebuildStu(calendarId, studentId);
        return RestResult.successData(rebuildStuVo);
    }

    @ApiOperation(value = "研究生重修代选课学生不及格课程列表")
    @GetMapping("/failedCourses")
    public RestResult<List<FailedCourseVo>> failedCourses(@RequestParam("calendarId") Long calendarId,
                                                          @RequestParam("studentId") String studentId)
    {
        List<FailedCourseVo> list = retakeCourseService.failedCourses(calendarId, studentId);
        return RestResult.successData(list);
    }

    /**
     * 研究生重修代选课可选课程列表
     *
     * @param condition
     * @return
     */
    @ApiOperation(value = "研究生重修代选课可选课程列表")
    @PostMapping("/findRebuildCourses")
    public RestResult<PageResult<RebuildCourseVo>> findRebuildCourses(@RequestBody PageCondition<RebuildCourseDto> condition) {
        PageResult<RebuildCourseVo> rebuildCourseList = retakeCourseService.findRebuildCourses(condition);
        return RestResult.successData(rebuildCourseList);
    }
}
