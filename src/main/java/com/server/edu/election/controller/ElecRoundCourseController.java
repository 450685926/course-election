package com.server.edu.election.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.validator.ValidatorUtil;
import com.server.edu.election.dto.CourseOpenDto;
import com.server.edu.election.query.ElecRoundCourseQuery;
import com.server.edu.election.service.ElecRoundCourseService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "可选课程管理", version = ""))
@RestSchema(schemaId = "ElecRoundCourseController")
@RequestMapping("elecRoundCourse")
public class ElecRoundCourseController
{
    static Logger logger =
        LoggerFactory.getLogger(ElecRoundCourseController.class);
    
    @Autowired
    private ElecRoundCourseService service;
    
    /**
     * 分页查询
     *
     * @param condition
     * @return
     */
    @ApiOperation(value = "分页查询已添加课程信息")
    @PostMapping("/page")
    public RestResult<PageResult<CourseOpenDto>> page(
        @RequestBody PageCondition<ElecRoundCourseQuery> query)
    {
        ValidatorUtil.validateAndThrow(query.getCondition());
        PageResult<CourseOpenDto> page = service.listPage(query);
        
        return RestResult.successData(page);
    }
    
    @ApiOperation(value = "分页查询未添加课程信息")
    @PostMapping("/pageUnAdd")
    public RestResult<PageResult<CourseOpenDto>> pageUnAdd(
        @RequestBody PageCondition<ElecRoundCourseQuery> query)
    {
        ValidatorUtil.validateAndThrow(query.getCondition());
        PageResult<CourseOpenDto> page = service.listUnAddPage(query);
        
        return RestResult.successData(page);
    }
    
    /**
     * 添加
     *
     * @param round
     * @return
     */
    @ApiOperation(value = "添加课程")
    @PutMapping("/{roundId}")
    public RestResult<?> add(@PathVariable("roundId") @NotNull Long roundId,
        @RequestBody @NotNull List<String> courseCodes)
    {
        service.add(roundId, courseCodes);
        
        return RestResult.success();
    }
    
    /**
     * 条件添加可选课学生
     *
     * @param round
     * @return
     */
    @ApiOperation(value = "添加全部课程")
    @PutMapping("/addAll")
    public RestResult<?> addAll(
        @RequestBody @Valid @NotNull ElecRoundCourseQuery condition)
    {
        service.addAll(condition);
        
        return RestResult.success();
    }
    
    /**
     * 删除可选课学生
     *
     * @param ids
     * @return
     */
    @ApiOperation(value = "删除")
    @DeleteMapping("/{roundId}")
    public RestResult<?> delete(@PathVariable("roundId") @NotNull Long roundId,
        @RequestBody @NotEmpty List<String> studentCodes)
    {
        service.delete(roundId, studentCodes);
        
        return RestResult.success();
    }
    
    /**
     * 条件删除可选课学生
     *
     * @param round
     * @return
     */
    @ApiOperation(value = "删除全部课程")
    @DeleteMapping("/deleteAll/{roundId}")
    public RestResult<?> deleteAll(@PathVariable("roundId") @NotNull Long roundId)
    {
        service.deleteAll(roundId);
        
        return RestResult.success();
    }
}
