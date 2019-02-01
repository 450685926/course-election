package com.server.edu.election.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.dto.ElectionRoundsDto;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.service.ElectionRoundService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "选课轮次管理", version = ""))
@RestSchema(schemaId = "ElectionRoundController")
@RequestMapping("electionRound")
public class ElectionRoundController
{
    @Autowired
    private ElectionRoundService electionRoundService;
    
    /**
     * 分页查询
     *
     * @param condition
     * @return
     */
    @ApiOperation(value = "分页查询轮次信息")
    @PostMapping("/page")
    public RestResult<PageResult<ElectionRounds>> page(
        @RequestBody PageCondition<ElectionRounds> condition)
    {
        PageResult<ElectionRounds> page = electionRoundService.listPage(condition);
        
        return RestResult.successData(page);
    }
    
    @ApiOperation(value = "根据ID查询轮次信息")
    @GetMapping("/{roundId}")
    public RestResult<ElectionRoundsDto> get(Long roundId){
        
        ElectionRoundsDto electionRoundsDto = electionRoundService.get(roundId);
        
        return RestResult.successData(electionRoundsDto);
    }
    
    /**
     * 添加轮次
     *
     * @param round
     * @return
     */
    @ApiOperation(value = "添加轮次")
    @PutMapping
    public RestResult<?> add(
        @RequestBody @NotNull @Valid ElectionRoundsDto round)
    {
        electionRoundService.add(round);
        
        return RestResult.success();
    }
    
    /**
     * 修改轮次
     *
     * @param round
     * @return
     */
    @ApiOperation(value = "修改轮次")
    @PostMapping
    public RestResult<?> update(
        @RequestBody @NotNull @Valid ElectionRoundsDto round)
    {
        electionRoundService.update(round);
        
        return RestResult.success();
    }
    
    /**
     * 删除轮次
     *
     * @param ids
     * @return
     */
    @ApiOperation(value = "删除轮次")
    @DeleteMapping
    public RestResult<?> delete(@RequestBody @NotEmpty List<Long> ids)
    {
        electionRoundService.delete(ids);
        
        return RestResult.success();
    }
}
