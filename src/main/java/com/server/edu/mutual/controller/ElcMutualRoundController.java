package com.server.edu.mutual.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.dto.ElectionRoundsDto;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.vo.ElectionRoundsVo;
import com.server.edu.mutual.service.ElecMutualRoundService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "本研互选轮次管理", version = ""))
@RestSchema(schemaId = "elcMutualRoundController")
@RequestMapping("elcMutualRound")
public class ElcMutualRoundController {
	@Autowired
	private ElecMutualRoundService elecMutualRoundService;
    
    /**
     * 分页查询
     *
     * @param condition
     * @return
     */
    @ApiOperation(value = "分页查询轮次信息")
    @PostMapping("/page")
    public RestResult<PageResult<ElectionRoundsVo>> page(
        @RequestBody PageCondition<ElectionRounds> condition)
    {
        PageResult<ElectionRoundsVo> page =
        		elecMutualRoundService.listPage(condition);
        
        return RestResult.successData(page);
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
        @RequestBody @NotNull @Valid ElectionRoundsDto dto)
    {
    	elecMutualRoundService.add(dto);
        return RestResult.success();
    }
    
    @ApiOperation(value = "根据ID查询轮次信息")
    @GetMapping("/{roundId}")
    public RestResult<ElectionRoundsDto> get(
        @PathVariable("roundId") Long roundId)
    {
        ElectionRoundsDto electionRoundsDto = elecMutualRoundService.get(roundId);
        
        return RestResult.successData(electionRoundsDto);
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
        @RequestBody @NotNull @Valid ElectionRoundsDto dto)
    {
    	elecMutualRoundService.update(dto);
        
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
    	elecMutualRoundService.delete(ids);
        
        return RestResult.success();
    }
}
