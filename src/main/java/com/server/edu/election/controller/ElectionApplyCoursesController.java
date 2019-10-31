package com.server.edu.election.controller;

import java.util.List;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.dto.CourseDto;
import com.server.edu.election.dto.ElectionApplyCoursesDto;
import com.server.edu.election.entity.Course;
import com.server.edu.election.service.ElectionApplyCoursesService;
import com.server.edu.election.vo.ElectionApplyCoursesVo;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
@SwaggerDefinition(info = @Info(title = "选课申请课程管理", version = ""))
@RestSchema(schemaId = "ElectionApplyCoursesController")
@RequestMapping("electionApplyCourses")
public class ElectionApplyCoursesController {
	private static Logger LOG =
	        LoggerFactory.getLogger(ElectionApplyCoursesController.class);
	@Autowired
	private ElectionApplyCoursesService electionApplyCoursesService;
	 /**
     * 选课申请课程管理列表
     * 
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "选课申请课程管理列表")
    @PostMapping("/applyCourseList")
    public RestResult<PageInfo<ElectionApplyCoursesVo>> applyCourseList(
        @RequestBody PageCondition<ElectionApplyCoursesDto> condition)
        throws Exception
    {
        LOG.info("applyCourseList.start");
        PageInfo<ElectionApplyCoursesVo> list =electionApplyCoursesService.applyCourseList(condition);
        return RestResult.successData(list);
    }
    
	 /**
     * 
     * 
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "课程列表")
    @PostMapping("/courseList1")
    public RestResult<PageInfo<Course>> courseList(
        @RequestBody PageCondition<CourseDto> condition)
        throws Exception
    {
        LOG.info("courseList.start");
        PageInfo<Course> list =electionApplyCoursesService.courseList(condition);
        return RestResult.successData(list);
    }
    
    @ApiOperation(value = "课程列表")
    @PostMapping("/courseList")
    public RestResult<PageResult<Course>> courseList1(
    		@RequestBody PageCondition<CourseDto> condition)
    				throws Exception
    {
    	LOG.info("courseList.start");
    	PageResult<Course> list =electionApplyCoursesService.courseList1(condition);
    	return RestResult.successData(list);
    }
    
    /**
     * 添加课程
     * 
     * @param dto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "添加课程")
    @PostMapping("/addCourses")
    public RestResult<Integer> addCourses(
    		@RequestBody ElectionApplyCoursesDto dto)
        throws Exception
    {
        LOG.info("approval.start");
        int result =electionApplyCoursesService.addCourses(dto);
        return RestResult.successData(result);
    }
	
	
    /**
     * 删除课程
     * 
     * @param ids
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "删除课程")
    @PostMapping("/deleteCourses")
    public RestResult<Integer> deleteCourses(
    		@RequestBody @NotEmpty List<Long> ids)
        throws Exception
    {
        LOG.info("deleteCourses.start");
        int result =electionApplyCoursesService.deleteCourses(ids);
        return RestResult.successData(result);
    }
    
	
}
