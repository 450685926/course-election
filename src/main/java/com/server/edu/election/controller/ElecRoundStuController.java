package com.server.edu.election.controller;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.hibernate.validator.constraints.NotEmpty;
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
import com.server.edu.common.rest.StudentInfo;
import com.server.edu.election.query.ElecRoundStuQuery;
import com.server.edu.election.service.ElecRoundStuService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "可选课学生管理", version = ""))
@RestSchema(schemaId = "ElecRoundStuController")
@RequestMapping("electionRoundStu")
public class ElecRoundStuController
{
    @Autowired
    private ElecRoundStuService elecRoundStuService;
    
    /**
     * 分页查询
     *
     * @param condition
     * @return
     */
    @ApiOperation(value = "分页查询可选课学生信息")
    @PostMapping("/page")
    public RestResult<PageResult<StudentInfo>> page(
        @RequestBody PageCondition<ElecRoundStuQuery> condition)
    {
        PageResult<StudentInfo> page = elecRoundStuService.listPage(condition);
        
        return RestResult.successData(page);
    }
    
    /**
     * 添加可选课学生
     *
     * @param round
     * @return
     */
    @ApiOperation(value = "添加可选课学生")
    @PutMapping("/{roundId}")
    public RestResult<?> add(@PathVariable("roundId") Long roundId,
        @RequestBody @NotNull List<String> studentCodes)
    {
        String add = elecRoundStuService.add(roundId, studentCodes);
        if(StringUtils.isNotBlank(add)) {
            add="以下学号"+add+"错误，没有添加";
        }
        return RestResult.success(add);
    }
    
    /**
     * 条件添加可选课学生
     *
     * @param round
     * @return
     */
    @ApiOperation(value = "条件添加可选课学生")
    @PutMapping("/addByCondition")
    public RestResult<?> addByCondition(
        @RequestBody @NotNull ElecRoundStuQuery condition)
    {
        elecRoundStuService.addByCondition(condition);
        
        return RestResult.success();
    }
    
    /**
     * 删除可选课学生
     *
     * @param ids
     * @return
     */
    @ApiOperation(value = "删除可选课学生")
    @DeleteMapping("/{roundId}")
    public RestResult<?> delete(@PathVariable("roundId") Long roundId,
        @RequestBody @NotEmpty List<String> studentCodes)
    {
        elecRoundStuService.delete(roundId, studentCodes);
        
        return RestResult.success();
    }
    /**
     * 条件删除可选课学生
     *
     * @param round
     * @return
     */
    @ApiOperation(value = "条件删除可选课学生")
    @PutMapping("/deleteByCondition")
    public RestResult<?> deleteByCondition(
        @RequestBody @NotNull ElecRoundStuQuery condition)
    {
        elecRoundStuService.deleteByCondition(condition);
        
        return RestResult.success();
    }
}
