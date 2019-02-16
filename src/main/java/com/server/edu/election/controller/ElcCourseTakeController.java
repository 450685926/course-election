package com.server.edu.election.controller;

import java.util.List;

import javax.validation.Valid;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.validator.AddGroup;
import com.server.edu.common.validator.ValidatorUtil;
import com.server.edu.election.dto.CourseOpenDto;
import com.server.edu.election.dto.ElcCourseTakeAddDto;
import com.server.edu.election.entity.ElcCourseTake;
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
    
    @ApiOperation(value = "分页查询已可选课程信息")
    @PostMapping("/coursePage")
    public RestResult<PageResult<CourseOpenDto>> coursePage(
        @RequestBody PageCondition<ElecRoundCourseQuery> query)
    {
        ValidatorUtil.validateAndThrow(query.getCondition());
        PageResult<CourseOpenDto> page =
            roundCourseService.listTeachingClassPage(query);
        
        return RestResult.successData(page);
    }
    
    @ApiOperation(value = "学生加课")
    @PutMapping()
    public RestResult<?> add(@RequestBody ElcCourseTakeAddDto value)
    {
        ValidatorUtil.validateAndThrow(value, AddGroup.class);
        
        courseTakeService.add(value);
        return RestResult.success();
    }
    
    @ApiOperation(value = "学生退课")
    @DeleteMapping()
    public RestResult<?> withdraw(@RequestBody @NotEmpty List<ElcCourseTake> value)
    {
        ValidatorUtil.validateAndThrow(value, AddGroup.class);
        
        courseTakeService.withdraw(value);
        
        return RestResult.success();
    }
    
}
