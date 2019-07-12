package com.server.edu.election.controller;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.dto.AddAndRemoveCourseDto;
import com.server.edu.election.dto.ElcStudentDto;
import com.server.edu.election.service.ElcCourseUpholdService;
import com.server.edu.election.vo.ElcStudentVo;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@SwaggerDefinition(info = @Info(title = "课程维护", version = ""))
@RestSchema(schemaId = "ElcCourseUpholdController")
@RequestMapping("elcCrossUphold")
public class ElcCourseUpholdController {
    @Autowired
    private ElcCourseUpholdService elcCourseUpholdService;

    private static Logger LOG =
            LoggerFactory.getLogger(ExemptionController.class);

    @ApiOperation(value = "查询学生选课信息")
    @PostMapping("/elcStudentInfo")
    public RestResult<PageResult<ElcStudentVo>> elcStudentInfo(
            @RequestBody PageCondition<ElcStudentDto> condition) {
        PageResult<ElcStudentVo> page =
                elcCourseUpholdService.elcStudentInfo(condition);
        return RestResult.successData(page);
    }

    /**
     * 返回的可加课程列表修读类型为正常修读
     * @param condition
     * @return
     */
    @ApiOperation(value = "查询学生加课列表")
    @PostMapping("/addCourseList")
    public RestResult<PageResult<ElcStudentVo>> addCourseList(
            @RequestBody PageCondition<String> condition) {
        PageResult<ElcStudentVo> page = elcCourseUpholdService.addCourseList(condition);
        return RestResult.successData(page);
    }

    /**
     * 默认修读类型为正常修读
     * @param courseDto
     * @return
     */
    @ApiOperation(value = "加课")
    @PostMapping("/addCourse")
    public RestResult<Integer> addCourse(
            @RequestBody AddAndRemoveCourseDto courseDto) {
        Integer count = null;
        Session session = SessionUtils.getCurrentSession();
        setParam(session, courseDto);
        if (session.isAdmin()) {
            courseDto.setChooseObj(3);
            count = elcCourseUpholdService.addCourse(courseDto);
        } else if (session.isAcdemicDean()) {
            courseDto.setChooseObj(2);
            count = elcCourseUpholdService.addCourse(courseDto);
        }
        return RestResult.successData(count);
    }

    /**
     * 返回的退课课程列表修读类型为正常修读
     * @param condition
     * @return
     */
    @ApiOperation(value = "查询学生退课列表")
    @PostMapping("/removedCourseList")
    public RestResult<PageResult<ElcStudentVo>> removedCourseList(
            @RequestBody PageCondition<String> condition) {
        PageResult<ElcStudentVo> page = elcCourseUpholdService.removedCourseList(condition);
        return RestResult.successData(page);
    }

    /**
     * 默认修读类型为正常修读
     * @param courseDto
     * @return
     */
    @ApiOperation(value = "退课")
    @PostMapping("/removedCourse")
    public RestResult<Integer> removedCourse(
            @RequestBody AddAndRemoveCourseDto courseDto) {
        Integer count = null;
        Session session = SessionUtils.getCurrentSession();
        setParam(session, courseDto);
        if (session.isAdmin()) {
            courseDto.setChooseObj(3);
            count = elcCourseUpholdService.removedCourse(courseDto);
        } else if (session.isAcdemicDean()) {
            courseDto.setChooseObj(2);
            count = elcCourseUpholdService.removedCourse(courseDto);
        }
        return RestResult.successData(count);
    }

    @ApiOperation(value = "导出学生选课信息")
    @PostMapping("/exportElcStudentInfo")
    public RestResult<String> exportElcStudentInfo(
            @RequestBody PageCondition<ElcStudentDto> condition)
            throws Exception
    {
        LOG.info("export.elcStudentInfo.start");
        String export = elcCourseUpholdService.exportElcStudentInfo(condition);
        return RestResult.successData(export);
    }

    private void setParam(Session session, AddAndRemoveCourseDto courseDto) {
        String uid = session.getUid();
        String name = session.getName();
        courseDto.setId(uid);
        courseDto.setName(name);
    }

}


