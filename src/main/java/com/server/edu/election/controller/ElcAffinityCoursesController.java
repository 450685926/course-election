package com.server.edu.election.controller;

import java.util.List;

import javax.validation.Valid;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.dto.ElcAffinityCoursesDto;
import com.server.edu.election.dto.StudentDto;
import com.server.edu.election.entity.CourseOpen;
import com.server.edu.election.entity.Student;
import com.server.edu.election.service.ElcAffinityCoursesService;
import com.server.edu.election.vo.ElcAffinityCoursesVo;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "优选学生管理", version = ""))
@RestSchema(schemaId = "ElcAffinityCoursesController")
@RequestMapping("elcAffinityCourses")
public class ElcAffinityCoursesController
{
    private static Logger LOG =
        LoggerFactory.getLogger(ElcAffinityCoursesController.class);
    
    @Autowired
    private ElcAffinityCoursesService elcAffinityCoursesService;
    
    /**
    * 优先学生名单列表
    * 
    * @param condition
    * @return
    * @see [类、类#方法、类#成员]
    */
    @ApiOperation(value = "优先学生名单列表")
    @PostMapping("/list")
    public RestResult<PageInfo<ElcAffinityCoursesVo>> list(
        @RequestBody PageCondition<ElcAffinityCoursesDto> condition)
        throws Exception
    {
        LOG.info("list.start");
        PageInfo<ElcAffinityCoursesVo> list =
            elcAffinityCoursesService.list(condition);
        return RestResult.successData(list);
    }
    
    /**
     * 删除
     * 
     * @param ids
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "删除")
    @PostMapping("/delete")
    public RestResult<Integer> delete(@RequestBody @NotEmpty List<String> courseCodes)
        throws Exception
    {
        LOG.info("delete.start");
        int result = elcAffinityCoursesService.delete(courseCodes);
        return RestResult.successData(result);
    }
    
    /**
    * 优先课程列表
    * 
    * @param condition
    * @return
    * @see [类、类#方法、类#成员]
    */
    @ApiOperation(value = "优先课程列表")
    @PostMapping("/courseList")
    public RestResult<PageInfo<CourseOpen>> courseList(
        @RequestBody PageCondition<CourseOpen> condition)
        throws Exception
    {
        LOG.info("courseList.start");
        PageInfo<CourseOpen> list =
            elcAffinityCoursesService.courseList(condition);
        return RestResult.successData(list);
    }
    
    /**
     * 添加课程
     * 
     * @param ids
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "添加课程")
    @PostMapping("/addCourse")
    public RestResult<Integer> addCourse(
        @RequestBody @NotEmpty List<String> ids)
        throws Exception
    {
        LOG.info("addCourse.start");
        int result = elcAffinityCoursesService.addCourse(ids);
        return RestResult.successData(result);
    }
    
    /**
    * 优先学生列表
    * 
    * @param condition
    * @return
    * @see [类、类#方法、类#成员]
    */
    @ApiOperation(value = "优先学生列表")
    @PostMapping("/studentList")
    public RestResult<PageInfo<Student>> studentList(
        @RequestBody PageCondition<StudentDto> condition)
        throws Exception
    {
        LOG.info("courseList.start");
        PageInfo<Student> list =
            elcAffinityCoursesService.studentList(condition);
        return RestResult.successData(list);
    }
    
    /**
    * 学生列表
    * 
    * @param condition
    * @return
    * @see [类、类#方法、类#成员]
    */
    @ApiOperation(value = "学生列表")
    @PostMapping("/getStudents")
    public RestResult<PageInfo<Student>> getStudents(
        @RequestBody PageCondition<StudentDto> condition)
        throws Exception
    {
        LOG.info("getStudents.start");
        PageInfo<Student> list =
            elcAffinityCoursesService.getStudents(condition);
        return RestResult.successData(list);
    }
    
    /**
    * 添加学生
    * 
    * @param ids
    * @return
    * @see [类、类#方法、类#成员]
    */
    @ApiOperation(value = "添加学生")
    @PostMapping("/addStudent")
    public RestResult<Integer> addStudent(
        @RequestBody @Valid ElcAffinityCoursesVo elcAffinityCoursesVo)
        throws Exception
    {
        LOG.info("addStudent.start");
        int result = elcAffinityCoursesService.addStudent(elcAffinityCoursesVo);
        return RestResult.successData(result);
    }
    
    /**
    * 批量添加学生
    * 
    * @param 
    * @return
    * @see [类、类#方法、类#成员]
    */
    @ApiOperation(value = "批量添加学生")
    @PostMapping("/batchAddStudent")
    public RestResult<Integer> batchAddStudent(
    		@RequestParam("courseCode")  @NotBlank String courseCode)
        throws Exception
    {
        LOG.info("batchAddStudent.start");
        int result = elcAffinityCoursesService.batchAddStudent(courseCode);
        return RestResult.successData(result);
    }
    
    /**
    * 移除学生
    * 
    * @param ids
    * @return
    * @see [类、类#方法、类#成员]
    */
    @ApiOperation(value = "移除学生")
    @PostMapping("/deleteStudent")
    public RestResult<Integer> deleteStudent(
        @RequestBody @Valid ElcAffinityCoursesVo elcAffinityCoursesVo)
        throws Exception
    {
        LOG.info("deleteStudent.start");
        int result =
            elcAffinityCoursesService.deleteStudent(elcAffinityCoursesVo);
        return RestResult.successData(result);
    }
    
    /**
    * 移除所有学生
    * 
    * @param 
    * @return
    * @see [类、类#方法、类#成员]
    */
    @ApiOperation(value = "移除所有学生")
    @PostMapping("/batchDeleteStudent")
    public RestResult<Integer> batchDeleteStudent(
    		@RequestParam("courseCode")  @NotBlank String courseCode)
        throws Exception
    {
        LOG.info("batchDeleteStudent.start");
        int result = elcAffinityCoursesService.batchDeleteStudent(courseCode);
        return RestResult.successData(result);
    }
    
}
