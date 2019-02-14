package com.server.edu.election.controller;

import javax.validation.Valid;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.validator.AddGroup;
import com.server.edu.common.validator.ValidatorUtil;
import com.server.edu.election.dto.CourseOpenDto;
import com.server.edu.election.dto.ElcCourseTakeDto;
import com.server.edu.election.query.ElcCourseTakeQuery;
import com.server.edu.election.query.ElecRoundCourseQuery;
import com.server.edu.election.service.ElcCourseTakeService;
import com.server.edu.election.service.ElecRoundCourseService;
import com.server.edu.election.vo.ElcCourseTakeVo;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "上课名单", version = ""))
@RestSchema(schemaId = "ElcCourseTakeController")
@RequestMapping("elcCourseTake")
public class ElcCourseTakeController
{
    @Autowired
    private ElcCourseTakeService courseTakeService;
    
    @Autowired
    private ElecRoundCourseService roundCourseService;
    
    /**
     * 上课名单列表
     * 
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "上课名单列表")
    @PostMapping("/page")
    public RestResult<PageResult<ElcCourseTakeVo>> page(
        @RequestBody @Valid PageCondition<ElcCourseTakeQuery> condition)
        throws Exception
    {
        PageResult<ElcCourseTakeVo> list =
            courseTakeService.listPage(condition);
        
        return RestResult.successData(list);
    }
    
    /**
     * 分页查询
     *
     * @param condition
     * @return
     */
    @ApiOperation(value = "分页查询已添加课程信息")
    @PostMapping("/coursePage")
    public RestResult<PageResult<CourseOpenDto>> coursePage(
        @RequestBody PageCondition<ElecRoundCourseQuery> query)
    {
        ValidatorUtil.validateAndThrow(query.getCondition());
        PageResult<CourseOpenDto> page = roundCourseService.listPage(query);
        
        return RestResult.successData(page);
    }
    
    @ApiOperation(value = "给学生加课")
    @PostMapping("/add")
    public RestResult<?> add(
        @RequestBody ElcCourseTakeDto value)
    {
        ValidatorUtil.validateAndThrow(value, AddGroup.class);
        courseTakeService.add(value.getCalendarId(),
            value.getTeachingClassIds(),
            value.getStudentId());
        return RestResult.success();
    }
    
    @ApiOperation(value = "给学生退课")
    @PostMapping("/withdraw")
    public RestResult<?> withdraw(
        @RequestBody ElcCourseTakeDto value)
    {
        ValidatorUtil.validateAndThrow(value, AddGroup.class);
        
        courseTakeService.withdraw(value.getTeachingClassIds(),
            value.getStudentId());
        
        return RestResult.success();
    }
    
}
